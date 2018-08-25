package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Subscription

interface Source<El, Em : Emitter<El>> {
  fun <O : Observer<El, Em>> subscribe(observer: O) : Subscription<El, Em>
  fun unsubscribe(subscription: Subscription<El, Em>)

//  fun <T> map(transformation: (E) -> T) : Source<T, >
//  fun <T, O> combine(otherSource: Source<O>,
//                     combinationMode: CombinationMode,
//                     transform: (E, O) -> T): Source<T>

  enum class CombinationMode {
    CombineLatest /** http://reactivex.io/documentation/operators/combinelatest.html */
  }
}