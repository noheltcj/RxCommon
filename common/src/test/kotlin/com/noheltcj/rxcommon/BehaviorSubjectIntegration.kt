package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.subjects.BehaviorSubject
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
  fun `when subscribing, should emit the seed`() {
    BehaviorSubject("seed").subscribe(testObserver)
    testObserver.assertValue("seed")
  }

  @Test
  fun `given subscribed, when a new value published, should emit new value`() {
    BehaviorSubject("1").apply {
      subscribe(testObserver)
      publish("2")
    }
    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  fun `given no observers and subsequent element published, when subscribing, should emit the last value`() {
    BehaviorSubject("seed").apply {
      publish("elementOne")
      publish("elementTwo")

      subscribe(testObserver)
    }
    testObserver.assertValue("elementTwo")
  }

  @Test
  fun `given upstream has not emitted, when subscribing, should emit the seed`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable())
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
  }


  @Test
  fun `given upstream has emitted, when subscribing, should emit the upstream element`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(just = "upstream"))
      subscribe(testObserver)
    }
    testObserver.assertValue("upstream")
  }

  @Test
  fun `given upstream has emitted complete, when subscribing, should emit seed and complete`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(completeOnSubscribe = true))
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
    testObserver.assertComplete()
  }

  @Test
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