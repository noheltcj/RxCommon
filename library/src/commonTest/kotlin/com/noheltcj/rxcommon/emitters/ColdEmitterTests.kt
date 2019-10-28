package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.*

class ColdEmitterTests {
  private lateinit var emitter: ColdEmitter<Int>
  private lateinit var testObserver: TestObserver<Int>

  @BeforeTest
  fun beforeEach() {
    emitter = ColdEmitter {}
    testObserver = TestObserver()
  }

  @Test
  @JsName("givenObserver_whenNext_shouldEmit")
  fun `given observer, when next, should emit`() {
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
    assertEquals(1, capturedException.undeliverableEmission)
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
    assertEquals(1, capturedException.undeliverableEmission)
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
  fun `given observer and terminated, when terminated again, should do nothing`() {
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
  @JsName("givenEmissions_whenObserverAdded_shouldEmitAll")
  fun `given emissions, when observer added, should emit all`() {
    emitter.next(1)
    emitter.next(2)

    emitter.addObserver(testObserver)

    testObserver.assertValues(listOf(1, 2))
  }

  @Test
  @JsName("givenEmissionsAndTerminated_whenObserverAdded_shouldEmitAllAndNotify")
  fun `given emissions and terminated, when observer added, should emit all and notify`() {
    val expectedThrowable = Throwable("terminated")
    emitter.next(1)
    emitter.next(2)
    emitter.terminate(expectedThrowable)

    emitter.addObserver(testObserver)

    testObserver.assertValues(listOf(1, 2))
    testObserver.assertTerminated(expectedThrowable)
  }

  @Test
  @JsName("givenEmissionsAndCompleted_whenObserverAdded_shouldEmitAllAndNotify")
  fun `given emissions and completed, when observer added, should emit all and notify`() {
    emitter.next(1)
    emitter.next(2)
    emitter.complete()

    emitter.addObserver(testObserver)

    testObserver.assertValues(listOf(1, 2))
    testObserver.assertComplete()
  }

  @Test
  @JsName("givenCompletedByRemovingObservers_whenObserverAdded_shouldNotify")
  fun `given completed by removing observers, when observer added, should notify`() {
    val emptyObserver = NextObserver<Int> {}
    emitter.addObserver(emptyObserver)
    emitter.removeObserver(emptyObserver)
    emitter.addObserver(testObserver)

    testObserver.assertComplete()
  }
}
