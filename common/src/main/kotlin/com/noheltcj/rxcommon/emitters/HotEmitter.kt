package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.observers.Observer

internal class HotEmitter<E> : Emitter<E> {
  private val activeObservers = mutableListOf<Observer<E>>()
  private var terminalError: Throwable? = null

  override var isDisposed = false
    private set
  override var isCompleted = false
    private set
  override var isTerminated = false
    private set

  override fun addObserver(observer: Observer<E>) {
    activeObservers.add(observer)
    if (isCompleted) {
      observer.onComplete()
    }
    if (isTerminated) {
      observer.onError(terminalError!!)
    }
  }

  override fun removeObserver(observer: Observer<E>) {
    activeObservers.remove(observer)
  }

  override fun next(value: E) {
    activeObservers.forEach { it.onNext(value) }
  }

  override fun terminate(throwable: Throwable) {
    terminalError = throwable
    isTerminated = true
    activeObservers.forEach { it.onError(throwable) }
  }

  override fun complete() {
    isCompleted = true
    activeObservers.forEach { it.onComplete() }
  }

  override fun dispose(observer: Observer<E>) {
    isDisposed = true
    activeObservers.first { it == observer }.onDispose()
  }
}