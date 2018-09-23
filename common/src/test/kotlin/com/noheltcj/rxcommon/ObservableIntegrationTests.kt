package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObservableIntegrationTests {
  private lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun beforeEach() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenEmptyConstructorUsed_whenSubscribing_shouldNotBe")
  fun `given empty constructor used, when subscribing, should not be completed`() {
    Observable<String>().subscribe(testObserver)

    testObserver.assertNotComplete()
  }

  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andNextEmitted_whenSubscribing_shouldEmitTheValue")
  fun `given create with emitter constructor used, and next emitted, when subscribing, should emit the value`() {
    Observable<String> { emitter ->
      emitter.next("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertValue("element")
  }

  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andNextEmitted_whenSubscribing_shouldNotBeTerminated")
  fun `given create with emitter constructor used, and next emitted, when subscribing, should not be terminated`() {
    Observable<String> { emitter ->
      emitter.next("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertNotTerminated()
  }


  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andNextEmitted_whenSubscribing_shouldNotBeComplete")
  fun `given create with emitter constructor used, and next emitted, when subscribing, should not be complete`() {
    Observable<String> { emitter ->
      emitter.next("element")

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertNotComplete()
  }


  @Test
  @JsName("givenCreateWithEmitterConstructorUsed_andTerminateCalled_whenSubscribing_shouldBeTerminated")
  fun `given create with emitter constructor used, and terminate called, when subscribing, should be terminated`() {
    val expectedThrowable = Throwable("boom boom")
    Observable<String> { emitter ->
      emitter.terminate(expectedThrowable)

      Disposables.empty()
    }.subscribe(testObserver)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenErrorConstructorUsed_whenSubscribing_shouldBeTerminated")
  fun `given error constructor used, when subscribing, should be terminated`() {
    val expectedError = Throwable("terminal error")

    Observable<String>(error = expectedError).subscribe(testObserver)

    testObserver.assertTerminated(expectedError)
  }

  @Test
  @JsName("givenCompleteOnSubscribeConstructorUsed_whenSubscribing_shouldNotifyComplete")
  fun `given complete on subscribe constructor used, when subscribing, should notify complete`() {
    Observable<String>(completeOnSubscribe = true).subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenJustConstructorUsed_whenSubscribing_shouldEmitTheElementAndComplete")
  fun `given just constructor used, when subscribing, should emit the element and complete`() {
    Observable(just = "nextItem").subscribe(testObserver)

    testObserver.assertValues(listOf("nextItem"))
    testObserver.assertComplete()
  }
}