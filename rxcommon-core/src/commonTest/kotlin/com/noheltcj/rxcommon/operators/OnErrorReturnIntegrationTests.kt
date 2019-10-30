package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.observables.Single
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OnErrorReturnIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenSourceOnError_whenSubscribing_shouldNotEmit")
  fun `given source to on error, when subscribing should emit nothing`() {
    Observable<String>()
        .onErrorReturn { Observable("") }
        .subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSourceOnError_whenSourceEmits_shouldEmitOriginalValue")
  fun `given source to on error, when the source emits, should emit original value`() {
    Observable(just = "ten")
        .onErrorReturn { Observable("not ten") }
        .subscribe(testObserver)

    testObserver.assertValue("ten")
  }

  @Test
  @JsName("givenSourceOnError_whenSourceTerminates_shouldResolveToNewSource")
  fun `given source to on error, when the source terminates, should resolve to new source`() {
    val expectedThrowable = Throwable("poof")
    Observable<String>(error = expectedThrowable)
        .onErrorReturn { Observable("not poof") }
        .subscribe(testObserver)

    testObserver.assertValue("not poof")
  }

  @Test
  @JsName("givenSourceOnError_whenSourceCompletes_shouldNotify")
  fun `given source to on error, when the source completes, should notify`() {
    Observable<String>(completeOnSubscribe = true)
        .onErrorReturn { Single("") }
        .subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenDisposableSourceOnError_whenSourceCompletes_shouldNotify")
  fun `given disposable source to on error, when the source completes, should notify`() {
    val source = PublishSubject<String>()
    source.onErrorReturn { Single("") }.subscribe(testObserver)
    source.onComplete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSourceOnError_whenSourceTerminates_shouldNotEmit")
  fun `given source to on error, when the source terminates, should not emit`() {
    val source = PublishSubject<String>()
    source.onErrorReturn { Single() }.subscribe(testObserver)
    source.onError(RuntimeException())

    testObserver.assertNoEmission()
    testObserver.assertNotTerminated()
  }

  @Test
  @JsName("givenSourceOnError_whenSourceTerminates_shouldResolveNewSourceWithUpstreamThrowable")
  fun `given source to on error, when the source terminates, should resolve new source upstream throwable`() {
    val expectedThrowable = RuntimeException("should be me")
    var capturedUpstreamThrowable: Throwable? = null
    val source = PublishSubject<String>()
    source.onErrorReturn {
      capturedUpstreamThrowable = it
      Single()
    }.subscribe(testObserver)
    source.onError(expectedThrowable)

    assertEquals(expectedThrowable, capturedUpstreamThrowable)
  }

  @Test
  @JsName("givenOriginalSourceTerminatedWithOnError_whenResolvedSourceEmits_shouldEmit")
  fun `given original source terminated with on error, when resolved source emits, should emit`() {
    val source = PublishSubject<String>()
    source.onErrorReturn { Single(just = "") }.subscribe(testObserver)
    source.onError(RuntimeException())

    testObserver.assertValue("")
  }

  @Test
  @JsName("givenOriginalSourceTerminatedWithOnError_whenResolvedSourceTerminates_shouldNotify")
  fun `given original source terminated with on error, when resolved source terminates, should notify`() {
    val expectedThrowable = RuntimeException("woops")
    val source = PublishSubject<String>()
    source.onErrorReturn { Single(error = expectedThrowable) }.subscribe(testObserver)
    source.onError(RuntimeException())

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenOriginalSourceTerminatedWithOnError_whenResolvedSourceCompletes_shouldNotify")
  fun `given original source terminated with on error, when resolved source completes, should notify`() {
    val source = PublishSubject<String>()
    source.onErrorReturn { Observable(completeOnSubscribe = true) }.subscribe(testObserver)
    source.onError(RuntimeException())

    testObserver.assertComplete()
  }

  @Test // Test added for posterity's sake
  @JsName("givenOriginalSourceEmittedAndFlatMapped_andResolvedSourceTerminatedWithOnError_shouldNotDisposeFlatMapOrOriginalSource")
  fun `given original source emitted and flatmapped and resolved source terminated with on error, should not dispose flatmap or original source`() {
    lateinit var emitter: Emitter<String>
    val flatmapTestObserver = TestObserver<String>()
    val source = Observable<String>(createWithEmitter = {
      emitter = it
      Disposables.empty()
    })
    source.subscribe(testObserver)
    source.flatMap {
      Observable<String>(error = RuntimeException())
          .onErrorReturn { Single(just = "hmm") }
    }.subscribe(flatmapTestObserver)
    emitter.next("yup")

    testObserver.assertNotDisposed()
    flatmapTestObserver.assertNotDisposed()
    flatmapTestObserver.assertValue("hmm")
  }

  @Test
  @JsName("givenColdSourceOnError_whenOnlyObserverDisposed_shouldDisposeSource")
  fun `given cold source to on error, when the only observer disposed, should dispose source`() {
    val source = Observable<String>()
    source.onErrorReturn { Single("hi") }
        .subscribe(testObserver)
        .dispose()

    val sourceTestObserver = TestObserver<String>()
    source.subscribe(sourceTestObserver)

    sourceTestObserver.assertDisposed()
  }

  @Test
  @JsName("givenHotSourceOnError_whenOnlyObserverDisposed_shouldDisposeOperator")
  fun `given hot source to on error, when the only observer disposed, should dispose operator`() {
    val source = PublishSubject<String>()
    source.onErrorReturn { Single("no") }.apply {
      subscribe(TestObserver()).dispose()
      subscribe(testObserver)
    }

    testObserver.assertDisposed()
  }

  @Test
  @JsName("givenHotSourceOnError_whenOnlyObserverDisposed_shouldNotDisposeSource")
  fun `given hot source to on error, when the only observer disposed, should not dispose source`() {
    val source = PublishSubject<String>()
    source.onErrorReturn { Single("yes") }.apply {
      val emptyObserver = NextObserver<String> {}
      subscribe(emptyObserver).dispose()
    }

    val sourceTestObserver = TestObserver<String>()
    source.subscribe(sourceTestObserver)

    source.onNext("one")

    sourceTestObserver.assertValue("one")
  }
}