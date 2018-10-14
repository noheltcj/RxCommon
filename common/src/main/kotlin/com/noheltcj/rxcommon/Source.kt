package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.observers.CompleteObserver
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.observers.Observer
import com.noheltcj.rxcommon.observers.TerminalObserver
import com.noheltcj.rxcommon.operators.*

interface Source<E> {
  fun subscribe(observer: Observer<E>): Disposable

  fun <O, R> combineLatest(otherSource: Source<O>, transform: (E, O) -> R): Source<R> = CombineLatest(this, otherSource, transform)
  fun <R> map(transform: (E) -> R): Source<R> = MapOperator(this, transform)
  fun <R> flatMap(resolveAdditionalSource: (E) -> Source<R>): Source<R> = FlatMap(this, resolveAdditionalSource)
  fun <R> switchMap(resolveNewSource: (E) -> Source<R>): Source<R> = SwitchMap(this, resolveNewSource)
  fun doOnEach(observer: Observer<E>): Source<E> = DoOnEach(this, observer)
  fun doOnNext(onNext: (E) -> Unit): Source<E> = DoOnEach(this, NextObserver(onNext))
  fun doOnComplete(onComplete: () -> Unit): Source<E> = DoOnEach(this, CompleteObserver(onComplete))
  fun doOnError(onError: (Throwable) -> Unit): Source<E> = DoOnEach(this, TerminalObserver(onError))
}