package com.noheltcj.rxcommon

interface Observer<El, Em : Emitter<El>> {
  fun subscribeTo(source: Source<El, Em>)
}