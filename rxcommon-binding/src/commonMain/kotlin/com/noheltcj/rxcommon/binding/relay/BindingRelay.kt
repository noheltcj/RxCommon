package com.noheltcj.rxcommon.binding

import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.Observer
import com.noheltcj.rxcommon.subjects.BehaviorRelay

/**
 * This relay facilitates efficient two-way binding.
 */
open class BindingRelay<E>(seed: E) : BehaviorRelay<E>(seed) {
  override val emitter: Emitter<E> = BindingEmitter()

  /**
   * Use this for communicating local changes in two-way binding.
   */
  fun onNextIgnoring(observer: Observer<E>, value: E) {
    this.value = value
    (emitter as BindingEmitter<E>).nextIgnoringObserver(observer, value)
  }
}