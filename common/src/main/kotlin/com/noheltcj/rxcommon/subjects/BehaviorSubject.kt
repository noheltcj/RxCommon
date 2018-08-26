package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Observer
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Subscription
import com.noheltcj.rxcommon.observable.ObservableEmitter

class BehaviorSubject<Element>(seed: Element) : Subject<Element>() {
  override val emitter = ObservableEmitter<Element>()

  override fun subscribe(observer: Observer<Element>): Disposable {
    observer.onNext(value)
    return super.subscribe(observer)
  }

  var value: Element = seed
    private set

  override fun publish(value: Element) {
    this.value = value
    super.publish(value)
  }
}