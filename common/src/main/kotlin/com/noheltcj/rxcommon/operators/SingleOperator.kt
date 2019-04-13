package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.exceptions.UndeliverableNotificationException
import com.noheltcj.rxcommon.observables.Single
import com.noheltcj.rxcommon.observers.AllObserver
import com.noheltcj.rxcommon.observers.Observer

internal class SingleOperator<E>(private val upstream: Source<E>) : Single<E>() {
    override fun subscribe(observer: Observer<E>): Disposable {
        emitter.addObserver(observer)

        return upstream.subscribe(AllObserver(
            onNext = { emitter.success(it) },
            onError = { emitter.terminate(it) },
            onComplete = {
                if (!emitter.isDisposed) {
                    throw UndeliverableNotificationException(UndeliverableNotificationException.Notification.Completed)
                }
            }
        ))
    }
}

