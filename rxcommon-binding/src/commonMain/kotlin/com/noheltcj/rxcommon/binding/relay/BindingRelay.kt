package com.noheltcj.rxcommon.binding.relay

import com.noheltcj.rxcommon.binding.emitter.BindingEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.Observer
import com.noheltcj.rxcommon.subjects.BehaviorRelay

/**
 * A [BehaviorRelay]
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