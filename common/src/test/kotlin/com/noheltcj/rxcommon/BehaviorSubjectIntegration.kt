package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.subjects.BehaviorSubject
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.Test
import kotlin.test.assertEquals

class BehaviorSubjectIntegration {
  lateinit var testObserver: TestObserver<String>()

  // --- No upstream -- START
  @Test
  fun `subscribe - should emit the seed`() {
    var emission: String? = null
    BehaviorSubject("seed").subscribe {
      emission = it
    }
    assertEquals("seed", emission)
  }

  @Test
  fun `subscribe - when a new value published, should emit new value`() {
    var emission: String? = null
    BehaviorSubject("1").apply {
      subscribe {
        emission = it
      }
      publish("2")
    }
    assertEquals("2", emission)
  }

  // --- No upstream -- END

  // --- Has upstream observable -- START

  @Test
  fun `given upstream has not emitted, should emit the seed`() {
    var emission: MockEmission? = null
    BehaviorSubject("seed").apply {
      subscribe {
        emission = it
      }
    }
    assertEquals("seed", emission)
  }

  @Test
  fun `given upstream has emitted, when subscribe, should emit latest value from upstream`() {
    var emission: String? = null
    BehaviorSubject("1").apply {
      subscribe {
        emission = it
      }
    }
    assertEquals("2", emission)
  }
}