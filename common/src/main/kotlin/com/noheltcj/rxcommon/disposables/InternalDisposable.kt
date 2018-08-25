package com.noheltcj.rxcommon.disposables

internal class InternalDisposable(private val onDispose: () -> Unit) : Disposable {
  override fun dispose() {
    onDispose()
  }
}