package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableCompletionException
import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.exceptions.UndeliverableTerminationException
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.*

class HotEmitterTests {
  private lateinit var emitter: HotEmitter<Int>
  private lateinit var testObserver: TestObserver<Int>

  @BeforeTest
  fun beforeEach() {
    emitter = HotEmitter()
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenObserver_whenNext_shouldEmitToObserver")
  fun `given observer, when next called, should emit to observer`() {
    emitter.addObserver(testObserver)

    emitter.next(1)

    testObserver.assertValue(1)
  }

  @Test
  @JsName("givenObserverAndTerminated_whenNext_shouldThrow")
  fun `given observer and terminated, when next, should throw`() {
    val expectedException = Throwable("boom")
    emitter.addObserver(testObserver)
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
  @JsName("givenObserverAndCompleted_whenNext_shouldThrow")
  fun `given observer and completed, when next, should throw`() {
    emitter.addObserver(testObserver)
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
    emitter.addObserver(testObserver)

    emitter.terminate(expectedThrowable)

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenObserverAndTerminated_whenTerminated_shouldThrow")
  fun `given observer and terminated, when terminated again, should throw`() {
    val expectedThrowable = Throwable("crackle")
    emitter.addObserver(testObserver)
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
  @JsName("givenObserverAndCompleted_whenTerminated_shouldNotNotifyObserver")
  fun `given observer and completed, when terminated, should not notify observer`() {
    emitter.addObserver(testObserver)
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
    emitter.addObserver(testObserver)

    emitter.complete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenObserverAndTerminated_whenCompleted_shouldThrow")
  fun `given observer and terminated, when completed, should throw`() {
    val expectedThrowable = Throwable("expected")
    emitter.addObserver(testObserver)
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
  @JsName("givenObserverAndCompleted_whenCompletedAgain_shouldThrow")
  fun `given observer and completed, when completed again, should throw`() {
    emitter.addObserver(testObserver)
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

  @Test
  @JsName("givenObserverAddedAndRemoved_whenAddingAnotherObserver_shouldBeCompleted")
  fun `given observer added and removed, when adding another observer, should not be completed`() {
    val emptyObserver = NextObserver<Int> {}
    emitter.addObserver(emptyObserver)
    emitter.removeObserver(emptyObserver)

    assertFalse(emitter.isCompleted)
  }

  @Test
  @JsName("givenObserverAddedAndRemoved_whenAnotherObserverAdded_shouldAcceptNewEmissions")
  fun `given observer added and removed, when adding another observer, should accept new emissions`() {
    val emptyObserver = NextObserver<Int> {}
    emitter.addObserver(emptyObserver)
    emitter.removeObserver(emptyObserver)

    emitter.addObserver(testObserver)

    emitter.next(1)

    testObserver.assertValue(1)
  }
}