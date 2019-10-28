package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source

fun <S1, S2> Source<S1>.combineLatest(sourceTwo: Source<S2>): Source<Pair<S1, S2>> =
    combineLatest(sourceTwo) { e1, e2 ->
        e1 to e2
    }
