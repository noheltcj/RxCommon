package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableCompletionException
import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.exceptions.UndeliverableTerminationException
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.*

class HotEmitterIntegrationTests {
  private lateinit var emitter: HotEmitter<Int>
  private lateinit var testObserver: TestObserver<Int>

  @BeforeTest
  fun beforeEach() {
    emitter = HotEmitter()
    testObserver = TestObserver()

    emitter.addObserver(testObserver)
  }

  @Test
  @JsName("givenObserver_whenNext_shouldEmitToObserver")
  fun `given observer, when next called, should emit to observer`() {
    emitter.next(1)

    testObserver.assertValue(1)
  }

  @Test
  @JsName("givenTerminated_whenNext_shouldThrow")
  fun `given terminated, when next, should throw`() {
    val expectedException = Throwable("boom")
    emitter.terminate(expectedException)

    var capturedException: UndeliverableEmissionException? = null
    try {
      emitter.next(1)
    } catch (e: UndeliverableEmissionException) {
      capturedException = e
    }

    assertNotNull(capturedException)
    assertEquals(1, capturedException!!.undeliverableEmission)
    testObserver.assertValues(emptyList())
    testObserver.assertTerminated(expectedException)
  }

  @Test
  @JsName("givenCompleted_whenNext_shouldThrow")
  fun `given completed, when next, should throw`() {
    emitter.complete()

    var capturedException: UndeliverableEmissionException? = null
    try {
      emitter.next(1)
    } catch (e: UndeliverableEmissionException) {
      capturedException = e
    }

    assertNotNull(capturedException)
    assertEquals(1, capturedException!!.undeliverableEmission)
    testObserver.assertValues(emptyList())
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenObserver_whenTerminated_shouldNotifyObserver")
  fun `given observer, when terminated, should notify observer`() {
    val expectedThrowable = Throwable("pop")

    emitter.terminate(expectedThrowable)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenTerminated_whenTerminated_shouldThrow")
  fun `given terminated, when terminated again, should throw`() {
    val expectedThrowable = Throwable("crackle")
    emitter.terminate(expectedThrowable)

    var capturedException: UndeliverableTerminationException? = null
    try {
      emitter.terminate(Throwable("hiss"))
    } catch (e: UndeliverableTerminationException) {
      capturedException = e
    }

    assertNotNull(capturedException)
    assertEquals("hiss", capturedException!!.undeliverableTerminalError.message)
    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenCompleted_whenTerminated_shouldNotNotifyObserver")
  fun `given completed, when terminated, should not notify observer`() {
    emitter.complete()

    var capturedException: UndeliverableTerminationException? = null
    try {
      emitter.terminate(Throwable("hiss"))
    } catch (e: UndeliverableTerminationException) {
      capturedException = e
    }

    assertNotNull(capturedException)
    assertEquals("hiss", capturedException!!.undeliverableTerminalError.message)
    testObserver.assertNotTerminated()
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenObserver_whenCompleted_shouldNotifyObserver")
  fun `given observer, when completed, should notify observer`() {
    emitter.complete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenTerminated_whenCompleted_shouldThrow")
  fun `given terminated, when completed, should throw`() {
    val expectedThrowable = Throwable("expected")
    emitter.terminate(expectedThrowable)

    var capturedException: UndeliverableCompletionException? = null
    try {
      emitter.complete()
    } catch (e: UndeliverableCompletionException) {
      capturedException = e
    }

    assertNotNull(capturedException)
    testObserver.assertNotComplete()
    testObserver.assertTerminated(expectedThrowable)
  }


  @Test
  @JsName("givenCompleted_whenCompletedAgain_shouldThrow")
  fun `given completed, when completed again, should throw`() {
    emitter.complete()

    var capturedException: UndeliverableCompletionException? = null
    try {
      emitter.complete()
    } catch (e: UndeliverableCompletionException) {
      capturedException = e
    }

    assertNotNull(capturedException)
    testObserver.assertComplete()
  }
}