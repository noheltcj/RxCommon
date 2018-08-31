package com.noheltcj.rxcommon.observers

class AllObserver<E>(
    onNext: (E) -> Unit,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit,
    onDispose: () -> Unit
) : AbstractObserver<E>(
    onNext = onNext,
    onError = onError,
    onComplete = onComplete,
    onDispose = onDispose
)