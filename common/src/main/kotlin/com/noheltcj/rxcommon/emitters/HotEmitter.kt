package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.observers.Observer

class HotEmitter<E> : Emitter<E> {
  private val activeObservers = mutableListOf<Observer<E>>()

  override var isDisposed = false
    private set
  override var isCompleted = false
    private set
  override var isTerminated = false
    private set

  override fun addObserver(observer: Observer<E>) {
    if (!isDisposed)
      activeObservers.add(observer)
  }

  override fun removeObserver(observer: Observer<E>) {
    activeObservers.remove(observer)
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
    }
  }

  override fun complete() {
    if (!isDisposed) {
      isCompleted = true
      activeObservers.forEach { it.onComplete() }
      dispose()
    }
  }

  private fun dispose() {
    isDisposed = true
  }
}