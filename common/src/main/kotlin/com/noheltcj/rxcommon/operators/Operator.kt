package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.emitters.ObservableEmitter
import com.noheltcj.rxcommon.observers.Observer

abstract class Operator<E> : Source<E>, Observer<E> {
  protected abstract val emitter: ObservableEmitter<E>

  override fun onNext(value: E) {
    emitter.next(value)
  }

  override fun onError(throwable: Throwable) {
    emitter.terminate(throwable)
  }

  override fun onComplete() {
    emitter.complete()
  }
}