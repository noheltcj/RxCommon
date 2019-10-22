package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.emitters.HotEmitter

open class PublishSubject<E> : Subject<E>() {
  override val emitter: Emitter<E> = HotEmitter()
}