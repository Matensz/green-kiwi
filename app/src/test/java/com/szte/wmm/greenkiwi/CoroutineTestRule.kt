package com.szte.wmm.greenkiwi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Custom JUnit rule to control setting up dispatchers for testing view models.
 */
@ExperimentalCoroutinesApi
class CoroutineTestRule(private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()): TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher) {

    /**
     * Switches the main dispatcher to the testDispatcher before test runs..
     */
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    /**
     * Resets the main dispatcher after test runs.
     */
    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}
