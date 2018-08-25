package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.*
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Subscription
import com.noheltcj.rxcommon.observable.ObservableEmitter

private typealias Emitter<E> = ObservableEmitter<E>

abstract class Subject<E> : Source<E, Emitter<E>>, Observer<E, Emitter<E>> {
  abstract val emitter : Emitter<E>

  protected var disposable: Disposable? = null

  override fun subscribeTo(source: Source<E, Emitter<E>>) {
    source.subscribe(object : ObservableEmitter)
  }

  override fun <O : Observer<E, Emitter<E>>> subscribe(observer: O): Subscription<E, Emitter<E>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  open fun publish(value: E) {
    activeSubscriptions.forEach { it.emitter.onNext(value) }
  }

//  override fun subscribe(observer: Observer<E>) : Subscription<E> {
//    return ObservableSubscription(this)
//        .also {
//          activeSubscriptions.add(it)
//        }
//  }

  override fun unsubscribe(subscription: Subscription<E, ObservableEmitter<E>>) {
    activeSubscriptions.remove(subscription)
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