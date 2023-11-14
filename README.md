# State Ex Machina

State Ex Machina is a MVI-like library written in Kotlin for Android.
TODO
- Why another MVI library?
- Core concepts
  - state machine, how and why?
  - reactive, how and why?
  - declarative model definition, how and why?
  - good DSL
  - lightweight

## Getting started

TODO
- explain the example

### Define the dependencies

```kotlin
// MVI foundation
implementation("com.state-ex-machina:core:<latest-version>")
// Jetpack Compose MVI extensions
implementation("com.state-ex-machina:ext-compose:<latest-version>")
// Android ViewModel MVI extensions
implementation("com.state-ex-machina:ext-view-model:<latest-version>")
```

### Define the Intents

TODO
- Explain what is an intent

```kotlin
sealed interface CounterIntent : Intent {
    data class TypeNumber(val number: Int) : CounterIntent
    object Count : CounterIntent
}
```

### Define the State

TODO
- Explain what is a state 

```kotlin
data class CounterState(
    val currentCounter: Int = 0,
    val total: Int = 0
) : State
```

### Define the Reducers

Reducers is where you want to define the state update logic, save the business logic for the model

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

### Create the Model

TODO
- add imports
- fix code
- add sideEffect example with counterRepository

1. Create a model that implements the MVI `Model` and override the function `subscribeTo`,
    this is the scope where you can update the state and call coroutines
2. Use `on` to react to user intents
3. You can update the state with `updateState` or use `sideEffect` to elaborate data from a repository and more
4. alternatively you can call `launchedEffect` to always execute code when the viewModel is created TODO(?)

```kotlin
import *
// ...

class CounterModel(
    private val coroutineScope: CoroutineScope
) : Model<CounterState, CounterIntent> {
    private val reducers: CounterReducer = CounterReducersImpl()
    private val repository: CounterRepository = ...
    
    override fun subscribeTo(intents: Flow<CounterIntent>) = stateMachina(
        store = store(),
        intents = intents,
        coroutineScope = coroutineScope,
    ) {
        on<CounterIntent.TypeNumber>() updateState { reducers.updateCounter(it.number) }

        on<CounterIntent.Count>() updateState reducers.updateTotal
    }
}
```

### Wire up the Model with Jetpack Compose

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
