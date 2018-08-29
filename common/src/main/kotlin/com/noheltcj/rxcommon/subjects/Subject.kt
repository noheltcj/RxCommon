package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.emitters.HotEmitter
import com.noheltcj.rxcommon.observers.AbstractObserver

abstract class Subject<E> : AbstractObserver<E>(), Source<E> {
  abstract override val emitter : HotEmitter<E>

  open fun subscribeTo(source: Source<E>) {
    source.subscribe(this)
  }

  open fun publish(value: E) {
    emitter.next(value)
  }

//  override fun <T> map(transformation: (E) -> T): Source<T> {
//    return Operator.Map(this, transformation)
//  }
//
//  override fun <T, O> combine(
//      otherSource: Source<O>,
//      combinationMode: Source.CombinationMode,
//      transform: (E, O) -> T): Source<T> {
//    return Operator.Combine(this, otherSource, combinationMode, transform)
//  }
}