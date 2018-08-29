package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.observers.Observer

class ColdEmitter<E> : Emitter<E> {
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
    activeObservers.add(observer)
    if (!released) {
      release()
    }
  }

  override fun removeObserver(observer: Observer<E>) {
    activeObservers.remove(observer)
  }

  override fun next(value: E) {
    forwardPressure.add(value)
    activeObservers.forEach { it.onNext(value) }
  }

  override fun terminate(throwable: Throwable) {
    isTerminated = true
    terminalError = throwable
    if (released)
      activeObservers.forEach { it.onError(throwable) }
  }

  override fun complete() {
    isCompleted = true
    if (released)
      activeObservers.forEach { it.onComplete() }
  }

  override fun dispose(observer: Observer<E>) {
    isDisposed = true
    activeObservers.first { it == observer }.onDispose()
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