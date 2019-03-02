package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.CompositeDisposeBag
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.Observer

abstract class Subject<E> : Observer<E>, Source<E> {
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
    emitter.complete()
    disposeBag.dispose()
  }

  override fun onError(throwable: Throwable) {
    emitter.terminate(throwable)
    disposeBag.dispose()
  }

  /**
   * Subscribes this as an observer of the specified source; forwarding emissions.
   *
   * @param source The source to be subscribed to.
   * @return Created from the internal subscribe call.
   *
   * @see Source.subscribe
   * @see Disposable
   */
  open fun subscribeTo(source: Source<E>): Disposable {
    return source.subscribe(this)
        .apply(disposeBag::add)
  }
}