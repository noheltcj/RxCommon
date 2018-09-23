package com.noheltcj.rxcommon.observers

class NextObserver<E>(onNext: (E) -> Unit) : AbstractObserver<E>(doOnNext = onNext)