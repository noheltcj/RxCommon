package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.observers.Observer

interface Emitter<E> {
  val isDisposed: Boolean
  val isCompleted: Boolean
  val isTerminated: Boolean

  fun addObserver(observer: Observer<E>)
  fun removeObserver(observer: Observer<E>)
}