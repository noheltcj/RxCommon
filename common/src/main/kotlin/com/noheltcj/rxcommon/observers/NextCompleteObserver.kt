package com.noheltcj.rxcommon.observers

class NextCompleteObserver<E> (
    onNext: (E) -> Unit,
    onComplete: () -> Unit
) : AbstractObserver<E>(
    onNext = onNext,
    onComplete = onComplete
)