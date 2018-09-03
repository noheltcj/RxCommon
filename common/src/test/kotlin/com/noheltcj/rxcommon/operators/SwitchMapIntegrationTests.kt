package com.noheltcj.rxcommon.operators

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

class SwitchMapIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenSwitchMap_whenSubscribing_shouldNotEmit")
  fun `given switchMap, when subscribing, should not emit`() {
    Observable<Int>()
        .switchMap { Observable(just = "") }
        .subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToSwitchMap_whenOriginalSourceEmits_shouldNotEmit")
  fun `given subscribed to switchMap, when original source emits, should not emit`() {
    val source = PublishSubject<Int>()
    source.switchMap { Observable<String>() }
        .subscribe(testObserver)

    source.publish(1)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToSwitchMap_andOriginalSourceEmitted_whenNewSourceEmits_shouldEmit")
  fun `given subscribed to switchMap and original source has emitted, when new source emits, should emit`() {
    Observable(just = 1)
        .switchMap { Observable(just = "hi") }
        .subscribe(testObserver)

    testObserver.assertValue("hi")
  }

  @Test
  @JsName("givenSubscribedToSwitchMap_andBothOriginalAndNewSourcesEmitted_whenNewSourceEmitsAgain_shouldEmit")
  fun `given subscribed to switchMap and both original and new sources emitted, when new source emits again, should emit`() {
    val newSource = PublishSubject<String>()
    Observable(just = 1)
        .switchMap { integer ->
          newSource.map { "$integer - $it" }
        }
        .subscribe(testObserver)

    newSource.publish("one")
    newSource.publish("two")

    testObserver.assertValues(listOf("1 - one", "1 - two"))
  }

  @Test
  @JsName("givenSwitchedToNewSource_whenSwitchingToNewSeededSource_shouldEmit")
  fun `given switched to new source, when switching to new seeded source, should emit`() {
    val observables = arrayOf(Observable(just = "1"), Observable(just = "2"))
    val originalSource = BehaviorSubject(seed = 0)

    originalSource
        .switchMap { observables[it] }
        .subscribe(testObserver)

    originalSource.publish(1)

    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  @JsName("givenSubscribedToSwitchMap_andSwitchedFromFirstNewSource_whenOldSourceEmits_shouldNotEmit")
  fun `given subscribed to switch map and switched from first new source, when old source emits, should not emit`() {
    val sourceOne = PublishSubject<String>()
    val observables = arrayOf(sourceOne, Observable<String>())
    val originalSource = BehaviorSubject(seed = 0)

    originalSource
        .switchMap { observables[it] }
        .subscribe(testObserver)

    originalSource.publish(1)
    sourceOne.publish("2")

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenUpstreamSourceTerminated_whenSubscribing_shouldNotify")
  fun `given upstream source terminated, when subscribing, should notify`() {
    val expectedThrowable = Throwable("source")

    Observable<String>(error = expectedThrowable)
        .switchMap { Observable(just = it) }
        .subscribe(testObserver)

    testObserver.assertValues(emptyList())
    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenUpstreamSourceCompleted_whenSubscribing_shouldNotify")
  fun `given upstream source completed, when subscribing, should notify`() {
    Observable<String>(completeOnSubscribe = true)
        .switchMap { Observable(just = it) }
        .subscribe(testObserver)

    testObserver.assertValues(emptyList())
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToFlatMap_whenTheSourceDisposes_shouldNotify")
  fun `given subscribed to flatMap, when the source disposes, should notify`() {
    val source = PublishSubject<String>()
    source.switchMap { Observable(just = it) }
        .subscribe(testObserver)

    source.dispose()

    testObserver.assertValues(emptyList())
    testObserver.assertDisposed()
  }

  @Test
  @JsName("givenSourceEmitted_andNewSourceTerminated_whenSubscribing_shouldNotify")
  fun `given source emitted and new source terminated, when subscribing, should notify`() {
    val expectedThrowable = Throwable("flatMap error")
    Observable(just = 1)
        .switchMap { Observable<String>(error = expectedThrowable) }
        .subscribe(testObserver)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenSourceEmitted_andNewSourceCompleted_whenSubscribing_shouldNotify")
  fun `given source emitted and new source completed, when subscribing, should notify`() {
    Observable(just = 1)
        .switchMap { Observable<String>(completeOnSubscribe = true) }
        .subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToSwitchMap_whenAllDisposed_shouldNotify")
  fun `given subscribed to switchMap, when all disposed, should notify`() {
    Observable<String>()
        .switchMap { Observable<String>() }
        .subscribe(testObserver)
        .dispose()

    testObserver.assertDisposed()
  }


  @Test
  @JsName("givenSubscribedToSwitchMap_whenAllDisposed_shouldDisposeUpstream")
  fun `given subscribed to switchMap from cold upstream, when all disposed, should dispose upstream`() {
    lateinit var emitter: Emitter<String>
    val upstream = Observable<String>(createWithEmitter = {
      emitter = it
      Disposables.empty()
    })

    upstream
        .switchMap { Observable<String>() }
        .subscribe(NextObserver {})
        .dispose()

    upstream.subscribe(testObserver)
    emitter.next("already disposed")

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToSwitchMapOfNewColdSource_andOriginalSourceEmitted_whenAllDisposed_shouldDisposeNewSource")
  fun `given subscribed to switchMap of new cold source and original source emitted, when all disposed, should dispose new source`() {
    lateinit var emitter: Emitter<String>
    val newSource = Observable<String>(createWithEmitter = {
      emitter = it
      Disposables.empty()
    })
    Observable(just = "original")
        .switchMap { newSource }
        .subscribe(NextObserver {})
        .dispose()

    newSource.subscribe(testObserver)
    emitter.next("already disposed")

    testObserver.assertNoEmission()
  }
}