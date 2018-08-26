package com.noheltcj.rxcommon.observable

import com.noheltcj.rxcommon.Observer
import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Subscription

abstract class AbstractObserver<E>(val next: (E) -> Unit = {},
                                   val error: (Throwable) -> Unit = {},
                                   val complete: () -> Unit = {},
                                   val dispose: () -> Unit = {}) : Observer<E> {
  private val emitter = ObservableEmitter<E>()

  override fun onNext(value: E) {
    next(value)
  }

  override fun onError(throwable: Throwable) {
    error(throwable)
  }

  override fun onComplete() {
    complete()
  }

  override fun onDispose() {
    dispose()
  }

  override fun subscribeTo(source: Source<E>) {
    emitter.addSubscription(Subscription(this))
  }
}