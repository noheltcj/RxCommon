package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.emitters.HotEmitter
import com.noheltcj.rxcommon.observers.Observer

/**
 * See <a href="http://www.introtorx.com/Content/v1.0.10621.0/02_KeyTypes.html#BehaviorSubject" />
 */
class BehaviorSubject<E>(seed: E) : Subject<E>() {
  override val emitter: Emitter<E> = HotEmitter()

  var value: E = seed
    private set

  override fun subscribe(observer: Observer<E>): Disposable {
    observer.onNext(value)
    return super.subscribe(observer)
  }

  override fun onNext(value: E) {
    this.value = value
    super.onNext(value)
  }
}