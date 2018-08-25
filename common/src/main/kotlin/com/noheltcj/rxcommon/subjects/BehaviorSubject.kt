package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.disposables.Subscription

class BehaviorSubject<Element>(seed: Element) : Subject<Element>() {
  var value: Element = seed
    private set

  override fun publish(value: Element) {
    this.value = value
    super.publish(value)
  }

  fun publishOmitting(subscription: Subscription<Element>, value: Element) {
    this.value = value
    activeSubscriptions
        .filter { it != subscription }
        .forEach { it.operation(value) }
  }

  override fun subscribe(operation: (Element) -> Unit) : Subscription<Element> {
    value?.run(operation)
    return super.subscribe(operation)
  }
}