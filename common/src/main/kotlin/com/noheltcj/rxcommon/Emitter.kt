package com.noheltcj.rxcommon

interface Emitter<T> {
  /**
   * Will return true when the source was disposed
   */
  val isDisposed: Boolean

  fun onDispose()
}