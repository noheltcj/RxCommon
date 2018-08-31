package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class MapOperatorIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  fun `given mapped source, should emit nothing`() {
    Observable<Int>()
        .map { it.toString() }
        .subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  fun `given mapped source, when the source emits, should emit mapped value`() {
    Observable(just = 10)
        .map { it.toString() }
        .subscribe(testObserver)

    testObserver.assertValue("10")
  }
}