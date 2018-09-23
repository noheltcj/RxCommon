package com.noheltcj.rxcommon.observers

class NextTerminalObserver<E>(
    onNext: (E) -> Unit,
    onError: (Throwable) -> Unit
) : AbstractObserver<E>(
    doOnNext = onNext,
    doOnError = onError
)