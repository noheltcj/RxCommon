package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.emitters.ObservableEmitter

class PublishSubject<E> : Subject<E>() {
  override val emitter = ObservableEmitter<E>()
}