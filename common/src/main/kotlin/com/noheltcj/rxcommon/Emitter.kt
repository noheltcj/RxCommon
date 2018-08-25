package com.noheltcj.rxcommon

abstract class Emitter<T> {
  /**
   * Will return true when the source was disposed
   */
  var isDisposed = false
    internal set
}