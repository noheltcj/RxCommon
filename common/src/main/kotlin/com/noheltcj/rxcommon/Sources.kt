package com.noheltcj.rxcommon

//fun <S1, S2, R> Source<S1, Observer<S1>>.combineLatest(otherSource: Source<S2>, transform: (S1, S2) -> R): Source<R> =
//    Operator.Combine(this, otherSource, Source.CombinationMode.CombineLatest, transform)
//
//fun <S1, S2> Source<S1>.combineLatest(otherSource: Source<S2>): Source<Pair<S1, S2>> =
//    Operator.Combine(this, otherSource, Source.CombinationMode.CombineLatest) {
//      e1, e2 -> e1 to e2
//    }
