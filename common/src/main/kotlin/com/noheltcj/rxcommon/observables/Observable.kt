package com.noheltcj.rxcommon.observables

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.emitters.ObservableEmitter
import com.noheltcj.rxcommon.observers.Observer

open class Observable<E>(completeOnSubscribe: Boolean = false) : Source<E> {
  private var disposable: Disposable? = null

  protected val emitter = ObservableEmitter<E> {
    disposable?.dispose()
  }

  init {
    if (completeOnSubscribe) {
      emitter.complete()
    }
  }

  constructor(createWithEmitter : (ObservableEmitter<E>) -> Disposable) : this() {
    disposable = createWithEmitter(emitter)
    if (emitter.isDisposed) {
      disposable?.dispose()
    }
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
}