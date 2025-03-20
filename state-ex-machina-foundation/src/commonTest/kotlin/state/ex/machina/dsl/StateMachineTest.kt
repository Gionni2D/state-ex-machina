package state.ex.machina.dsl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Reducer
import state.ex.machina.foundation.State
import state.ex.machina.foundation.store
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
        data object Load: TestIntent
        data class Store(val valueToAdd: Int): TestIntent
    }

    class TestRepository {
        var counter: Int = 42

        suspend fun loadCounter(): Int {
            delay(100)
            return counter
        }

        suspend fun storeCounter(counter: Int) {
            delay(100)
            this.counter = counter
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun TestScope.initTest(
        initialState: TestState,
        intents: Flow<TestIntent>
    ): Pair<StateFlow<TestState>, TestRepository> {
        val repository = TestRepository()

        return stateMachine(
            store = store(initialState),
            intents = intents,
            coroutineScope = backgroundScope
        ) {
            on<TestIntent.Increment>() updateState { intent ->
                Reducer { state ->
                    state.copy(counter = state.counter + 1)
                }
            }

            on<TestIntent.Decrement>() updateState Reducer { state ->
                state.copy(counter = state.counter - 1)
            }

            on<TestIntent.Load>() sideEffect {
                val loadedCounter = repository.loadCounter()
                updateState { state ->
                    state.copy(counter = loadedCounter)
                }
            }

            on<TestIntent.Store>() sideEffect { intent ->
                val counterToStore = currentState.counter + intent.valueToAdd
                repository.storeCounter(counterToStore)
            }
        } to repository
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
            val (stateMachine) = initTest(
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
            val (stateMachine) = initTest(
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

    @Test
    fun `test side effect`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val intents = MutableSharedFlow<TestIntent>()
        val initialState = TestState(counter = 0)
        val (stateMachine, repository) = initTest(
            initialState = initialState,
            intents = intents,
        )

        // When
        val repositoryInit = repository.counter
        val stateInit = stateMachine.value

        intents.emit(TestIntent.Load)
        val stateAfterLoadIntent = stateMachine.value
        delay(100)
        val stateAfterLoadEnd = stateMachine.value

        intents.emit(TestIntent.Store(valueToAdd = 10))
        val stateAfterStoreIntent = stateMachine.value
        delay(100)
        val stateAfterStoreEnd = stateMachine.value

        // Then
        assertEquals(0, stateInit.counter)
        assertEquals(0, stateAfterLoadIntent.counter)
        assertEquals(42, stateAfterLoadEnd.counter)
        assertEquals(42, stateAfterStoreIntent.counter)
        assertEquals(42, stateAfterStoreEnd.counter)
        assertEquals(42, repositoryInit)
        assertEquals(52, repository.counter)
    }
}
