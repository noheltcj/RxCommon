package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.observers.Observer

internal class ColdEmitter<E>() : Emitter<E> {
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
    observer.onDispose()
    activeObservers.remove(observer)
    if (activeObservers.size == 0) {
      dispose()
    }
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

  override fun dispose() {
    isDisposed = true
    activeObservers.forEach(Observer<E>::onDispose)
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