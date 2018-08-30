package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposables
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
  fun `given empty constructor used, when subscribing, should not be completed`() {
    Observable<String>().subscribe(testObserver)

    testObserver.assertNotComplete()
  }

  @Test
  fun `given create with emitter constructor used, and next emitted, when subscribing, should emit the value`() {
    Observable<String> { emitter ->
      emitter.next("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertValue("element")
  }

  @Test
  fun `given create with emitter constructor used, and next emitted, when subscribing, should not be terminated`() {
    Observable<String> { emitter ->
      emitter.next("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertNotTerminated()
  }


  @Test
  fun `given create with emitter constructor used, and next emitted, when subscribing, should not be complete`() {
    Observable<String> { emitter ->
      emitter.next("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertNotComplete()
  }


  @Test
  fun `given create with emitter constructor used, and error emitted, when subscribing, should be terminated`() {
    val expectedThrowable = Throwable("boom boom")
    Observable<String> { emitter ->
      emitter.terminate(expectedThrowable)

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  fun `given error constructor used, when subscribing, should be terminated with the correct error`() {
    val expectedError = Throwable("terminal error")

    Observable<String>(error = expectedError).subscribe(testObserver)

    testObserver.assertTerminated(expectedError)
  }

  @Test
  fun `given complete on subscribe constructor used, when subscribing, should be completed`() {
    Observable<String>(completeOnSubscribe = true).subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  fun `given just constructor used, when subscribing, should emit the element and complete`() {
    Observable(just = "nextItem").subscribe(testObserver)

    testObserver.assertValues(listOf("nextItem"))
    testObserver.assertComplete()
  }
}