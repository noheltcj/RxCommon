package com.noheltcj.rxcommon

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
    testObserver.assertValue("2")
  }

  @Test
  fun `given upstream has not emitted, when subscribing, should emit the seed`() {
    // TODO: Implement this test when Observable exists
  }

  @Test
  fun `given upstream has emitted, when subscribing, should emit latest value from upstream`() {
    BehaviorSubject("1").apply {
      subscribeTo(BehaviorSubject("2"))
      subscribe(testObserver)
    }
    testObserver.assertValue("2")
  }
}