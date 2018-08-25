package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.subjects.BehaviorSubject
import com.noheltcj.rxcommon.subjects.PublishSubject
import kotlin.test.Test
import kotlin.test.assertEquals

class CommonTest {
  @Test
  fun randomTest() {
    PublishSubject<String> {
      it
    }
  }
}