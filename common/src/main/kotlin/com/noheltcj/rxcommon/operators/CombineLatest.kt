package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

class CombineLatest<S1, S2, R>(
    private val sourceOne: Source<S1>,
    private val sourceTwo: Source<S2>,
    private inline val transform: (S1, S2) -> R
) : Source<R> {
  val emitter: Emitter<R> = ColdEmitter {}

  override fun subscribe(observer: Observer<R>): Disposable {
    emitter.addObserver(observer)

    var sourceOneLastElement: S1? = null
    var sourceTwoLastElement: S2? = null

    var completedOne = false

    fun onSourceCompleted() {
      if (!emitter.isDisposed && completedOne) {
        emitter.complete()
      } else {
        completedOne = true
      }
    }

    val upstreamOneDisposable = sourceOne.subscribe(
        AllObserver(
            onNext = {
              sourceOneLastElement = it
              sourceTwoLastElement?.run { emitter.next(transform(it, this)) }
            },
            onError = { emitter.terminate(it) },
            onComplete = { onSourceCompleted() }
        )
    )
    val upstreamTwoDisposable = sourceTwo.subscribe(
        AllObserver(
            onNext = {
              sourceTwoLastElement = it
              sourceOneLastElement?.run { emitter.next(transform(this, it)) }
            },
            onError = { emitter.terminate(it) },
            onComplete = { onSourceCompleted() }
        )
    )

    return Disposables.create {
      emitter.removeObserver(observer)
      upstreamOneDisposable.dispose()
      upstreamTwoDisposable.dispose()
    }
  }
}