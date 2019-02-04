package com.noheltcj.rxcommon.observers

class TerminalObserver<E>(
    onError: (Throwable) -> Unit
) : AbstractObserver<E>(
    doOnError = onError
)