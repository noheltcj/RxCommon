package com.noheltcj.rxcommon.emitters

import com.noheltcj.rxcommon.exceptions.UndeliverableEmissionException
import com.noheltcj.rxcommon.observers.NextObserver
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SingleEmitterTests {
  private lateinit var emitter: SingleEmitter<Int>
  private lateinit var testObserver: TestObserver<Int>

  @BeforeTest
  fun beforeEach() {
    emitter = SingleEmitter {}
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
  @JsName("givenObserver_whenNext_shouldComplete")
  fun `given observer, when next, should complete`() {
    emitter.addObserver(testObserver)

    emitter.next(1)

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
  @JsName("givenObserverAndEmitted_whenNext_shouldThrow")
  fun `given observer and emitted, when next, should throw`() {
    emitter.addObserver(testObserver)
    emitter.next(1)

    var capturedException: UndeliverableEmissionException? = null
    try {
      emitter.next(2)
    } catch (e: UndeliverableEmissionException) {
      capturedException = e
    }

    assertNotNull(capturedException)
    assertEquals(2, capturedException!!.undeliverableEmission)
    testObserver.assertValue(1)
    testObserver.assertComplete()
    testObserver.assertNotTerminated()
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
  @JsName("givenObserverAndEmitted_whenTerminated_shouldDoNothing")
  fun `given observer and emitted, when terminated, should do nothing`() {
    emitter.addObserver(testObserver)
    emitter.next(0)

    emitter.terminate(Throwable())

    testObserver.assertComplete()
    testObserver.assertNotTerminated()
  }

  @Test
  @JsName("givenEmission_whenObserverAdded_shouldEmit")
  fun `given emission, when observer added, should emit`() {
    emitter.next(1)

    emitter.addObserver(testObserver)

    testObserver.assertValue(1)
  }

  @Test
  @JsName("givenEmission_whenObserverAdded_shouldComplete")
  fun `given emission, when observer added, should complete`() {
    emitter.next(1)

    emitter.addObserver(testObserver)

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