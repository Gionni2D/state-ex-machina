# State Ex Machina

State Ex Machina is a MVI-like library written in Kotlin for Android.

**Why do we need another MVI library?**\
Because we simply couldn't find one that was easy to start working with, lightweight and that would cover enough use cases.

**Core concepts**
  - state machine built on kotlin ```Flow``` to handle and store state changes  
  - reactive state that can be applied to the UI 
  - declarative model definition, how and why?
  - clear DSL
  - lightweight

## Getting started

Here's a simple example to show off the fundaments on which the library is based on.\ 
The user wants to insert a number, sum it with the previous inserted number (initial value is zero) and see the result.\
Lastly saving the total sum with a network call.

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

Intents represent user intentions: the actions with which the user achieves an objective

```kotlin
sealed interface CounterIntent : Intent {
    data class TypeNumber(val number: Int) : CounterIntent
    object Count : CounterIntent
    object SaveTotal : CounterIntent
}
```

### Define the State

State represents the current condition of the data and UI elements 

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

1. Create a model that implements the MVI `Model` and override the function `subscribeTo`,
    this is the scope where you can update the state and call coroutines
2. Use `on` to react to user intents
3. You can update the state with `updateState` or use `sideEffect` to elaborate data from a repository and more
4. alternatively you can call `launchedEffect` to always execute code when the viewModel is created TODO(?)

```kotlin
import com.gionni2d.mvi.foundation.Model
import com.gionni2d.mvi.dsl.stateMachine
import com.gionni2d.mvi.dsl.updateState 

class CounterModel(
    private val coroutineScope: CoroutineScope
) : Model<CounterState, CounterIntent> {
    private val reducers: CounterReducer = CounterReducersImpl()
    private val repository: CounterRepository = CounterRepository()
    
    override fun subscribeTo(intents: Flow<CounterIntent>) = stateMachina(
        store = store(),
        intents = intents,
        coroutineScope = coroutineScope,
    ) {
        on<CounterIntent.TypeNumber>() updateState { reducers.updateCounter(it.number) }

        on<CounterIntent.Count>() updateState reducers.updateTotal

        on<CounterIntent.SaveTotal>() sideEffect {
            repository.saveTotal(currentState.total)
        }
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
