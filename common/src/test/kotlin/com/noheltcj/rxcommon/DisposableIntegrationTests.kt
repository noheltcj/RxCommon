package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.subjects.BehaviorSubject
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class DisposableIntegrationTests {
  private lateinit var testObserver: TestObserver<Int>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenUpstreamIsColdAndHasObserver_whenObserverIsDisposed_shouldDisposeUpstream")
  fun `given upstream is cold and has observer, when observer is disposed, should dispose upstream`() {
    lateinit var upstreamEmitter: Emitter<Int>
    val upstream = Observable<Int> {
      upstreamEmitter = it
      Disposables.empty()
    }
    PublishSubject<Int>().apply {
      subscribeTo(upstream)
      subscribe(testObserver)
    }.onComplete()

    testObserver.assertDisposed()

    upstreamEmitter.next(1)

    /* Indirectly asserting disposed by subscribing to the cold upstream observable. */
    val upstreamTestObserver = TestObserver<Int>()
    upstream.subscribe(upstreamTestObserver)

    upstreamEmitter.next(2)

    upstreamTestObserver.assertNoEmission()
  }

  @Test
  @JsName("givenUpstreamIsColdAndHasTwoObservers_whenOneIsDisposed_shouldNotDisposeUpstream")
  fun `given upstream is cold and has two observers, when one is disposed, should not dispose upstream`() {
    lateinit var upstreamEmitter: Emitter<Int>
    val upstream = Observable<Int> {
      upstreamEmitter = it
      Disposables.empty()
    }
    val upstreamTestObserver = TestObserver<Int>()
    upstream.subscribe(upstreamTestObserver)
    PublishSubject<Int>().apply {
      subscribeTo(upstream)
      subscribe(testObserver)
    }.onComplete()

    testObserver.assertDisposed()

    upstreamTestObserver.assertComplete()
  }

  @Test
  @JsName("givenColdSourceWithAnObserver_whenAllObserversDisposed_andNewObserverAdded_shouldDisposeSource")
  fun `given cold source with an observer, when all observers disposed and a new observer is added, should dispose source`() {
    lateinit var capturedEmitter: Emitter<Int>
    Observable<Int> {
      capturedEmitter = it
      Disposables.empty()
    }.apply {
      val emptyObserver = NextObserver<Int> {}

      // Waking cold observable
      subscribe(emptyObserver)

      unsubscribe(emptyObserver)

      // Observing notifications and emissions
      subscribe(testObserver)
    }
    capturedEmitter.next(1)
    capturedEmitter.complete()
    capturedEmitter.terminate(RuntimeException())

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenHotSourceWithAnObserver_whenAllObserversDisposed_andNewObserverAdded_shouldNotDisposeSource")
  fun `given hot source with an observer, when all observers disposed and a new observer is added, should not dispose source`() {
    val emptyObserver = NextObserver<Int> {}
    BehaviorSubject(1).apply {
      subscribe(emptyObserver)
      unsubscribe(emptyObserver)
      subscribe(testObserver)
    }
    testObserver.assertNotDisposed()
    testObserver.assertValue(1)
  }
}