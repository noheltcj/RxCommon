package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.*
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Subscription

abstract class Subject<E> : Source<E> {
  protected val emitter = SubjectEmitter<E>()
  protected val activeSubscriptions = mutableListOf<Subscription<E>>()

  protected var disposable: Disposable? = null

  open fun publish(value: E) {
    activeSubscriptions.forEach { it.operation(value) }
  }

  override fun subscribe(operation: (E) -> Unit) : Subscription<E> {
    return Subscription(this, operation)
        .also {
          activeSubscriptions.add(it)
        }
  }

  override fun unsubscribe(subscription: Subscription<E>) {
    activeSubscriptions.remove(subscription)
  }

  override fun <T> map(transformation: (E) -> T): Source<T> {
    return Operator.Map(this, transformation)
  }

  override fun <T, O> combine(
      otherSource: Source<O>,
      combinationMode: Source.CombinationMode,
      transform: (E, O) -> T): Source<T> {
    return Operator.Combine(this, otherSource, combinationMode, transform)
  }
}