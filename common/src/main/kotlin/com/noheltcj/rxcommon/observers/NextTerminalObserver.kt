package com.noheltcj.rxcommon.observers

class NextTerminalObserver<E>(
    onNext: (E) -> Unit,
    onError: (Throwable) -> Unit
) : AbstractObserver<E>(
    onNext = onNext,
    onError = onError
)