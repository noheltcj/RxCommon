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

class CombineLatestIntegrationTests {
  lateinit var testObserver: TestObserver<Pair<Int, String>>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenTwoCombinedColdSources_whenSubscribing_shouldEmitNothing")
  fun `given two combined cold sources, when subscribing, should emit nothing`() {
    val combinedObs = Observable<Int>().combineLatest(Observable<String>())

    combinedObs.subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenTwoCombinedColdSources_andOnlyOneHasEmitted_whenSubscribing_shouldEmitNothing")
  fun `given two combined cold sources and only one has emitted, when subscribing, should emit nothing`() {
    val combinedObs = Observable(just = 1).combineLatest(Observable<String>())

    combinedObs.subscribe(testObserver)

    testObserver.assertValues(emptyList())
  }

  @Test
  @JsName("givenTwoCombinedColdSources_andBothHaveEmitted_whenSubscribing_shouldEmit")
  fun `given two combined cold sources and both have emitted, when subscribing, should emit`() {
    val combinedObs = Observable(just = 1).combineLatest(Observable(just = "1"))

    combinedObs.subscribe(testObserver)

    testObserver.assertValue(1 to "1")
  }

  @Test
  @JsName("givenTwoCombinedColdSources_andBothHaveEmittedNull_whenSubscribing_shouldEmit")
  fun `given two combined cold sources and both have emitted null, when subscribing, should emit`() {
    val nullableCombineTestObserver = TestObserver<Pair<Any?, String?>>()
    val combinedObs = Observable<Any?>(just = null).combineLatest(Observable<String?>(just = null))

    combinedObs.subscribe(nullableCombineTestObserver)

    nullableCombineTestObserver.assertValue(null to null)
  }

  @Test
  @JsName("givenSubscribedToCombinedColdSources_andBothHaveEmitted_whenOneEmitsAgain_shouldEmitTwoValues")
  fun `given subscribed to combined cold sources and both have emitted, when one emits again, should emit two values`() {
    lateinit var emitter: Emitter<String>
    val stringObs = Observable<String>(createWithEmitter = {
      it.next("1")
      emitter = it
      Disposables.empty()
    })

    Observable(just = 1).combineLatest(stringObs).subscribe(testObserver)

    emitter.next("2")

    testObserver.assertValues(listOf(
        1 to "1",
        1 to "2"
    ))
  }

  @Test
  @JsName("givenSubscribedToCombinedColdSources_andBothHaveEmitted_whenBothEmitAgain_shouldEmitThreeValues")
  fun `given subscribed to two combined cold sources and both have emitted, when both emit again, should emit three values`() {
    lateinit var emitterOne: Emitter<Int>
    lateinit var emitterTwo: Emitter<String>

    val intObs = Observable<Int>(createWithEmitter = {
      it.next(1)
      emitterOne = it
      Disposables.empty()
    })
    val stringObs = Observable<String>(createWithEmitter = {
      it.next("1")
      emitterTwo = it
      Disposables.empty()
    })

    intObs.combineLatest(stringObs).subscribe(testObserver)

    emitterOne.next(2)
    emitterTwo.next("2")

    testObserver.assertValues(listOf(
        1 to "1",
        2 to "1",
        2 to "2"
    ))
  }

  @Test
  @JsName("givenSubscribedToCombinationOfColdSourcesWithNoOtherObservers_whenSubscriptionDisposed_shouldDisposeUpstream")
  fun `given subscribed to combination of cold sources with no other observers, when subscription disposed, should dispose upstream`() {
    val intSource = Observable<Int>()
    val stringSource = Observable<String>()

    val combinedObservable = intSource.combineLatest(stringSource)

    combinedObservable.subscribe(NextObserver {}).dispose()

    val intSourceTestObserver = TestObserver<Int>()
    val stringSourceTestObserver = TestObserver<String>()

    intSource.subscribe(intSourceTestObserver)
    stringSource.subscribe(stringSourceTestObserver)
    combinedObservable.subscribe(testObserver)

    intSourceTestObserver.assertComplete()
    stringSourceTestObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToHotAndColdSources_andNoOtherObservers_whenSubscriptionDisposed_shouldDisposeColdSources")
  fun `given subscribed to hot and cold sources and no other observers, when subscription disposed, should complete cold sources`() {
    val coldSource = Observable<Int>()
    val hotSource = PublishSubject<String>()

    val combinedObservable = coldSource.combineLatest(hotSource)

    combinedObservable.subscribe(NextObserver {}).dispose()

    val coldSourceTestObserver = TestObserver<Int>()

    coldSource.subscribe(coldSourceTestObserver)

    coldSourceTestObserver.assertComplete()
  }

  @Test
  @JsName("givenCombinedSources_andOneSourceTerminated_whenSubscribing_shouldTerminateOperator")
  fun `given combined sources and one source is terminated, when subscribing, should terminate operator`() {
    val expectedThrowable = Throwable("bam")

    Observable<Int>()
        .combineLatest(Observable<String>(error = expectedThrowable))
        .subscribe(testObserver)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenCombinedSources_andOneSourceCompleted_whenSubscribing_shouldNotComplete")
  fun `given combined sources and one source is completed, when subscribing, should not complete`() {
    Observable<Int>()
        .combineLatest(Observable<String>(completeOnSubscribe = true))
        .subscribe(testObserver)

    testObserver.assertNotComplete()
  }

  @Test
  @JsName("givenCombinedSources_andBothSourcesCompleted_whenSubscribing_shouldComplete")
  fun `given combined sources and both sources have completed, when subscribing, should complete`() {
    Observable<Int>(completeOnSubscribe = true)
        .combineLatest(Observable<String>(completeOnSubscribe = true))
        .subscribe(testObserver)

    testObserver.assertComplete()
  }
}