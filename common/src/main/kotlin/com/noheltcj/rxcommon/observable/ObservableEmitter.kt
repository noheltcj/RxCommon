package com.noheltcj.rxcommon.observable

import com.noheltcj.rxcommon.Emitter
import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Subscription

class ObservableEmitter<El, Em : Emitter<El>>(val source: Source<El, Em>) : Emitter<El> {
  protected val activeSubscriptions = mutableListOf<Subscription<El>>()

  override var isDisposed = false
    private set

  override fun onDispose() {
    isDisposed = true
    source.unsubscribe()
  }

  fun onNext(value: E)
  fun onError(throwable: Throwable)
  fun onComplete()
}