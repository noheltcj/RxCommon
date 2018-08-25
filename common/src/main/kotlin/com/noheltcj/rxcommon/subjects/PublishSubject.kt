package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.disposables.Disposable

class PublishSubject<E>() : Subject<E>() {
  constructor(source: (emitter: SubjectEmitter<E>) -> Disposable) : this() {
    source(emitter)
  }
}