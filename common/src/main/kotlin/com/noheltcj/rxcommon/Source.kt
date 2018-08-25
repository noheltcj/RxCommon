package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Subscription

interface Source<E> {
  fun subscribe(operation: (E) -> Unit) : Subscription<E>
  fun unsubscribe(subscription: Subscription<E>)

  fun <T> map(transformation: (E) -> T) : Source<T>
  fun <T, O> combine(otherSource: Source<O>,
                     combinationMode: CombinationMode,
                     transform: (E, O) -> T): Source<T>

  enum class CombinationMode {
    CombineLatest /** http://reactivex.io/documentation/operators/combinelatest.html */
  }
}