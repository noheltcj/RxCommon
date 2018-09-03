package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

/**
 * This class will not listen to any upstream dispose notifications other than the original source.
 */
class SwitchMap<E, U>(
    private val upstream: Source<U>,
    private val resolveNewSource: (U) -> Source<E>
) : Operator<E>() {

  override val emitter: Emitter<E> = ColdEmitter()

  private var currentSecondaryDisposable: Disposable? = null

  override fun subscribe(observer: Observer<E>): Disposable {
    emitter.addObserver(observer)

    val upstreamDisposable = upstream.subscribe(
        AllObserver(
          onNext = {
            val tempDisposable = resolveNewSource(it).subscribe(this)
            currentSecondaryDisposable?.dispose()
            currentSecondaryDisposable = tempDisposable
          },
          onError = { emitter.terminate(it) },
          onComplete = { emitter.complete() },
          onDispose = { emitter.dispose() }
        )
    )

    return Disposables.create {
      upstreamDisposable.dispose()
      currentSecondaryDisposable?.dispose()
      unsubscribe(observer)
    }
  }

  override fun onDispose() {}
}