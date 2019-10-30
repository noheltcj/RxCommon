package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

/**
 * This class will not listen to any upstream dispose notifications
 * other than the original source.
 */
class SwitchMap<E, U>(
    private val upstream: Source<U>,
    private val resolveNewSource: (U) -> Source<E>
) : Operator<E>() {

    override val emitter: Emitter<E> = ColdEmitter {}

    private var currentSecondaryDisposable: Disposable? = null
    private var sourceCompleted = false
    private var newSourceCompleted = true

    override fun subscribe(observer: Observer<E>): Disposable {
        emitter.addObserver(observer)

        val upstreamDisposable = upstream.subscribe(
            AllObserver(
                onNext = {
                    newSourceCompleted = false
                    val newSourceDisposable = resolveNewSource(it)
                        .subscribe(this)
                    currentSecondaryDisposable?.dispose()
                    currentSecondaryDisposable = newSourceDisposable
                },
                onError = {
                    currentSecondaryDisposable?.dispose()
                    emitter.terminate(it)
                },
                onComplete = {
                    sourceCompleted = true
                    if (newSourceCompleted)
                        emitter.complete()
                }
            )
        )

        return Disposables.create {
            upstreamDisposable.dispose()
            currentSecondaryDisposable?.dispose()
            emitter.removeObserver(observer)
        }
    }

    override fun onComplete() {
        newSourceCompleted = true
        if (sourceCompleted) {
            super.onComplete()
        }
    }
}