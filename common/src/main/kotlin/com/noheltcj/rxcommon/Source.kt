package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposable
import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observers.Observer

interface Source<E> {
  fun subscribe(observer: Observer<E>) : Disposable
  fun unsubscribe(observer: Observer<E>)
}