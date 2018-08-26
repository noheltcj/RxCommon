package com.noheltcj.rxcommon.observers

abstract class AbstractObserver<E>(val next: (E) -> Unit = {},
                                   val error: (Throwable) -> Unit = {},
                                   val complete: () -> Unit = {},
                                   val dispose: () -> Unit = {}) : Observer<E> {
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
}