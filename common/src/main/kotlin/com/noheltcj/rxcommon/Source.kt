package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables

interface Source<E> {
  val emitter: Emitter<E>

  fun subscribe(observer: Observer<E>) : Disposable {
    emitter.addObserver(observer)

    return Disposables.create {
      emitter.dispose(observer)
    }
  }

  fun unsubscribe(observer: Observer<E>)

//  fun <T> map(transformation: (E) -> T) : Source<T, >
//  fun <T, O> combine(otherSource: Source<O>,
//                     combinationMode: CombinationMode,
//                     transform: (E, O) -> T): Source<T>
//
//  enum class CombinationMode {
//    CombineLatest /** http://reactivex.io/documentation/operators/combinelatest.html */
//  }
}