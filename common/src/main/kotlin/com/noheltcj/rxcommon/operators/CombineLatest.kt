package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.observers.Observer

class CombineLatest<S1, S2, R>(
    private val sourceOne: Source<S1>,
    private val sourceTwo: Source<S2>,
    private inline val transform: (S1, S2) -> R
) : Operator<R>() {
  private var sourceOneLastElement: S1? = null
  private var sourceTwoLastElement: S2? = null

  override val emitter: Emitter<R> = ColdEmitter()

  override fun subscribe(observer: Observer<R>): Disposable {
    emitter.addObserver(observer)

    val upstreamOneDisposable = sourceOne.subscribe(NextObserver {
      sourceOneLastElement = it
      sourceTwoLastElement?.run { emitter.next(transform(it, this)) }
    })
    val upstreamTwoDisposable = sourceTwo.subscribe(NextObserver {
      sourceTwoLastElement = it
      sourceOneLastElement?.run { emitter.next(transform(this, it)) }
    })

    return Disposables.create {
      emitter.removeObserver(observer)
      upstreamOneDisposable.dispose()
      upstreamTwoDisposable.dispose()
    }
  }
}