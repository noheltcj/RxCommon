package com.noheltcj.rxcommon.emitters

class SingleEmitter<E>(doOnDispose: () -> Unit = {}) : ColdEmitter<E>(doOnDispose) {
  fun success(value: E) {
    onNext(value)
    onComplete()
  }

  fun terminate(throwable: Throwable) {
    onTerminate(throwable)
  }
}