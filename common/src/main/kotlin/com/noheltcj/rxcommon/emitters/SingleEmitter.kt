package com.noheltcj.rxcommon.emitters

class SingleEmitter<E>(doOnDispose: () -> Unit) : ColdEmitter<E>(doOnDispose) {
  override fun next(value: E) {
    super.next(value)
    super.complete()
  }

  @Deprecated("Only intended for internal use.", level = DeprecationLevel.HIDDEN)
  override fun complete() {
    super.complete()
  }
}