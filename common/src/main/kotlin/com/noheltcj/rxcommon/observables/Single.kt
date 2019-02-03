package com.noheltcj.rxcommon.observables

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.SingleEmitter
import com.noheltcj.rxcommon.observers.Observer

open class Single<E>() : Source<E> {
  private var disposable: Disposable? = null

  private val emitter = SingleEmitter<E> {
    disposable?.dispose()
  }

  constructor(createWithEmitter : (SingleEmitter<E>) -> Disposable) : this() {
    disposable = createWithEmitter(emitter)
    if (emitter.isDisposed) {
      disposable?.dispose()
    }
  }

  constructor(just: E) : this() {
    emitter.next(just)
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