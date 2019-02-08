package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.exceptions.UndeliverableNotificationException
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.js.JsName
import kotlin.test.*

class SingleOperatorIntegrationTests {
    lateinit var testObserver: TestObserver<Int>

    @BeforeTest
    fun beforeEach() {
        testObserver = TestObserver()
    }

    @Test
    @JsName("givenSourceToSingle_whenSubscribing_shouldNotEmit")
    fun `given source to single, when subscribing, should emit nothing`() {
        Observable<Int>()
            .toSingle()
            .subscribe(testObserver)

        testObserver.assertNoEmission()
    }

    @Test
    @JsName("givenSourceToSingle_whenSourceEmits_shouldEmit")
    fun `given source to single, when the source emits, should emit`() {
        Observable(just = 10)
            .toSingle()
            .subscribe(testObserver)

        testObserver.assertValue(10)
    }

    @Test
    @JsName("givenSourceToSingle_whenSourceEmits_shouldNotifyComplete")
    fun `given source to single, when the source emits, should notify complete`() {
        val subject = PublishSubject<Int>()
        subject
            .toSingle()
            .subscribe(testObserver)

        subject.onNext(1)

        testObserver.assertComplete()
    }

    @Test
    @JsName("givenColdSourceToSingle_whenSourceEmitsMultiple_shouldOnlyEmitFirst")
    fun `given source to single, when the source emits multiple, should only emit first`() {
        try {
            Observable<Int>(createWithEmitter = { emitter ->
                emitter.next(1)
                emitter.next(2)
                Disposables.empty()
            })
                .toSingle()
                .subscribe(testObserver)
        } catch (t: Throwable) { }

        testObserver.assertValues(listOf(1))
    }

    @Test
    @JsName("givenColdSourceToSingle_whenSourceEmitsMultiple_shouldThrow")
    fun `given source to single, when the source emits multiple, should throw`() {
        var resultThrowable: Throwable? = null
        try {
            Observable<Int>(createWithEmitter = { emitter ->
                emitter.next(1)
                emitter.next(2)
                Disposables.empty()
            })
                .toSingle()
                .subscribe(testObserver)
        } catch (t: Throwable) {
            resultThrowable = t
        }

        assertNotNull(resultThrowable)
        assertTrue(resultThrowable is UndeliverableEmissionException)
    }

    @Test
    @JsName("givenSourceToSingle_whenSourceTerminates_shouldNotify")
    fun `given source to single, when the source terminates, should notify`() {
        val expectedThrowable = Throwable("poof")
        Observable<Int>(error = expectedThrowable)
            .toSingle()
            .subscribe(testObserver)

        testObserver.assertTerminated(expectedThrowable)
    }

    @Test
    @JsName("givenSourceToSingle_whenSourceCompletes_shouldThrow")
    fun `given source to single, when the source completes, should throw`() {
        var resultThrowable: Throwable? = null
        try {
            Observable<Int>(completeOnSubscribe = true)
                .toSingle()
                .subscribe(testObserver)
        } catch (t: Throwable) {
            resultThrowable = t
        }

        assertNotNull(resultThrowable)
        assertTrue(resultThrowable is UndeliverableNotificationException)
        assertEquals(UndeliverableNotificationException.Notification.Completed, resultThrowable.notification)
    }

    @Test
    @JsName("givenColdSourceToSingle_whenOnlyObserverDisposed_shouldDisposeSource")
    fun `given cold source to single, when the only observer disposed, should dispose source`() {
        val source = Observable<Int>()
        source.toSingle()
            .subscribe(testObserver)
            .dispose()

        val sourceTestObserver = TestObserver<Int>()
        source.subscribe(sourceTestObserver)

        sourceTestObserver.assertDisposed()
    }
}
