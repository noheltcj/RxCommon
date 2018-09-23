package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.emitters.HotEmitter
import com.noheltcj.rxcommon.observers.Observer

class BehaviorRelay<E>(seed: E) : Subject<E>() {
  override val emitter: Emitter<E> = HotEmitter()

  var value = seed
    private set

  override fun subscribe(observer: Observer<E>): Disposable {
    observer.onNext(value)
    return super.subscribe(observer)
  }

  override fun onNext(value: E) {
    this.value = value
    super.onNext(value)
  }

  /** This subject will not terminate */
  override fun onError(throwable: Throwable) {}

  /** This subject will not complete */
  override fun onComplete() {}
}