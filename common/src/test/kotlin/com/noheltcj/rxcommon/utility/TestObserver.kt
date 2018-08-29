package com.noheltcj.rxcommon.utility

import com.noheltcj.rxcommon.observers.Observer
import kotlin.test.*

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
   * Asserts that the source has not emitted any events.
   *
   * This method will fail the test if the source has already terminated with a throwable,
   * emitted an element, emitted complete, or emitted dispose.
   */
  fun assertNoEmission() {
    assertEquals(0, nextValues.size, "expected no elements to be emitted, " +
        "but $nextValues were emitted.")
    assertNotTerminated()
    assertNotComplete()
    assertEquals(false, disposed, "Source emitted disposed, expected no emission.")
  }

  /**
   * Asserts that the source has emitted a single value equal to [expected].
   *
   * This method will fail the test if the source has already terminated with a throwable.
   * The method will also fail in the case that more than one element has been emitted.
   */
  fun assertValue(expected: E) {
    assertNull(error, "Source has already been terminated with $error.")
    assertNotEquals(0, nextValues.size, "Source did not emit an element, expected 1 element.")
    assertEquals(1, nextValues.size, "Source emitted more than one element, " +
        "but only a single element is expected.")
    assertEquals(expected, nextValues.first())
  }

  /**
   * Asserts that the whole sequence of emissions is equal to and in the same order as [expected].
   *
   * This method will fail the test if the source has already terminated with a throwable.
   */
  fun assertValues(expected: List<E>) {
    assertNull(error, "Source has already been terminated with $error.")
    assertEquals(expected, nextValues)
  }

  /**
   * Asserts that the source emitted the [error].
   *
   * This method will fail the test if the source has not emitted the expected [error].
   */
  fun assertTerminated(error: Throwable) {
    assertNotNull(error, "expected $error, but an error was not emitted.")
    assertEquals(error, this.error, "expected $error, but was ${this.error}")
  }

  /**
   * Asserts that the source has not emitted an error.
   *
   * This method will fail the test if the source has emitted an error event.
   */
  fun assertNotTerminated() {
    assertNull(error, "expected $error to be null.")
  }


  /**
   * Asserts that the source emitted complete.
   *
   * This method will fail the test if the source has not emitted the completed event.
   */
  fun assertComplete() {
    assertTrue(completed, "expected the source to have emitted complete.")
  }

  /**
   * Asserts that the source has not emitted complete.
   *
   * This method will fail the test if the source has emitted the completed event.
   */
  fun assertNotComplete() {
    assertFalse(completed, "expected the source to have never emitted the complete event.")
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