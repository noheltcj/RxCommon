package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.BehaviorSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class BehaviorSubjectIntegration {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun setup() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("whenSubscribing_shouldEmitTheSeed")
  fun `when subscribing, should emit the seed`() {
    BehaviorSubject("seed").subscribe(testObserver)
    testObserver.assertValue("seed")
  }

  @Test
  @JsName("givenSubscribed_whenNewValuePublished_shouldEmitNewValue")
  fun `given subscribed, when a new value published, should emit new value`() {
    BehaviorSubject("1").apply {
      subscribe(testObserver)
      publish("2")
    }
    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  @JsName("givenNoObservers_andSubsequentElementPublished_whenSubscribing_shouldEmitTheLastValue")
  fun `given no observers and subsequent element published, when subscribing, should emit the last value`() {
    BehaviorSubject("seed").apply {
      publish("elementOne")
      publish("elementTwo")

      subscribe(testObserver)
    }
    testObserver.assertValue("elementTwo")
  }

  @Test
  @JsName("givenUpstreamHasNotEmitted_whenSubscribing_shouldEmitTheSeed")
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
  @JsName("givenUpstreamHasEmittedComplete_whenSubscribing_shouldEmitSeedAndComplete")
  fun `given upstream has emitted complete, when subscribing, should emit seed and complete`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(completeOnSubscribe = true))
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenUpstreamHasTerminated_whenSubscribing_shouldEmitSeedAndTerminate")
  fun `given upstream has terminated, when subscribing, should emit seed and terminate`() {
    val expectedThrowable = Throwable("POW!")
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(error = expectedThrowable))
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
    testObserver.assertTerminated(expectedThrowable)
  }
}