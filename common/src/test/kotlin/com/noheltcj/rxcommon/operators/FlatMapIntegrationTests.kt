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

class FlatMapIntegrationTests {
  private lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenFlatMappedSource_whenSubscribing_shouldNotEmit")
  fun `given flatMapped source, when subscribing, should not emit`() {
    Observable<Int>()
        .flatMap { Observable(just = "") }
        .subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToFlatMappedSource_whenUpstreamEmits_shouldNotEmit")
  fun `given subscribed to flatMapped source, when upstream emits, should not emit`() {
    val source = PublishSubject<Int>()
    source.flatMap { Observable<String>() }
        .subscribe(testObserver)

    source.publish(1)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToFlatMappedSource_andUpstreamEmitted_whenNewSourceEmits_shouldEmitNewSourceValue")
  fun `given subscribed to flatMapped source and upstream has emitted, when new source emits, should emit value of new emission`() {
    Observable(just = 1)
        .flatMap { Observable(just = "hi") }
        .subscribe(testObserver)

    testObserver.assertValue("hi")
  }

  @Test
  @JsName("givenSubscribedToFlatMappedSource_andBothUpstreamAndNewSourceEmitted_whenNewSourceEmitsAgain_shouldEmitValueOfNewEmission")
  fun `given subscribed to flatMapped source and both upstream and new source emitted when new source emits again should emit new emission`() {
    val newSource = PublishSubject<String>()
    Observable(just = 1)
        .flatMap { integer ->
          newSource.map { "$integer - $it" }
        }
        .subscribe(testObserver)

    newSource.publish("one")
    newSource.publish("two")

    testObserver.assertValues(listOf("1 - one", "1 - two"))
  }

  @Test
  @JsName("givenUpstreamSourceFlatMappedToNewSeededSource_andOriginalEmitted_whenOriginalSourceEmitsAgain_shouldEmitSecondSourceValue")
  fun `given upstream source flatMapped to a seeded source and original source has emitted, when the original source emits again, should emit second source value`() {
    val observables = arrayOf(Observable(just = "1"), Observable(just = "2"))
    val originalSource = BehaviorSubject(seed = 0)

    originalSource
        .flatMap { observables[it] }
        .subscribe(testObserver)

    originalSource.publish(1)

    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  @JsName("givenUpstreamSourceTerminated_whenSubscribing_shouldNotify")
  fun `given upstream source terminated, when subscribing, should notify`() {
    val expectedThrowable = Throwable("source")

    Observable<String>(error = expectedThrowable)
        .flatMap { Observable(just = it) }
        .subscribe(testObserver)

    testObserver.assertValues(emptyList())
    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenUpstreamSourceCompleted_whenSubscribing_shouldNotify")
  fun `given upstream source completed, when subscribing, should notify`() {
    Observable<String>(completeOnSubscribe = true)
        .flatMap { Observable(just = it) }
        .subscribe(testObserver)

    testObserver.assertValues(emptyList())
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToFlatMap_whenTheSourceDisposes_shouldNotify")
  fun `given subscribed to flatMap, when the source disposes, should notify`() {
    val source = PublishSubject<String>()
    source.flatMap { Observable(just = it) }
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
        .flatMap { Observable<String>(error = expectedThrowable) }
        .subscribe(testObserver)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenSourceEmitted_andNewSourceCompleted_whenSubscribing_shouldNotify")
  fun `given source emitted and new source completed, when subscribing, should notify`() {
    Observable(just = 1)
        .flatMap { Observable<String>(completeOnSubscribe = true) }
        .subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToFlatMapAndOriginalSourceEmitted_whenNewSourceDisposed_shouldNotify")
  fun `given subscribed to flatMap and original source emitted, when new source disposed, should notify`() {
    val newSource = PublishSubject<String>()
    Observable(just = 1)
        .flatMap { newSource }
        .subscribe(testObserver)

    newSource.dispose()

    testObserver.assertDisposed()
  }

  @Test
  @JsName("givenSubscribedToFlatMap_whenAllDisposed_shouldNotify")
  fun `given subscribed to flatMap, when all disposed, should notify`() {
    Observable<String>()
        .flatMap { Observable<String>() }
        .subscribe(testObserver)
        .dispose()

    testObserver.assertDisposed()
  }


  @Test
  @JsName("givenSubscribedToFlatMap_whenAllDisposed_shouldDisposeUpstream")
  fun `given subscribed to flatMap from cold upstream, when all disposed, should dispose upstream`() {
    lateinit var emitter: Emitter<String>
    val upstream = Observable<String>(createWithEmitter = {
      emitter = it
      Disposables.empty()
    })

    upstream
        .flatMap { Observable<String>() }
        .subscribe(NextObserver {})
        .dispose()

    upstream.subscribe(testObserver)
    emitter.next("already disposed")

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToFlatMapOfNewColdSource_andOriginalSourceEmitted_whenAllDisposed_shouldDisposeNewSource")
  fun `given subscribed to flatMap of new cold source and original source emitted, when all disposed, should dispose new source`() {
    lateinit var emitter: Emitter<String>
    val newSource = Observable<String>(createWithEmitter = {
      emitter = it
      Disposables.empty()
    })
    Observable(just = "original")
        .flatMap { newSource }
        .subscribe(NextObserver {})
        .dispose()

    newSource.subscribe(testObserver)
    emitter.next("already disposed")

    testObserver.assertNoEmission()
  }
}