package com.noheltcj.rxcommon.utility

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.observers.Observer
import kotlin.test.*

/**
 * An observer for testing *sources*
 *
 * Captures events and records them for performing later assertions.
 * It is recommended only to subscribe this observer to a single source.
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
        "but $nextValues was emitted.")
    assertNotTerminated()
    assertNotComplete()
    assertEquals(false, disposed, "Source was disposed, expected no notifications.")
  }

  /**
   * Asserts that the source has emitted a single element equal to [expected].
   *
   * This method will fail in the case that more than one element has been emitted.
   */
  fun assertValue(expected: E) {
    assertNotEquals(0, nextValues.size, "Source did not emit an element, expected 1 element.")
    assertEquals(1, nextValues.size, "Source emitted more than one element, " +
        "but only a single element is expected.")
    assertEquals(expected, nextValues.first())
  }

  /**
   * Asserts that the whole sequence of emissions is equal to and in the same order as [expected].
   */
  fun assertValues(expected: List<E>) {
    assertEquals(expected, nextValues)
  }

  /**
   * Asserts that the source terminated with [error].
   *
   * This method will fail the test if the source has not sent the the expected notification matching [error].
   */
  fun assertTerminated(error: Throwable) {
    assertNotNull(error, "expected the source to be terminated with $error, but it has not terminated.")
    assertEquals(error, this.error, "expected source to be terminated with $error, but was ${this.error}")
  }

  /**
   * Asserts that the source has not terminated with an error.
   *
   * This method will fail the test if the source has terminated with an error notification.
   */
  fun assertNotTerminated() {
    assertNull(error, "expected the error notification to not have been sent, but was $error ")
  }

  /**
   * Asserts that the source has completed.
   *
   * This method will fail the test if the source has not sent the completed notification.
   */
  fun assertComplete() {
    assertTrue(completed, "expected the source to have sent the completed notification.")
  }

  /**
   * Asserts that the source has not completed.
   *
   * This method will fail the test if the source has sent the completed notification.
   */
  fun assertNotComplete() {
    assertFalse(completed, "expected the source to have never sent the completed notification.")
  }

  /**
   * Asserts that the source has been disposed.
   *
   * This method will fail the test if the source has not sent the dispose notification.
   */
  fun assertDisposed() {
    assertTrue(disposed, "expected the source to have sent the dispose notification.")
  }

  /**
   * Asserts that the source has not been disposed.
   *
   * This method will fail the test if the source has sent the dispose notification.
   */
  fun assertNotDisposed() {
    assertFalse(disposed, "expected the source to have never sent the dispose notification.")
  }

  /**
   * Called when the source emits a value. It is not recommended to call this in a test.
   */
  override fun onNext(value: E) {
    nextValues.add(value)
  }

  /**
   * Called when the source terminates with an error notification. It is not recommended to call this in a test.
   */
  override fun onError(throwable: Throwable) {
    error = throwable
  }

  /**
   * Called when the source sends the completed notification. It is not recommended to call this in a test.
   */
  override fun onComplete() {
    completed = true
  }

  /**
   * Called when the source sends the disposed notification. It is not recommended to call this in a test.
   */
  override fun onDispose() {
    disposed = true
  }
}