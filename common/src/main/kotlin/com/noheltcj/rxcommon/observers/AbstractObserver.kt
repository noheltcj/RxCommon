package com.noheltcj.rxcommon.observers

abstract class AbstractObserver<E>(val onNext: (E) -> Unit = {},
                                   val onError: (Throwable) -> Unit = {},
                                   val onComplete: () -> Unit = {},
                                   val onDispose: () -> Unit = {}) : Observer<E> {

  override fun onNext(value: E) {
    onNext.invoke(value)
  }

  override fun onError(throwable: Throwable) {
    onError.invoke(throwable)
  }

  override fun onComplete() {
    onComplete.invoke()
  }

  override fun onDispose() {
    onDispose.invoke()
  }
}