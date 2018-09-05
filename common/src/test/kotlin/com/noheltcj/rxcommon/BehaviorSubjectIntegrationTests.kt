package com.noheltcj.rxcommon

import com.noheltcj.rxcommon.disposables.Disposables
import com.noheltcj.rxcommon.emitters.Emitter
import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.subjects.BehaviorSubject
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class BehaviorSubjectIntegrationTests {
  lateinit var testObserver: TestObserver<String>

  @BeforeTest
  fun setup() {
    testObserver = TestObserver()
  }

  @Test
  @JsName("whenSubscribing_shouldEmitTheSeed")
  fun `when subscribing, should emit the seed`() {
    BehaviorSubject("seed").subscribe(testObserver)
    testObserver.assertValue("seed")
  }

  @Test
  @JsName("givenSubscribed_whenNewValuePublished_shouldEmitNewValue")
  fun `given subscribed, when a new value published, should emit new value`() {
    BehaviorSubject("1").apply {
      subscribe(testObserver)
      onNext("2")
    }
    testObserver.assertValues(listOf("1", "2"))
  }

  @Test
  @JsName("givenNoObservers_andSubsequentElementPublished_whenSubscribing_shouldEmitTheLastValue")
  fun `given no observers and subsequent element published, when subscribing, should emit the last value`() {
    BehaviorSubject("seed").apply {
      onNext("elementOne")
      onNext("elementTwo")

      subscribe(testObserver)
    }
    testObserver.assertValue("elementTwo")
  }

  @Test
  @JsName("givenUpstreamHasNotEmitted_whenSubscribing_shouldEmitTheSeed")
  fun `given upstream has not emitted, when subscribing, should emit the seed`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable())
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
  }

  @Test
  @JsName("givenUpstreamHasEmitted_whenSubscribing_shouldEmitTheUpstreamElement")
  fun `given upstream has emitted, when subscribing, should emit the upstream element`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(just = "upstream"))
      subscribe(testObserver)
    }
    testObserver.assertValue("upstream")
  }

  @Test
  @JsName("givenUpstreamHasEmittedComplete_whenSubscribing_shouldEmitSeedAndComplete")
  fun `given upstream has emitted complete, when subscribing, should emit seed and complete`() {
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(completeOnSubscribe = true))
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenUpstreamHasTerminated_whenSubscribing_shouldEmitSeedAndTerminate")
  fun `given upstream has terminated, when subscribing, should emit seed and terminate`() {
    val expectedThrowable = Throwable("POW!")
    BehaviorSubject("seed").apply {
      subscribeTo(Observable(error = expectedThrowable))
      subscribe(testObserver)
    }
    testObserver.assertValue("seed")
    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenSubscribedToUpstreamSource_whenUpstreamSourceCompletes_shouldNotify")
  fun `given subscribed to upstream source, when upstream source completes, should notify`() {
    val upstream = Observable<String>(completeOnSubscribe = true)
    BehaviorSubject("hi").apply {
      subscribe(testObserver)
      subscribeTo(upstream)
    }

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribed_whenComplete_shouldNotify")
  fun `given subscribed, when disposed, should notify`() {
    BehaviorSubject("hello").apply {
      subscribe(testObserver)
    }.onComplete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenSubscribed_whenTerminated_shouldNotify")
  fun `given subscribed, when terminated, should notify`() {
    val expectedThrowable = Throwable()
    BehaviorSubject("hello").apply {
      subscribe(testObserver)
    }.onError(expectedThrowable)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenOnlyObserverOfColdUpstreamSource_whenCompleted_shouldCompleteUpstream")
  fun `given subscribed to cold upstream source, when disposed, should dispose upstream`() {
    lateinit var upstreamEmitter: Emitter<String>
    val upstream = Observable<String>(createWithEmitter = {
      upstreamEmitter = it
      Disposables.empty()
    })
    BehaviorSubject("bye").apply {
      subscribeTo(upstream)
    }.onComplete()

    var undeliverableException: UndeliverableEmissionException? = null
    try {
      upstreamEmitter.next("already disposed")
    } catch (e: UndeliverableEmissionException) {
      undeliverableException = e
    }
    assertNotNull(undeliverableException)

    testObserver.assertNoEmission()
  }
}