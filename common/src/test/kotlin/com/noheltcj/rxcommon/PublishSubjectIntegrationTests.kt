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
      onNext("2")
    }
    testObserver.assertValue("2")
  }

  @Test
  @JsName("givenNotSubscribed_andAValuePublished_whenSubscribing_shouldNotEmit")
  fun `given not subscribed and a value was published, when subscribing, should not emit`() {
    PublishSubject<String>().apply {
      onNext("2")
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
  @JsName("givenSubscribedWithUpstream_whenUpstreamCompletes_shouldNotify")
  fun `given subscribed with upstream, when upstream completes, should notify`() {
    PublishSubject<String>().apply {
      subscribe(testObserver)
      subscribeTo(Observable(completeOnSubscribe = true))
    }

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedWithUpstream_whenUpstreamTerminates_shouldNotify")
  fun `given subscribed with upstream, when upstream terminates, should notify`() {
    val expectedThrowable = Throwable("POW!")
    PublishSubject<String>().apply {
      subscribe(testObserver)
      subscribeTo(Observable(error = expectedThrowable))
    }

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenSubscribed_whenCompleted_shouldNotify")
  fun `given subscribed, when completed, should notify`() {
    PublishSubject<String>().apply {
      subscribe(testObserver)
    }.onComplete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToColdUpstreamSource_whenCompleted_shouldDisposeUpstream")
  fun `given subscribed to cold upstream source, when completed, should dispose upstream`() {
    val upstream = Observable<String>()
    PublishSubject<String>().apply {
      subscribeTo(upstream)
    }.onComplete()

    upstream.subscribe(testObserver)

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribedToColdUpstreamSource_whenTerminated_shouldDisposeUpstream")
  fun `given subscribed to cold upstream source, when terminated, should dispose upstream`() {
    val upstream = Observable<String>()
    PublishSubject<String>().apply {
      subscribeTo(upstream)
    }.onError(Throwable())

    upstream.subscribe(testObserver)

    testObserver.assertComplete()
  }
}