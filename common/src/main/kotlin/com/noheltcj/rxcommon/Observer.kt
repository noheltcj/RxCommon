package com.noheltcj.rxcommon

interface Observer<E> {
  fun onNext(value: E)
  fun onError(throwable: Throwable)
  fun onComplete()
  fun onDispose()
}