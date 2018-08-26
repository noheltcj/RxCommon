package com.noheltcj.rxcommon.disposables

import com.noheltcj.rxcommon.Emitter
import com.noheltcj.rxcommon.Observer

class Subscription<E>(private val observer: Observer<E>, private val emitter: Emitter<E>) : Disposable {
  override fun dispose() {
    emitter.removeSubscription(this)
  }
}
