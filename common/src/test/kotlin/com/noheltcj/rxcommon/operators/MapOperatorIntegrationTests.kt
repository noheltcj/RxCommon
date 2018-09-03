package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class MapOperatorIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenSourceMapped_whenSubscribing_shouldNotEmit")
  fun `given source mapped, when subscribing should emit nothing`() {
    Observable<Int>()
        .map { it.toString() }
        .subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSourceMapped_whenSourceEmits_shouldEmitMappedValue")
  fun `given source mapped, when the source emits, should emit mapped value`() {
    Observable(just = 10)
        .map { it.toString() }
        .subscribe(testObserver)

    testObserver.assertValue("10")
  }

  @Test
  @JsName("givenSourceMapped_whenSourceTerminates_shouldNotify")
  fun `given source mapped, when the source terminates, should notify`() {
    val expectedThrowable = Throwable("poof")
    Observable<Int>(error = expectedThrowable)
        .map { it.toString() }
        .subscribe(testObserver)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenSourceMapped_whenSourceCompletes_shouldNotify")
  fun `given source mapped, when the source completes, should notify`() {
    Observable<Int>(completeOnSubscribe = true)
        .map { it.toString() }
        .subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenDisposableSourceMapped_whenSourceDisposed_shouldNotify")
  fun `given disposable source mapped, when the source disposes, should notify`() {
    val source = PublishSubject<Int>()
    source.map { it.toString() }.subscribe(testObserver)
    source.dispose()

    testObserver.assertDisposed()
  }

  @Test
  @JsName("givenColdSourceMapped_whenOnlyObserverDisposed_shouldDisposeSource")
  fun `given cold source mapped, when the only observer disposed, should dispose source`() {
    lateinit var sourceEmitter: Emitter<Int>
    val source = Observable<Int>(createWithEmitter = {
      sourceEmitter = it
      Disposables.empty()
    })
    source.map { it.toString() }
        .subscribe(testObserver)
        .dispose()

    val sourceTestObserver = TestObserver<Int>()
    source.subscribe(sourceTestObserver)

    sourceEmitter.next(1)

    sourceTestObserver.assertNoEmission()
  }

  @Test
  @JsName("givenHotSourceMapped_whenOnlyObserverDisposed_shouldDisposeOperator")
  fun `given hot source mapped, when the only observer disposed, should dispose operator`() {
    val source = PublishSubject<Int>()
    source.map { it.toString() }.apply {
      val emptyObserver = NextObserver<String> {}
      subscribe(emptyObserver)
      unsubscribe(emptyObserver)
      subscribe(testObserver)
    }

    source.publish(1)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenHotSourceMapped_whenOnlyObserverDisposed_shouldNotDisposeSource")
  fun `given hot source mapped, when the only observer disposed, should not dispose source`() {
    val source = PublishSubject<Int>()
    source.map { it.toString() }.apply {
      val emptyObserver = NextObserver<String> {}
      subscribe(emptyObserver)
      unsubscribe(emptyObserver)
    }

    val sourceTestObserver = TestObserver<Int>()
    source.subscribe(sourceTestObserver)

    source.publish(1)

    sourceTestObserver.assertValue(1)
  }
}