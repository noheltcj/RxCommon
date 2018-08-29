package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObservableIntegration {
  private lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  fun `givenEmptyConstructor_whenSubscribing_shouldNotBeCompleted`() {
    Observable<String>().subscribe(testObserver)
    testObserver.assertNotComplete()
  }

  @Test
  fun `givenCompletedConstructorTrue_whenSubscribing_shouldBeCompleted`() {
    Observable<String>(completeOnSubscribe = true).subscribe(testObserver)
    testObserver.assertComplete()
  }
}