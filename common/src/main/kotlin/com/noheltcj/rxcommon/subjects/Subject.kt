package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.*
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Subscription
import com.noheltcj.rxcommon.observable.ObservableEmitter

class Subject<E> : Observer<E>, Source<E> {
  abstract override val emitter : ObservableEmitter<E>

  override fun subscribeTo(source: Source<E>) {
    source.subscribe(this)
  }

  override fun subscribe(observer: Observer<E>): Disposable {
    return Subscription(observer).also {
      emitter.addSubscription(it)
    }
  }

  open fun publish(value: E) {
    emitter.onNext(value)
  }

  override fun unsubscribe(observer: Observer<E>) {
    emitter.removeObserver(observer)
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