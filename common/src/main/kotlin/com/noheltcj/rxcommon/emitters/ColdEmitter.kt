package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableCompletionException
import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.exceptions.UndeliverableTerminationException
import com.noheltcj.rxcommon.observers.Observer

internal class ColdEmitter<E> : Emitter<E> {
  private val activeObservers = mutableListOf<Observer<E>>()

  override var isDisposed = false
    private set
  override var isCompleted = false
    private set
  override var isTerminated = false
    private set

  private var released = false
  private val forwardPressure = mutableListOf<E>()
  private var terminalError: Throwable? = null

  override fun addObserver(observer: Observer<E>) {
    if (!isDisposed) {
      activeObservers.add(observer)
    }
    if (!released) {
      release()
    }
  }

  override fun removeObserver(observer: Observer<E>) {
    activeObservers.remove(observer)
    if (activeObservers.size == 0) {
      dispose()
    }
  }

  override fun next(value: E) {
    if (!isDisposed) {
      activeObservers.forEach { it.onNext(value) }
    } else {
      throw UndeliverableEmissionException(value)
    }
  }

  override fun terminate(throwable: Throwable) {
    if (!isDisposed) {
      isTerminated = true
      activeObservers.forEach { it.onError(throwable) }
      dispose()
    } else {
      throw UndeliverableTerminationException(throwable)
    }
  }

  override fun complete() {
    if (!isDisposed) {
      isCompleted = true
      activeObservers.forEach { it.onComplete() }
      dispose()
    } else {
      throw UndeliverableCompletionException()
    }
  }

  private fun dispose() {
    isDisposed = true
    activeObservers.clear()
  }

  private fun release() {
    released = true
    activeObservers.forEach { observer ->
      forwardPressure.forEach { element ->
        observer.onNext(element)
      }
      if (isCompleted)
        observer.onComplete()
      terminalError?.run { observer.onError(this) }
    }
  }
}