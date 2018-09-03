package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.CompositeDisposeBag
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

class FlatMap<E, U>(
    private val upstream: Source<U>,
    private val resolveAdditionalSource: (U) -> Source<E>
) : Operator<E>() {

  override val emitter: Emitter<E> = ColdEmitter()

  override fun subscribe(observer: Observer<E>): Disposable {
    emitter.addObserver(observer)

    val disposeBag = CompositeDisposeBag()

    disposeBag.add(upstream.subscribe(
        AllObserver(
            onNext = {
              disposeBag.add(resolveAdditionalSource(it).subscribe(this))
            },
            onError = { emitter.terminate(it) },
            onComplete = { emitter.complete() },
            onDispose = { emitter.dispose() }
        )
    ))

    return Disposables.create {
      disposeBag.dispose()
      unsubscribe(observer)
    }
  }
}
