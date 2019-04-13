package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ObservableEmitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

class OnErrorReturn<U>(
    private val upstream: Source<U>,
    private val onErrorResolveNewSource: (Throwable) -> Source<U>
) : Operator<U>() {
  override val emitter = ObservableEmitter<U>()

  override fun subscribe(observer: Observer<U>): Disposable {
    emitter.addObserver(observer)

    val upstreamDisposable = upstream.subscribe(AllObserver(
        onNext = { emitter.next(it) },
        onError = { upstreamThrowable ->
          onErrorResolveNewSource(upstreamThrowable).subscribe(AllObserver(
              onNext = { emitter.next(it) },
              onError = { emitter.terminate(it) },
              onComplete = { emitter.complete() }
          ))
        },
        onComplete = { emitter.complete() }
    ))

    return Disposables.create {
      emitter.removeObserver(observer)
      upstreamDisposable.dispose()
    }
  }
}