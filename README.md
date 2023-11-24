# State Ex Machina

State Ex Machina is a MVI-like library written in Kotlin for Android.

**Why do we need another MVI library?**\
Because we simply couldn't find one that was easy to start working with, lightweight and that would cover enough use cases.

**Core concepts**
  - state machine built on kotlin ```Flow``` to handle and store state changes  
  - reactive entities at the base for both states and intents
  - clear DSL with few core directives, open to customisation
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

Intents represent user intentions, for example the intention to type a number.

```kotlin
sealed interface CounterIntent : Intent {
    data class TypeNumber(val number: Int) : CounterIntent
    object Count : CounterIntent
    object SaveTotal : CounterIntent
}
```

### Define the State

State represents a photo of all the dynamic information needed to present the view and for the model to interact with the domain (and update itself)

```kotlin
data class CounterState(
    val currentCounter: Int = 0,
    val total: Int = 0
) : State
```

### Define the Reducers

Reducers are pure functions that takes in input the old state and return a new state. In these functions is where you want to define the state update logic. 
ReducerFactory is an abstraction on reducers that we're adopting to try to divide as much as possible the state update logic from the Model, using High Order Functions.

```kotlin
interface CounterReducerFactory {
    fun updateCounter(c: Int): Reducer<CounterState>
    val updateTotal: Reducer<CounterState>
}

class CounterReducerFactoryImpl : CounterReducerFactory {
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

The model holds the representation of the state and updates it with the reducers, it's the layer responsible for most of the business logic.

1. Create a model that implements the MVI `Model` and override the function `subscribeTo`,
    this is the scope where you can update the state and call coroutines
2. Use `on` to react to user intents
3. You can update the state with `updateState` or use `sideEffect` to elaborate data from a repository and more
4. More functions are present in the library to cover most common use-cases (eg: `launchedEffect` always execute code when the function `subscribeTo` of the Model is called)

**Model is immutable, every function or variable declared inside its scope should only be called in `subscribeTo`**

```kotlin
import com.gionni2d.mvi.foundation.Model
import com.gionni2d.mvi.dsl.stateMachine
import com.gionni2d.mvi.dsl.updateState 

class CounterModel(
    private val coroutineScope: CoroutineScope
) : Model<CounterState, CounterIntent> {
    private val reducers: CounterReducerFactory = CounterReducerFactoryImpl()
    private val repository: CounterRepository = CounterRepository()
    private val _uiEffect: MutableSharedFlow<CounterUIEffect> = MutableSharedFlow()
    val uiEffect: Flow<CounterUIEffect> = _uiEffect.toSharedFlow()
    
    override fun subscribeTo(intents: Flow<CounterIntent>) = stateMachine(
        initialState = CounterState(),
        intents = intents,
        coroutineScope = coroutineScope,
    ) {
        on<CounterIntent.TypeNumber>() updateState { reducers.updateCounter(it.number) }

        on<CounterIntent.Count>() updateState reducers.updateTotal

        on<CounterIntent.SaveTotal>() sideEffect {
            repository.saveTotal(currentState.total)
            _uiEffect.emit(CounterUIEffect.ShowTotalSavedNotification)
        }
    }
}
```

With the library extension for Android ViewModel we can utilize the `stateMachine` that calls for `viewModelScope` as coroutine scope.

```kotlin
import com.gionni2d.mvi.viewmodel.stateMachine

class CounterModel : ViewModel(), Model<CounterState, CounterIntent> {

    override fun subscribeTo(intents: Flow<CounterIntent>) = stateMachine(
        initialState = CounterState(),
        intents = intents
    ) {
        // 
    }
}
```


### Wire up the Model with Jetpack Compose

```kotlin
@Composable
fun CounterScreen(model: Model<CounterState, CounterIntent>) {
    val (state, onIntent) = rememberMviComponent(model)

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
