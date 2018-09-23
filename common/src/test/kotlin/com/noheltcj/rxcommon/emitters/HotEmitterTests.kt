package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
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
  @JsName("givenObserver_whenCompleted_shouldNotifyObserver")
  fun `given observer, when completed, should notify observer`() {
    emitter.addObserver(testObserver)

    emitter.complete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenObserverAndTerminated_whenTerminatedAgain_shouldDoNothing")
  fun `given observer and terminated, when terminated again, should throw`() {
    val expectedThrowable = Throwable("crackle")
    emitter.addObserver(testObserver)
    emitter.terminate(expectedThrowable)

    emitter.terminate(Throwable("hiss"))

    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenObserverAndTerminated_whenCompleted_shouldDoNothing")
  fun `given observer and terminated, when completed, should do nothing`() {
    val expectedThrowable = Throwable("expected")
    emitter.addObserver(testObserver)
    emitter.terminate(expectedThrowable)

    emitter.complete()

    testObserver.assertNotComplete()
    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenObserverAndCompleted_whenCompletedAgain_shouldDoNothing")
  fun `given observer and completed, when completed again, should do nothing`() {
    emitter.addObserver(testObserver)
    emitter.complete()

    emitter.complete()

    testObserver.assertComplete()
  }

  @Test
  @JsName("givenObserverAndCompleted_whenTerminated_shouldDoNothing")
  fun `given observer and completed, when terminated, should do nothing`() {
    emitter.addObserver(testObserver)
    emitter.complete()

    emitter.terminate(Throwable())

    testObserver.assertComplete()
    testObserver.assertNotTerminated()
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