package com.noheltcj.rxcommon

class Subscription<T>(
    private val channel: Channel<T>,
    inline val operation: (T) -> Unit) : Disposable {

  override fun dispose() {
    channel.unsubscribe(this)
  }
}