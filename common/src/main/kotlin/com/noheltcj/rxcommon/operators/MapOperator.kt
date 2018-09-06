package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.observers.Observer

class MapOperator<U, E>(private val upstream: Source<U>, private val transform: (U) -> E) : Operator<E>() {
  override val emitter: Emitter<E> = ColdEmitter()

  override fun subscribe(observer: Observer<E>): Disposable {
    emitter.addObserver(observer)

    val upstreamDisposable = upstream.subscribe(AllObserver (
        onNext = { emitter.next(transform(it)) },
        onError = { emitter.terminate(it) },
        onComplete = { emitter.complete() }
    ))

    return Disposables.create {
      emitter.removeObserver(observer)
      upstreamDisposable.dispose()
    }
  }
}