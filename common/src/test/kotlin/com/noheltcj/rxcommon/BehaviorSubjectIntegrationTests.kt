package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.BehaviorSubject
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class BehaviorSubjectIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun setup() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("whenSubscribing_shouldEmitTheSeed")
  fun `when subscribing, should emit the seed`() {
    BehaviorSubject("seed")
        .subscribe(testObserver)

    testObserver.assertValue("seed")
  }

  @Test
  @JsName("givenSubscribed_whenNewValuePublished_shouldEmitNewValue")
  fun `given subscribed, when a new value published, should emit new value`() {
    BehaviorSubject("1").apply {
      subscribe(testObserver)
      onNext("2")
    }
    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  @JsName("givenSubscribed_whenCompleted_shouldNotify")
  fun `given subscribed, when completed, should notify`() {
    BehaviorSubject("hello").apply {
      subscribe(testObserver)
    }.onComplete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribed_whenTerminated_shouldNotify")
  fun `given subscribed, when terminated, should notify`() {
    val expectedThrowable = Throwable()
    BehaviorSubject("hello").apply {
      subscribe(testObserver)
    }.onError(expectedThrowable)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenNoObservers_andSubsequentElementPublished_whenSubscribing_shouldEmitTheLastValue")
  fun `given no observers and subsequent element published, when subscribing, should emit the last value`() {
    BehaviorSubject("seed").apply {
      onNext("elementOne")
      onNext("elementTwo")

      subscribe(testObserver)
    }

    testObserver.assertValue("elementTwo")
  }

  @Test
  @JsName("givenUpstreamHasNotEmitted_whenSubscribing_shouldEmitSeed")
  fun `given upstream has not emitted, when subscribing, should emit the seed`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable())
      subscribe(testObserver)
    }

    testObserver.assertValue("seed")
  }

  @Test
  @JsName("givenUpstreamHasEmitted_whenSubscribing_shouldEmitTheUpstreamElement")
  fun `given upstream has emitted, when subscribing, should emit the upstream element`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(just = "upstream"))
      subscribe(testObserver)
    }

    testObserver.assertValue("upstream")
  }

  @Test
  @JsName("givenSubscribed_whenCompletedUpstreamAdded_shouldEmitSeedAndNotify")
  fun `given subscribed, when completed upstream added, should emit seed and notify`() {
    BehaviorSubject("seed").apply {
      subscribe(testObserver)
      subscribeTo(Observable(completeOnSubscribe = true))
    }

    testObserver.assertValue("seed")
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribed_whenTerminatedUpstreamAdded_shouldEmitSeedAndNotify")
  fun `given subscribed, when terminated upstream added, should emit seed and notify`() {
    val expectedThrowable = Throwable("POW!")
    BehaviorSubject("seed").apply {
      subscribe(testObserver)
      subscribeTo(Observable(error = expectedThrowable))
    }

    testObserver.assertValue("seed")
    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenSubscribedToUpstreamSource_whenUpstreamSourceCompletes_shouldNotify")
  fun `given subscribed to upstream source, when upstream source completes, should notify`() {
    val upstream = Observable<String>(completeOnSubscribe = true)
    BehaviorSubject("hi").apply {
      subscribe(testObserver)
      subscribeTo(upstream)
    }

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToColdUpstreamSource_whenCompleted_shouldCompleteUpstream")
  fun `given subscribed to cold upstream source, when completed, should complete upstream`() {
    val upstream = Observable<String>()
    val subject = BehaviorSubject("")

    subject.subscribeTo(upstream)

    subject.onComplete()

    upstream.subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToColdUpstreamSource_whenSubscriptionDisposed_shouldOnlyDisposeUpstream")
  fun `given subscribed to cold upstream source, when subscription disposed, should only dispose upstream`() {
    val upstreamObserver = TestObserver<String>()
    val upstream = Observable<String>()

    BehaviorSubject("").apply {
      subscribeTo(upstream).dispose()
      subscribe(testObserver)
    }

    upstream.subscribe(upstreamObserver)
    upstreamObserver.assertDisposed()
    testObserver.assertNotDisposed()
  }
}