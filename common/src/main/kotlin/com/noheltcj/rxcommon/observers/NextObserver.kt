package com.noheltcj.rxcommon.observers

import com.noheltcj.rxcommon.observable.AbstractObserver

class NextObserver<E>(next: (E) -> Unit) : AbstractObserver<E>(next = next)