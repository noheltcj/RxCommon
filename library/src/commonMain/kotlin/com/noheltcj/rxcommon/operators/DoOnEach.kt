package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

class DoOnEach<E>(
    private val upstream: Source<E>,
    private val onEachObserver: Observer<E>
) : Operator<E>() {

    override val emitter: Emitter<E> = ColdEmitter {}

    override fun subscribe(observer: Observer<E>): Disposable {
        emitter.addObserver(observer)

        val upstreamDisposable = upstream.subscribe(
            AllObserver(
                onNext = {
                    onEachObserver.onNext(it)
                    emitter.next(it)
                },
                onError = {
                    onEachObserver.onError(it)
                    emitter.terminate(it)
                },
                onComplete = {
                    onEachObserver.onComplete()
                    emitter.complete()
                }
            )
        )

        return Disposables.create {
            emitter.removeObserver(observer)
            upstreamDisposable.dispose()
        }
    }
}