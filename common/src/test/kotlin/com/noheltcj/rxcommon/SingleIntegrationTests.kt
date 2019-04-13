package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.observables.Single
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SingleIntegrationTests {
  private lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenEmptyConstructorUsed_whenSubscribing_shouldNotEmit")
  fun `given empty constructor used, when subscribing, should not emit`() {
    Single<String>().subscribe(testObserver)

    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andNextEmitted_whenSubscribing_shouldEmitTheValueAndComplete")
  fun `given create with emitter constructor used, and next emitted, when subscribing, should emit the value and complete`() {
    Single<String> { emitter ->
      emitter.success("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertValue("element")
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andNextEmitted_whenSubscribing_shouldNotBeTerminated")
  fun `given create with emitter constructor used, and next emitted, when subscribing, should not be terminated`() {
    Single<String> { emitter ->
      emitter.success("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertNotTerminated()
  }

  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andTerminateCalled_whenSubscribing_shouldBeTerminated")
  fun `given create with emitter constructor used, and terminate called, when subscribing, should be terminated`() {
    val expectedThrowable = Throwable("boom boom")
    Single<String> { emitter ->
      emitter.terminate(expectedThrowable)

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenErrorConstructorUsed_whenSubscribing_shouldBeTerminated")
  fun `given error constructor used, when subscribing, should be terminated`() {
    val expectedError = Throwable("terminal error")

    Single<String>(error = expectedError).subscribe(testObserver)

    testObserver.assertTerminated(expectedError)
  }

  @Test
  @JsName("givenJustConstructorUsed_whenSubscribing_shouldEmitTheElementAndComplete")
  fun `given just constructor used, when subscribing, should emit the element and complete`() {
    Single(success = "nextItem").subscribe(testObserver)

    testObserver.assertValues(listOf("nextItem"))
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andEmitted_whenSubscribing_shouldDispose")
  fun `given create with emitter constructor used and emitted, when subscribing, should dispose`() {
    var didDispose = false
    val disposable = Disposables.create {
      didDispose = true
    }
    Single<String> { emitter ->
      emitter.success("element")

      disposable
    }.subscribe(testObserver)

    assertTrue(didDispose)
  }

  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andTerminated_whenSubscribing_shouldDispose")
  fun `given create with emitter constructor used and terminated, when subscribing, should dispose`() {
    var didDispose = false
    val disposable = Disposables.create {
      didDispose = true
    }
    Single<String> { emitter ->
      emitter.terminate(Throwable())

      disposable
    }.subscribe(testObserver)

    assertTrue(didDispose)
  }
}