package com.noheltcj.rxcommon.disposables

class CompositeDisposeBag : Disposable {
  private val disposables = mutableListOf<Disposable>()

  fun add(disposable: Disposable) {
    disposables.add(disposable)
  }

  override fun dispose() {
    disposables.forEach(Disposable::dispose)
  }
}