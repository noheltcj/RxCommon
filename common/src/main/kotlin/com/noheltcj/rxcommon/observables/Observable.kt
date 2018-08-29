package com.noheltcj.rxcommon.observables

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.Observer

class Observable<E>(private val completeOnSubscribe: Boolean = false) : Source<E> {
  override val emitter: ColdEmitter<E> = ColdEmitter()

  override fun subscribe(observer: Observer<E>): Disposable {
    return super.subscribe(observer)
  }

  constructor(createBlock : (Emitter<E>) -> Disposable) : this() {
    createBlock(emitter)
  }

  constructor(just: E) : this() {
    emitter.next(just)
    emitter.complete()
  }
}