package com.noheltcj.rxcommon.disposables

import com.noheltcj.rxcommon.Source

class Subscription<T>(
    private val source: Source<T>,
    inline val operation: (T) -> Unit) : Disposable {

  override fun dispose() {
    source.unsubscribe(this)
  }
}