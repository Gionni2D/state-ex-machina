package state.ex.machina.dsl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Reducer
import state.ex.machina.foundation.State
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StateMachineTest {

    private data class TestState(
        val counter: Int
    ) : State

    private sealed interface TestIntent : Intent {
        data object Increment : TestIntent
        data object Decrement : TestIntent
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun TestScope.testStateMachine(
        initialState: TestState,
        intents: Flow<TestIntent>
    ) = stateMachine(
        initialState = initialState,
        intents = intents,
        coroutineScope = backgroundScope
    ) {
        on<TestIntent.Increment>() updateState { intent ->
            Reducer { currentState ->
                currentState.copy(counter = currentState.counter + 1)
            }
        }

        on<TestIntent.Decrement>() updateState Reducer { currentState ->
            currentState.copy(counter = currentState.counter - 1)
        }
    }


    // Each vector is a list of intent to emit and the relative updated state
    private val vectorsUpdateState = listOf(
        emptyList(),
        listOf(
            TestIntent.Increment to 1
        ),
        listOf(
            TestIntent.Increment to 1,
            TestIntent.Increment to 2,
            TestIntent.Decrement to 1,
            TestIntent.Decrement to 0,
        )
    )

    @Test
    fun `test state update after each intent`() = runTest(UnconfinedTestDispatcher()) {
        vectorsUpdateState.forEach { vector ->
            // Given
            val intents = MutableSharedFlow<TestIntent>()
            val initialState = TestState(counter = 0)
            val stateMachine = testStateMachine(
                initialState = initialState,
                intents = intents
            )

            // When
            val stateInit = stateMachine.value
            val states = vector.map { (intent) ->
                intents.emit(intent)
                stateMachine.value
            }

            // Then
            assertEquals(0, stateInit.counter)
            vector.mapIndexed { i, (_, expected) ->
                assertEquals(expected, states[i].counter)
            }
        }
    }

    @Test
    fun `test last state update after intents`() = runTest(UnconfinedTestDispatcher()) {
        vectorsUpdateState.filter { it.isNotEmpty() }.forEach { vector ->
            // Given
            val intents = MutableSharedFlow<TestIntent>()
            val initialState = TestState(counter = 0)
            val stateMachine = testStateMachine(
                initialState = initialState,
                intents = intents,
            )

            // When
            vector.forEach { (intent) ->
                intents.emit(intent)
            }

            // Then
            val (_, expected) = vector.last()
            assertEquals(expected, stateMachine.value.counter)
        }
    }
}
