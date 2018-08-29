package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.BehaviorSubject
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class PublishSubjectIntegration {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun setup() {
    testObserver = TestObserver()
  }

  @Test
  fun `given empty constructor used, when subscribing, should not emit`() {
    PublishSubject<String>().subscribe(testObserver)
    testObserver.assertValues(emptyList())
    testObserver.assertNotComplete()
    testObserver.assertNotTerminated()
  }

  @Test
  fun `given subscribed, when a new value published, should emit new value`() {
    PublishSubject<String>().apply {
      subscribe(testObserver)
      publish("2")
    }
    testObserver.assertValue("2")
  }

  @Test
  fun `given upstream has not emitted, when subscribing, should not emit`() {
    PublishSubject<String>().apply {
      subscribeTo(Observable())
      subscribe(testObserver)
    }
    testObserver.assertNoEmission()
  }

  @Test
  fun `given upstream has emitted, when subscribing, should not emit values`() {
    PublishSubject<String>().apply {
      subscribeTo(Observable(just = "upstream"))
      subscribe(testObserver)
    }
    testObserver.assertValues(emptyList())
  }

  @Test
  fun `given upstream has emitted complete, when subscribing, should emit complete`() {
    PublishSubject<String>().apply {
      subscribeTo(Observable(completeOnSubscribe = true))
      subscribe(testObserver)
    }
    testObserver.assertComplete()
  }

  @Test
  fun `given upstream has terminated, when subscribing, should emit error`() {
    val expectedThrowable = Throwable("POW!")
    PublishSubject<String>().apply {
      subscribeTo(Observable(error = expectedThrowable))
      subscribe(testObserver)
    }
    testObserver.assertTerminated(expectedThrowable)
  }
}