package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AbstractObserver
import com.noheltcj.rxcommon.observers.Observer

abstract class Subject<E> : AbstractObserver<E>(), Source<E> {
  protected abstract val emitter : Emitter<E>

  override fun subscribe(observer: Observer<E>) : Disposable {
    emitter.addObserver(observer)

    return Disposables.create {
      emitter.dispose(observer)
    }
  }

  override fun unsubscribe(observer: Observer<E>) {
    emitter.removeObserver(observer)
  }

  override fun onComplete() {
    super.onComplete()
    emitter.complete()
  }

  override fun onError(throwable: Throwable) {
    super.onError(throwable)
    emitter.terminate(throwable)
  }

  open fun subscribeTo(source: Source<E>) {
    source.subscribe(this)
  }

  open fun publish(value: E) {
    emitter.next(value)
  }
}