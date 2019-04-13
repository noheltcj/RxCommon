package com.noheltcj.rxcommon.observables

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.CompletableEmitter
import com.noheltcj.rxcommon.observers.Observer

class Completable : Source<Any?> {
    private var disposable: Disposable? = null

    protected val emitter = CompletableEmitter {
        disposable?.dispose()
    }

    constructor(createWithEmitter: (emitter: CompletableEmitter) -> Disposable) {
        disposable = createWithEmitter(emitter)
        if (emitter.isDisposed) {
            disposable?.dispose()
        }
    }

    constructor(complete: Boolean) {
        if (complete) {
            emitter.complete()
        }
    }

    constructor(error: Throwable) {
        emitter.terminate(error)
    }

    override fun subscribe(observer: Observer<Any?>) : Disposable {
        emitter.addObserver(observer)

        return Disposables.create {
            emitter.removeObserver(observer)
        }
    }
}
