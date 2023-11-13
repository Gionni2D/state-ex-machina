# state-ex-machine
State Ex Machine is a Redux / MVI-like library written in Kotlin for Android.

## Getting started

```kotlin
implementation("com.state-ex-machine:core:<latest-version>")
```

### Define the Intents

```kotlin
sealed interface CounterIntent : Intent {
    data class TypeNumber(val number: Int) : CounterIntent
    object Count : CounterIntent
}
```

TODO Should I explain what is an intent ?

### Define the State

```kotlin
data class CounterState(
    val currentCounter: Int = 0,
    val total: Int = 0
) : State
```

### Create the ViewModel and Reducers

1. Override the function subscribeTo, this is the scope where you can update the state and call coroutines 
2. Use `on` to react to user intents
3. You can update the state with `updateState` or use `sideEffect` to elaborate data from a repository and more
4. alternatively you can call `launchedEffect` to always execute code when the viewModel is created TODO(?)

```kotlin
class CounterViewModel(
    private val store: Store<CounterState>,
    private val reducers: CounterReducers,
) : ViewModel(), Model<CounterState, CounterIntent> {
    override fun subscribeTo(intents: Flow<CounterIntent>) = stateMachine(
        store = store,
        intents = intents
    ) {
        on<CounterIntent.TypeNumber>() updateState { reducers.updateCounter(it.number) }

        on<CounterIntent.Count>() updateState reducers.updateTotal
    }
}
```

Reducers is where you want to update the state, save the business logic for the viewmodel

```kotlin
interface CounterReducers {
    fun updateCounter(c: Int): Reducer<CounterState>
    val updateTotal: Reducer<CounterState>
}

class CounterReducersImpl : CounterReducers {
    override fun updateCounter(
        c: Int
    ) = Reducer<CounterState> { s ->
        s.copy(currentCounter = c)
    }

    override val updateTotal = Reducer<CounterState> { s ->
        s.copy(total = s.currentCounter + s.total)
    }
}
```

### Wire up the ViewModel with Jetpack Compose

```kotlin
@Composable
fun CounterScreen(model: Model<CounterState, CounterIntent>) {
    val (stateFlow, onIntent) = rememberMviComponent(model)
    val state by stateFlow.collectAsState()

    CounterScreen(
        state = state,
        onTypeNumber = { CounterIntent.TypeNumber(it).let(onIntent) },
        onCount = { CounterIntent.Count.let(onIntent) }
    )
}

@Composable
private fun CounterScreen(
    state: CounterState,
    onTypeNumber: (Int) -> Unit,
    onCount: () -> Unit,
) {
    // render UI using data from 'state' and wire intents to UI components actions
}
```


