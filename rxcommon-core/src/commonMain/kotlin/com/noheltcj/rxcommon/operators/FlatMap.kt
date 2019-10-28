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
    override val emitter: Emitter<E> = ColdEmitter {}

    private var upstreamCompleted = false
    private var mergedSourceCount = 0

    override fun subscribe(observer: Observer<E>): Disposable {
        emitter.addObserver(observer)

        val disposeBag = CompositeDisposeBag()

        disposeBag.add(upstream.subscribe(
            AllObserver(
                onNext = {
                    mergedSourceCount += 1
                    disposeBag.add(resolveAdditionalSource(it).subscribe(this))
                },
                onError = { emitter.terminate(it) },
                onComplete = {
                    upstreamCompleted = true
                    if (!emitter.isDisposed && mergedSourceCount == 0) emitter.complete()
                }
            )
        ))

        return Disposables.create {
            emitter.removeObserver(observer)
            disposeBag.dispose()
        }
    }

    override fun onComplete() {
        mergedSourceCount--
        if (upstreamCompleted && mergedSourceCount == 0) {
            super.onComplete()
        }
    }
}
