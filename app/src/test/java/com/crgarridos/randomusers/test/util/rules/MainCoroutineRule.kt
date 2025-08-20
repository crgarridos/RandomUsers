package com.crgarridos.randomusers.test.util.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.ExternalResource

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : ExternalResource() {

    override fun before() {
        super.before()
        Dispatchers.setMain(testDispatcher)
    }

    override fun after() {
        super.after()
        Dispatchers.resetMain()
    }
}