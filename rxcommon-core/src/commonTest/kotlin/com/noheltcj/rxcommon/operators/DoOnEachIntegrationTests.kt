package com.noheltcj.rxcommon.operators

import com.noheltcj.rxcommon.observables.Observable
import com.noheltcj.rxcommon.utility.JsName
import com.noheltcj.rxcommon.utility.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnEachIntegrationTests {
    lateinit var testObserver: TestObserver<String>
    private var emissionCounter = 0

    @BeforeTest
    fun beforeEach() {
        testObserver = TestObserver()
    }

    @Test
    @JsName("givenDoOnNextIncrementsEmissionCounter_whenSubscribing_counterWhouldBeIncremented")
    fun `given doOnNext increment emission counter, emission counter should be incremented`() {
        Observable(just = "Emissed")
                .doOnNext { ++emissionCounter }
                .subscribe(testObserver)
        testObserver.assertValue("Emissed")
        assertEquals(emissionCounter, emissionCounter)
    }
}