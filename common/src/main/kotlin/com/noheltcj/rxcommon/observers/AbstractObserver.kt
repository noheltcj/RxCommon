package com.noheltcj.rxcommon.observers

abstract class AbstractObserver<E>(val doOnNext: (E) -> Unit = {},
                                   val doOnError: (Throwable) -> Unit = {},
                                   val doOnComplete: () -> Unit = {},
                                   val doOnDispose: () -> Unit = {}) : Observer<E> {

  override fun onNext(value: E) {
    doOnNext(value)
  }

  override fun onError(throwable: Throwable) {
    doOnError(throwable)
  }

  override fun onComplete() {
    doOnComplete()
  }

  override fun onDispose() {
    doOnDispose()
  }
}