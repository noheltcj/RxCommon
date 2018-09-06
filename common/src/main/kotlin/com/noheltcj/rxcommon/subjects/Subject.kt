package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.CompositeDisposeBag
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AbstractObserver
import com.noheltcj.rxcommon.observers.Observer

abstract class Subject<E> : Observer<E>, Source<E>/*, Disposable*/ {
  protected abstract val emitter : Emitter<E>

  private val disposeBag = CompositeDisposeBag()

  override fun subscribe(observer: Observer<E>) : Disposable {
    emitter.addObserver(observer)

    return Disposables.create {
      emitter.removeObserver(observer)
    }
  }

  override fun onNext(value: E) {
    emitter.next(value)
  }

  override fun onComplete() {
    if (!emitter.isDisposed) {
      emitter.complete()
      disposeBag.dispose()
    }
  }

  override fun onError(throwable: Throwable) {
    emitter.terminate(throwable)
    disposeBag.dispose()
  }

  open fun subscribeTo(source: Source<E>) {
    disposeBag.add(source.subscribe(this))
  }
}