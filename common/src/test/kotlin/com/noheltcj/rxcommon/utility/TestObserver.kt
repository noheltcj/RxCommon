package com.noheltcj.rxcommon.utility

import com.noheltcj.rxcommon.observers.Observer
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestObserver<E> : Observer<E> {
  private val nextValues = mutableListOf<E>()
  private var error: Throwable? = null
  private var completed = false
  private var disposed = false

  /**
   * asserts that the last value emitted onNext is equal to expected
   */
  fun assertValue(expected: E) {
    assertNull(error, "Observer has already been terminated with $error")
    assertEquals(expected, nextValues.lastOrNull())
  }

  override fun onNext(value: E) {
    nextValues.add(value)
  }

  override fun onError(throwable: Throwable) {
    error = throwable
  }

  override fun onComplete() {
    completed = true
  }

  override fun onDispose() {
    disposed = true
  }
}