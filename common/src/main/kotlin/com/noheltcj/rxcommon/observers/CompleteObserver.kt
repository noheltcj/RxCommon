package com.noheltcj.rxcommon.observers

class CompleteObserver<E>(
        onComplete: () -> Unit
) : AbstractObserver<E>(
        doOnComplete = onComplete
)