package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class FilterIntegrationTests {
    lateinit var testObserver: TestObserver<Int>

    @BeforeTest
    fun beforeEach() {
        testObserver = TestObserver()
    }

    @Test
    @JsName("givenSourceFiltered_whenSubscribing_shouldNotEmit")
    fun `given source filtered, when subscribing, should emit nothing`() {
        Observable<Int>()
            .filter { true }
            .subscribe(testObserver)

        testObserver.assertNoEmission()
    }

    @Test
    @JsName("givenSourceFiltered_whenSourceEmits_andKeepReturnsTrue_shouldEmit")
    fun `given source filtered, when the source emits and keep returns true, should emit`() {
        Observable(just = 10)
            .filter { true }
            .subscribe(testObserver)

        testObserver.assertValue(10)
    }

    @Test
    @JsName("givenSourceFiltered_whenSourceEmits_andKeepReturnsFalse_shouldEmit")
    fun `given source filtered, when the source emits and keep returns false, should not emit`() {
        val subject = PublishSubject<Int>()
        subject
            .filter { false }
            .subscribe(testObserver)

        subject.onNext(1)

        testObserver.assertNoEmission()
    }

    @Test
    @JsName("givenSourceFiltered_whenSourceEmitsMultiple_andKeepReturnsTrueForSome_shouldEmitSome")
    fun `given source filtered, when the source emits and keep returns true for some, should emit some`() {
        Observable<Int>(createWithEmitter = { emitter ->
            emitter.next(3)
            emitter.next( 7)
            emitter.next(4)
            emitter.next(9)
            Disposables.empty()
        })
            .filter { it > 5 }
            .subscribe(testObserver)

        testObserver.assertValues(listOf(7, 9))
    }

    @Test
    @JsName("givenSourceFiltered_whenSourceTerminates_shouldNotify")
    fun `given source filtered, when the source terminates, should notify`() {
        val expectedThrowable = Throwable("poof")
        Observable<Int>(error = expectedThrowable)
            .filter { true }
            .subscribe(testObserver)

        testObserver.assertTerminated(expectedThrowable)
    }

    @Test
    @JsName("givenSourceFiltered_whenSourceCompletes_shouldNotify")
    fun `given source filtered, when the source completes, should notify`() {
        Observable<Int>(completeOnSubscribe = true)
            .filter { true }
            .subscribe(testObserver)

        testObserver.assertComplete()
    }

    @Test
    @JsName("givenColdSourceFiltered_whenOnlyObserverDisposed_shouldDisposeSource")
    fun `given cold source filtered, when the only observer disposed, should dispose source`() {
        val source = Observable<Int>()
        source.filter { false }
            .subscribe(testObserver)
            .dispose()

        val sourceTestObserver = TestObserver<Int>()
        source.subscribe(sourceTestObserver)

        sourceTestObserver.assertDisposed()
    }
}