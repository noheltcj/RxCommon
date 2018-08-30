package com.noheltcj.rxcommon.observables

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.Observer

class Observable<E>(completeOnSubscribe: Boolean = false) : Source<E> {
  private val emitter: ColdEmitter<E> = ColdEmitter()

  init {
    if (completeOnSubscribe) {
      emitter.complete()
    }
  }

  constructor(createWithEmitter : (Emitter<E>) -> Disposable) : this() {
    createWithEmitter(emitter)
  }

  constructor(just: E) : this() {
    emitter.next(just)
    emitter.complete()
  }

  constructor(error: Throwable) : this() {
    emitter.terminate(error)
  }

  override fun subscribe(observer: Observer<E>) : Disposable {
    emitter.addObserver(observer)

    return Disposables.create {
      emitter.removeObserver(observer)
    }
  }

  override fun unsubscribe(observer: Observer<E>) {
    emitter.removeObserver(observer)
  }
}