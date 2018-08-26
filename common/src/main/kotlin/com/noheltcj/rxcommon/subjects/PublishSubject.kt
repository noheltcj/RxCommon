package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.observable.ObservableEmitter

class PublishSubject<E> : Subject<E>() {
  override val emitter = ObservableEmitter<E>()
}