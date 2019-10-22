package com.noheltcj.rxcommon.observers

class AllObserver<E>(
    onNext: (E) -> Unit,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : AbstractObserver<E>(
    doOnNext = onNext,
    doOnError = onError,
    doOnComplete = onComplete
)