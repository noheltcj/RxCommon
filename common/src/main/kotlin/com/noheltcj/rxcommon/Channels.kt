package com.noheltcj.rxcommon

fun <S1, S2, R> Channel<S1>.combineLatest(otherSource: Channel<S2>, transform: (S1, S2) -> R): Channel<R> {
  return Transformation.Combine(this, otherSource, Channel.CombinationMode.CombineLatest, transform)
}