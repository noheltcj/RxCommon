package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.observers.Observer

open class HotEmitter<E> : Emitter<E> {
  protected val activeObservers = mutableListOf<Observer<E>>()

  override var isDisposed = false
    protected set
  override var isCompleted = false
    protected set
  override var isTerminated = false
    protected set

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
      isDisposed = true
    }
  }

  override fun complete() {
    if (!isDisposed) {
      isCompleted = true
      activeObservers.forEach { it.onComplete() }
      isDisposed = true
    }
  }
}