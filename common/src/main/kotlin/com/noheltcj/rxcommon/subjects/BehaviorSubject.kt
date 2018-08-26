package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Observer
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.observable.ObservableEmitter

/**
 * See <a href="http://www.introtorx.com/Content/v1.0.10621.0/02_KeyTypes.html#BehaviorSubject" />
 */
class BehaviorSubject<Element>(seed: Element) : Subject<Element>() {
  var value: Element = seed
    private set

  override val emitter = ObservableEmitter<Element>()

  override fun subscribe(observer: Observer<Element>): Disposable {
    observer.onNext(value)
    return super.subscribe(observer)
  }

  override fun onNext(value: Element) {
    this.value = value
    super.onNext(value)
  }

  override fun publish(value: Element) {
    this.value = value
    super.publish(value)
  }
}