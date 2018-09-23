package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.Source

/**
 * While this doesn't follow kotlin convention, the Operators creates better native interfaces
 */
object OperatorHelpers {
  fun <S1, S2> combineLatest(sourceOne: Source<S1>, sourceTwo: Source<S2>): Source<Pair<S1, S2>> =
      sourceOne.combineLatest(sourceTwo) {
        e1, e2 -> e1 to e2
      }
}

fun <S1, S2> Source<S1>.combineLatest(otherSource: Source<S2>): Source<Pair<S1, S2>> =
    combineLatest(otherSource) {
      e1, e2 -> e1 to e2
    }
