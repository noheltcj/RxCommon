package com.noheltcj.rxcommon.emitters

class CompletableEmitter(doOnDispose: () -> Unit = {}) : ColdEmitter<Any?>(doOnDispose) {
    fun terminate(throwable: Throwable) {
        onTerminate(throwable)
    }

    fun complete() {
        onComplete()
    }
}