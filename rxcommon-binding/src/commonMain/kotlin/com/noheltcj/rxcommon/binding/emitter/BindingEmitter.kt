package com.noheltcj.rxcommon.binding.emitter

import com.noheltcj.rxcommon.emitters.HotEmitter
import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.observers.Observer

/**
 * A hot emitter with the ability to emit omitting a specified observer.
 * This is useful to create a more efficient, true two-way binding.
 */
class BindingEmitter<E> : HotEmitter<E>() {
  fun nextIgnoringObserver(observer: Observer<E>, value: E) {
    if (!isDisposed) {
      activeObservers
          .filter { it != observer}
          .forEach { it.onNext(value) }
    } else {
      throw UndeliverableEmissionException(value)
    }
  }
}