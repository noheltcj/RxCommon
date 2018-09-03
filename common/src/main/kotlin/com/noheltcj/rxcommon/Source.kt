package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.observers.Observer
import com.noheltcj.rxcommon.operators.CombineLatest
import com.noheltcj.rxcommon.operators.FlatMap
import com.noheltcj.rxcommon.operators.MapOperator

interface Source<E> {
  fun subscribe(observer: Observer<E>) : Disposable
  fun unsubscribe(observer: Observer<E>)

  fun <O, R> combineLatest(otherSource: Source<O>, transform: (E, O) -> R): Source<R> = CombineLatest(this, otherSource, transform)
  fun <R> map(transform: (E) -> R): Source<R> = MapOperator(this, transform)
  fun <R> flatMap(additionalSourceResolver: (E) -> Source<R>) : Source<R> = FlatMap(this, additionalSourceResolver)
}