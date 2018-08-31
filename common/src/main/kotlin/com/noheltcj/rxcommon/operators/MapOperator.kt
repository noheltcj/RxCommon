package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.observers.Observer

class MapOperator<U, E>(private val upstream: Source<U>, private val transform: (U) -> E) : Operator<E>() {
  override val emitter: Emitter<E> = ColdEmitter()

  override fun subscribe(observer: Observer<E>): Disposable {
    emitter.addObserver(observer)

    upstreamDisposable = upstream.subscribe(NextObserver {
      emitter.next(transform(it))
    })

    return Disposables.create {
      emitter.removeObserver(observer)
    }
  }

  override fun unsubscribe(observer: Observer<E>) {
    emitter.removeObserver(observer)
  }

  override fun onDispose() {
    upstreamDisposable?.dispose()
  }
}