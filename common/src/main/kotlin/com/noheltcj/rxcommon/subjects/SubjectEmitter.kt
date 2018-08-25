package com.noheltcj.rxcommon.subjects

import com.noheltcj.rxcommon.Emitter

class SubjectEmitter<E> : Emitter<E>() {
  /**
   * Will return true when the subject has already emitted the completed event
   */
  var isCompleted = false
    internal set

  /**
   * Will return true in case the subject was terminated with an error
   */
  var isTerminated = false
    internal set
}