package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.BehaviorRelay
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class BehaviorRelayIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun setup() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("whenSubscribing_shouldEmitTheSeed")
  fun `when subscribing, should emit the seed`() {
    BehaviorRelay("seed").subscribe(testObserver)
    testObserver.assertValue("seed")
  }

  @Test
  @JsName("givenSubscribed_whenNewValuePublished_shouldEmitNewValue")
  fun `given subscribed, when a new value published, should emit new value`() {
    BehaviorRelay("1").apply {
      subscribe(testObserver)
      onNext("2")
    }
    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  @JsName("givenSubscribed_whenTerminated_shouldNotNotify")
  fun `given subscribed, when terminated, should not notify`() {
    BehaviorRelay("1").apply {
      subscribe(testObserver)
      onError(Throwable())
    }
    testObserver.assertNotTerminated()
  }

  @Test
  @JsName("givenSubscribed_whenCompleted_shouldNotNotify")
  fun `given subscribed, when completed, should not notify`() {
    val expectedThrowable = Throwable()
    BehaviorRelay("1").apply {
      subscribe(testObserver)
      onComplete()
    }
    testObserver.assertNotComplete()
  }

  @Test
  @JsName("givenNoObservers_andSubsequentElementPublished_whenSubscribing_shouldEmitTheLastValue")
  fun `given no observers and subsequent element published, when subscribing, should emit the last value`() {
    BehaviorRelay("seed").apply {
      onNext("elementOne")
      onNext("elementTwo")

      subscribe(testObserver)
    }
    testObserver.assertValue("elementTwo")
  }

  @Test
  @JsName("givenUpstreamHasNotEmitted_whenSubscribing_shouldEmitTheSeed")
  fun `given upstream has not emitted, when subscribing, should emit the seed`() {
    BehaviorRelay("seed").apply {
      subscribeTo(Observable())
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
  }

  @Test
  @JsName("givenUpstreamHasEmitted_whenSubscribing_shouldEmitTheUpstreamElement")
  fun `given upstream has emitted, when subscribing, should emit the upstream element`() {
    BehaviorRelay("seed").apply {
      subscribeTo(Observable(just = "upstream"))
      subscribe(testObserver)
    }
    testObserver.assertValue("upstream")
  }

  @Test
  @JsName("givenUpstreamHasEmittedComplete_whenSubscribing_shouldEmitSeedAndIgnoreNotification")
  fun `given upstream has emitted complete, when subscribing, should emit seed and ignore notification`() {
    BehaviorRelay("seed").apply {
      subscribeTo(Observable(completeOnSubscribe = true))
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
    testObserver.assertNotComplete()
  }

  @Test
  @JsName("givenUpstreamHasTerminated_whenSubscribing_shouldEmitSeedAndIgnoreTerminateNotification")
  fun `given upstream has terminated, when subscribing, should emit seed and ignore terminate notification`() {
    BehaviorRelay("seed").apply {
      subscribeTo(Observable(error = Throwable("oops")))
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
    testObserver.assertNotTerminated()
  }

  @Test
  @JsName("givenSubscribedToUpstreamSource_whenUpstreamSourceCompletes_shouldIgnoreNotification")
  fun `given subscribed to upstream source, when upstream source completes, should ignore notification`() {
    val upstream = Observable(just = "hi")
    val relay = BehaviorRelay("hey").apply {
      subscribe(testObserver)
      subscribeTo(upstream)
    }

    relay.onNext("still alive")

    testObserver.assertValues(listOf("hey", "hi", "still alive"))
    testObserver.assertNotDisposed()
  }
}