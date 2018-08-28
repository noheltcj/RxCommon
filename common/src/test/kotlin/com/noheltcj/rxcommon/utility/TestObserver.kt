package com.noheltcj.rxcommon.utility

import com.noheltcj.rxcommon.observers.Observer
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * An observer for testing *sources*
 *
 * Captures events and records them for performing later assertions.
 *
 * @param E The type of element this observer can receive
 * @see Observer
 */
class TestObserver<E> : Observer<E> {
  private val nextValues = mutableListOf<E>()
  private var error: Throwable? = null
  private var completed = false
  private var disposed = false

  /**
   * Asserts that the last value emitted onNext is equal to [expected].
   *
   * This method will fail the test if the observer has already terminated with a throwable.
   */
  fun assertValue(expected: E) {
    assertNull(error, "Observer has already been terminated with $error")
    assertEquals(expected, nextValues.lastOrNull())
  }

  /**
   * Called when the source emits a value. It is not recommended to call this in a test.
   */
  override fun onNext(value: E) {
    nextValues.add(value)
  }

  /**
   * Called when the source emits an error. It is not recommended to call this in a test.
   */
  override fun onError(throwable: Throwable) {
    error = throwable
  }

  /**
   * Called when the source emits completed. It is not recommended to call this in a test.
   */
  override fun onComplete() {
    completed = true
  }

  /**
   * Called when the source emits disposed. It is not recommended to call this in a test.
   */
  override fun onDispose() {
    disposed = true
  }
}