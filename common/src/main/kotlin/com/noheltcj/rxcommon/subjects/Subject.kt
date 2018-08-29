package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.HotEmitter
import com.noheltcj.rxcommon.observers.AbstractObserver
import com.noheltcj.rxcommon.observers.Observer

abstract class Subject<E> : AbstractObserver<E>(), Source<E> {
  protected abstract val emitter : HotEmitter<E>

  override fun subscribe(observer: Observer<E>) : Disposable {
    emitter.addObserver(observer)

    return Disposables.create {
      emitter.dispose(observer)
    }
  }

  override fun unsubscribe(observer: Observer<E>) {
    emitter.removeObserver(observer)
  }

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