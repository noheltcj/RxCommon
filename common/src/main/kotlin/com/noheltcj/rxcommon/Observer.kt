package com.noheltcj.rxcommon

interface Observer<E> {
  fun subscribeTo(source: Source<E>) {
    source.subscribe(this)
  }

  fun onNext(value: E)
  fun onError(throwable: Throwable)
  fun onComplete()
  fun onDispose()
}