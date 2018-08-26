package com.noheltcj.rxcommon

interface Emitter<E> {
  val isDisposed: Boolean
  val isCompleted: Boolean
  val isTerminated: Boolean

  fun addObserver(observer: Observer<E>)
  fun removeObserver(observer: Observer<E>)

  fun next(value: E)
  fun terminate(throwable: Throwable)
  fun complete()
  fun dispose(observer: Observer<E>)
}