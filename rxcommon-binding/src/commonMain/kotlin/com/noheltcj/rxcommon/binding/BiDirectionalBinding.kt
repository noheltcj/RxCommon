package com.noheltcj.rxcommon.binding

import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.observers.NextObserver

abstract class BiDirectionalBinding<E, U>(
    private val relay: BindingRelay<U>,
    private val onNext: (E) -> Unit
): Disposable {
    private var started = false
    private val observer = NextObserver<U> {
        onNext(transformForSubscriber(it))
    }

    private var disposable: Disposable? = null

    fun start() {
        if (!started) {
            started = true
            disposable = relay.subscribe(observer)
        } else {
            throw RuntimeException("Already started")
        }
    }

    fun onSubscriberUpdatedLocally(localData: E) {
        relay.onNextIgnoring(observer, transformForUpstream(localData))
    }

    abstract fun transformForUpstream(localData: E): U
    abstract fun transformForSubscriber(emission: U): E

    override fun dispose() {
        disposable?.dispose()
    }
}
