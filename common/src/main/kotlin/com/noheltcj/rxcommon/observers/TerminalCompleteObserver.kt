package com.noheltcj.rxcommon.observers

class TerminalCompleteObserver<E>(
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : AbstractObserver<E>(
    doOnError = onError,
    doOnComplete = onComplete
)