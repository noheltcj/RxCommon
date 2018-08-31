package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.operators.combineLatest
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class CombineLatestIntegrationTests {
  lateinit var testObserver: TestObserver<Pair<Int, String>>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  fun `given two combined cold sources, when subscribing, should emit nothing`() {
    val combinedObs = Observable<Int>().combineLatest(Observable<String>())

    combinedObs.subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  fun `given two combined cold sources and only one emitted, when subscribing, should emit nothing`() {
    val combinedObs = Observable(just = 1).combineLatest(Observable<String>())

    combinedObs.subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  fun `given two combined cold sources and both have emitted, when subscribing, should emit a value`() {
    val combinedObs = Observable(just = 1).combineLatest(Observable(just = "1"))

    combinedObs.subscribe(testObserver)

    testObserver.assertValue(1 to "1")
  }

  @Test
  fun `given subscribed to two combined cold sources and both have emitted, when one emits again, should emit two values`() {
    val stringObs = Observable<String>(createWithEmitter = {
      it.next("1")
      it.next("2")
      Disposables.empty()
    })
    val combinedObs = Observable(just = 1).combineLatest(stringObs)

    combinedObs.subscribe(testObserver)

    testObserver.assertValues(listOf(
        1 to "1",
        1 to "2"
    ))
  }

  @Test
  fun `given subscribed to two combined cold sources and both have emitted, when both emit again, should emit three values`() {
    lateinit var emitterOne: Emitter<Int>
    lateinit var emitterTwo: Emitter<String>

    val intObs = Observable<Int>(createWithEmitter = {
      it.next(1)
      emitterOne = it
      Disposables.empty()
    })
    val stringObs = Observable<String>(createWithEmitter = {
      it.next("1")
      emitterTwo = it
      Disposables.empty()
    })

    intObs.combineLatest(stringObs).subscribe(testObserver)

    emitterOne.next(2)
    emitterTwo.next("2")

    testObserver.assertValues(listOf(
        1 to "1",
        2 to "1",
        2 to "2"
    ))
  }
}