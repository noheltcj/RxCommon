package com.noheltcj.rxcommon.observers

import com.noheltcj.rxcommon.Observer
import com.noheltcj.rxcommon.Source

class NextObserver<E>(private val next: (E) -> Unit) : Observer<E> {
  override fun subscribeTo(source: Source<E>) {

  }

  override fun onNext(value: E) {
    next(value)
  }

  override fun onError(throwable: Throwable) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onComplete() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onDispose() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}