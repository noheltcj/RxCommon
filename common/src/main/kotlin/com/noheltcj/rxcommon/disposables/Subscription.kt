package com.noheltcj.rxcommon.disposables

import com.noheltcj.rxcommon.Emitter

interface Subscription<El> : Disposable {
  val emitter: Em
}
