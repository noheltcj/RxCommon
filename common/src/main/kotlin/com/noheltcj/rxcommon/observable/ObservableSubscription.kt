package com.noheltcj.rxcommon.observable

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Subscription

class ObservableSubscription<E>(private val source: Source<E>) : Subscription {
  override fun dispose() {
    source.unsubscribe(this)
  }
}