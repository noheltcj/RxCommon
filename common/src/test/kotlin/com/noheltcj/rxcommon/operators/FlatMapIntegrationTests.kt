package com.noheltcj.rxcommon.operators

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
  fun `given flatmapped source, when subscribing, should not emit`() {
    Observable<Int>()
        .flatMap { Observable(just = "") }
        .subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToFlatMappedEmptySource_whenUpstreamEmits_shouldNotEmit")
  fun `given subscribed to flatmapped empty source, when upstream emits, should not emit`() {
    val source = PublishSubject<Int>()
    source.flatMap { Observable<String>() }
        .subscribe(testObserver)

    source.onNext(1)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenSubscribedToFlatMappedSource_andUpstreamEmitted_whenNewSourceEmits_shouldEmitNewSourceValue")
  fun `given subscribed to flatmapped source and upstream has emitted, when new source emits, should emit value of new emission`() {
    Observable(just = 1)
        .flatMap { Observable(just = "hi") }
        .subscribe(testObserver)

    testObserver.assertValue("hi")
  }

  @Test
  @JsName("givenSubscribedToFlatMappedSource_andBothUpstreamAndNewSourceEmitted_whenNewSourceEmitsAgain_shouldEmitValueOfNewEmission")
  fun `given subscribed to flatmapped source and both upstream and new source emitted when new source emits again should emit new emission`() {
    val newSource = PublishSubject<String>()
    Observable(just = 1)
        .flatMap { integer ->
          newSource.map { "$integer - $it" }
        }
        .subscribe(testObserver)

    newSource.onNext("one")
    newSource.onNext("two")

    testObserver.assertValues(listOf("1 - one", "1 - two"))
  }

  @Test
  @JsName("givenUpstreamSourceFlatMappedToNewSeededSource_andOriginalSourceEmitted_whenOriginalSourceEmitsAgain_shouldEmitSecondSourceValue")
  fun `given upstream source flatmapped to a seeded source and original source has emitted, when the original source emits again, should emit second source value`() {
    val observables = arrayOf(Observable(just = "1"), Observable(just = "2"))
    val originalSource = BehaviorSubject(seed = 0)

    originalSource
        .flatMap { observables[it] }
        .subscribe(testObserver)

    originalSource.onNext(1)

    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  @JsName("givenSubscribedToFlatMap_andResolvedSecondNewSource_whenFirstSourceEmits_shouldEmit")
  fun `given subscribed to flatmap and resolved second new source, when first source emits, should emit`() {
    val sourceOne = PublishSubject<String>()
    val observables = arrayOf(sourceOne, Observable<String>())
    val originalSource = BehaviorSubject(seed = 0)

    originalSource
        .flatMap { observables[it] }
        .subscribe(testObserver)

    sourceOne.onNext("2")

    originalSource.onNext(1)

    testObserver.assertValue("2")
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
        .flatMap { Observable<String>() }
        .subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenUpstreamSourceEmitted_andUpstreamIsNotComplete_whenNewSourceCompletes_shouldNotNotify")
  fun `given upstream source emitted and upstream is not complete, when the new source completes, should not notify`() {
    val source = PublishSubject<String>()

    source.flatMap { Observable(just = it) }
        .subscribe(testObserver)

    source.onNext("1")

    testObserver.assertValue("1")
    testObserver.assertNotComplete()
  }

  @Test
  @JsName("givenUpstreamSourceEmitted_andUpstreamIsComplete_whenNewSourceCompletes_shouldNotify")
  fun `given upstream source emitted and upstream is complete, when the new source completes, should notify`() {
    val newSource = PublishSubject<String>()

    Observable(just = 1).flatMap { newSource }
        .subscribe(testObserver)

    newSource.onComplete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenUpstreamSourceEmitted_andNewSourceNotCompleted_whenTheUpstreamCompletes_shouldNotNotify")
  fun `given upstream source emitted and new source not completed, when the upstream completes, should not notify`() {
    val source = PublishSubject<Int>()

    source.flatMap { Observable<String>() }
        .subscribe(testObserver)

    source.onNext(1)
    source.onComplete()

    testObserver.assertNotComplete()
  }

  @Test
  @JsName("givenUpstreamSourceEmitted_andFirstNewSourceCompleted_andSecondNewSourceIncomplete_whenTheUpstreamCompletes_shouldNotNotify")
  fun `given upstream source emitted and first new source completed and second new source incomplete, when the upstream completes, should not notify`() {
    val source = PublishSubject<Int>()
    val observables: List<Observable<String>> = listOf(Observable(completeOnSubscribe = true), Observable())

    source.flatMap {
      observables[it]
    }.subscribe(testObserver)

    source.onNext(0)
    source.onNext(1)
    source.onComplete()

    testObserver.assertNotComplete()
  }

  @Test
  @JsName("givenMultipleCompletedSourcesFlatMapped_whenUpstreamCompletes_shouldNotify")
  fun `given multiple completed sources flatmapped, when the upstream completes, should notify`() {
    val source = PublishSubject<Int>()

    source.flatMap { Observable<String>(completeOnSubscribe = true) }
        .subscribe(testObserver)

    source.onNext(1)
    source.onNext(2)
    source.onComplete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenMultipleIncompleteSourcesFlatMapped_andUpstreamCompleted_whenNewSourcesComplete_shouldNotify")
  fun `given multiple incomplete sources flatmapped and upstream completed, when new sources complete, should notify`() {
    val source = PublishSubject<Int>()
    val flatmappedSources = mutableListOf<PublishSubject<String>>()

    source.flatMap { _ ->
      PublishSubject<String>().also {
        flatmappedSources.add(it)
      }
    }.subscribe(testObserver)

    source.onNext(1)
    source.onNext(2)
    source.onComplete()

    flatmappedSources.forEach(PublishSubject<String>::onComplete)

    testObserver.assertComplete()
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
  @JsName("givenSubscribedToFlatMap_whenAllDisposed_shouldNotify")
  fun `given subscribed to flatmap, when all disposed, should notify`() {
    Observable<String>()
        .flatMap { Observable<String>() }
        .apply {
          subscribe(NextObserver {}).dispose()
          subscribe(testObserver)
        }

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToFlatMapFromColdUpstream_whenAllDisposed_shouldDisposeUpstream")
  fun `given subscribed to flatmap from cold upstream, when all disposed, should dispose upstream`() {
    val upstream = Observable<String>()
    upstream
        .flatMap { Observable<String>() }
        .subscribe(NextObserver {})
        .dispose()

    upstream.subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToFlatMapFromColdUpstream_andUpstreamEmitted_whenAllDisposed_shouldCompleteNewSource")
  fun `given subscribed to flatmap from cold upstream and upstream emitted, when all disposed, should complete new source`() {
    val newSource = Observable<String>()
    Observable(just = "original")
        .flatMap { newSource }
        .subscribe(NextObserver {})
        .dispose()

    newSource.subscribe(testObserver)

    testObserver.assertComplete()
  }
}