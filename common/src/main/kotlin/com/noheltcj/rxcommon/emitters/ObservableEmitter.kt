package com.noheltcj.rxcommon.emitters

class ObservableEmitter<E>(doOnDispose: () -> Unit = {}) : ColdEmitter<E>(doOnDispose) {
    fun next(value: E) = onNext(value)
    fun terminate(throwable: Throwable) = onTerminate(throwable)
    fun complete() = onComplete()
}