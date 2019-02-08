package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.emitters.ColdEmitter
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

class Filter<E>(private val upstream: Source<E>, private val keep: (E) -> Boolean): Operator<E>() {
    override val emitter: Emitter<E> = ColdEmitter {}

    override fun subscribe(observer: Observer<E>): Disposable {
        emitter.addObserver(observer)

        return upstream.subscribe(AllObserver(
            onNext = {
                if (keep(it)) {
                    emitter.next(it)
                }
            },
            onError = { emitter.terminate(it) },
            onComplete = { emitter.complete() }
        ))
    }
}