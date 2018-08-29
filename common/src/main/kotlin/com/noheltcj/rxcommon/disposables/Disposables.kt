package com.noheltcj.rxcommon.disposables

object Disposables {
  fun create(onDispose: () -> Unit) : Disposable {
    return InternalDisposable(onDispose)
  }

  fun empty() : Disposable {
    return InternalDisposable {}
  }
}