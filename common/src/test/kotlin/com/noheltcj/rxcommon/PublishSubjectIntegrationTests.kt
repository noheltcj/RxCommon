package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.PublishSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test

class PublishSubjectIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun setup() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("whenSubscribing_shouldNotEmit")
  fun `when subscribing, should not emit`() {
    PublishSubject<String>().subscribe(testObserver)
    testObserver.assertValues(emptyList())
    testObserver.assertNotComplete()
    testObserver.assertNotTerminated()
  }

  @Test
  @JsName("givenSubscribed_whenNewValuePublished_shouldEmitNewValue")
  fun `given subscribed, when a new value published, should emit new value`() {
    PublishSubject<String>().apply {
      subscribe(testObserver)
      publish("2")
    }
    testObserver.assertValue("2")
  }

  @Test
  @JsName("givenNotSubscribed_andAValuePublished_whenSubscribing_shouldNotEmit")
  fun `given not subscribed and a value was published, when subscribing, should not emit`() {
    PublishSubject<String>().apply {
      publish("2")
      subscribe(testObserver)
    }
    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenUpstreamHasNotEmitted_whenSubscribing_shouldNotEmit")
  fun `given upstream has not emitted, when subscribing, should not emit`() {
    PublishSubject<String>().apply {
      subscribeTo(Observable())
      subscribe(testObserver)
    }
    testObserver.assertNoEmission()
  }

  @Test
  @JsName("givenUpstreamHasCompleted_whenSubscribing_shouldNotify")
  fun `given upstream has completed, when subscribing, should notify`() {
    PublishSubject<String>().apply {
      subscribeTo(Observable(completeOnSubscribe = true))
      subscribe(testObserver)
    }
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenUpstreamHasTerminated_whenSubscribing_shouldNotify")
  fun `given upstream has terminated, when subscribing, should notify`() {
    val expectedThrowable = Throwable("POW!")
    PublishSubject<String>().apply {
      subscribeTo(Observable(error = expectedThrowable))
      subscribe(testObserver)
    }
    testObserver.assertTerminated(expectedThrowable)
  }
}