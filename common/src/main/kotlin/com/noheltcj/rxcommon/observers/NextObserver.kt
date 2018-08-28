package com.noheltcj.rxcommon.observers

class NextObserver<E>(next: (E) -> Unit) : AbstractObserver<E>(next = next)