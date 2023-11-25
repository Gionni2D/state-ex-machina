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
The user wants to add two numbers and see the result of the sum.\
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
sealed interface SumIntent : Intent {
    data class TypeFirstNumber(val firstNumber: Int) : SumIntent
    data class TypeSecondNumber(val secondNumber: Int) : SumIntent
    object Sum : SumIntent
    object SaveSum : SumIntent
}
```

### Define the State

State represents a photo of all the dynamic information needed to present the view and for the model to interact with the domain (and update itself)

```kotlin
data class SumState(
    val firstNumber: Int = 0,
    val secondNumber: Int = 0,
    val sum: Int = 0
) : State
```

### Define the Reducers

Reducers are pure functions that takes in input the old state and return a new state. In these functions is where you want to define the state update logic. 
ReducerFactory is an abstraction on reducers that we're adopting to try to divide as much as possible the state update logic from the Model, using High Order Functions.

```kotlin
interface SumReducersFactory {
    fun updateFirstNumber(n: Int): Reducer<SumState>
    fun updateSecondNumber(n: Int): Reducer<SumState>
    val updateSum: Reducer<SumState>
}

class SumReducersFactoryImpl : SumReducersFactory {
    override fun updateFirstNumber(
        n: Int
    ) = Reducer<SumState> { s ->
        s.copy(firstNumber = n)
    }

    override fun updateSecondNumber(
        n: Int
    ) = Reducer<SumState> { s ->
        s.copy(secondNumber = n)
    }

    override val updateSum = Reducer<SumState> { s ->
        s.copy(sum = s.firstNumber + s.secondNumber)
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

class SumModel(
    private val coroutineScope: CoroutineScope
) : Model<SumState, SumIntent> {
    private val reducers: SumReducersFactory = SumReducersFactoryImpl()
    private val repository: SumRepository = SumRepository()
    private val _uiEffect: MutableSharedFlow<SumUIEffect> = MutableSharedFlow()
    val uiEffect: Flow<SumUIEffect> = _uiEffect.toSharedFlow()
    
    override fun subscribeTo(intents: Flow<SumIntent>) = stateMachine(
        initialState = SumState(),
        intents = intents,
        coroutineScope = coroutineScope,
    ) {
        on<SumIntent.TypeFirstNumber>() updateState { reducers.updateFirstNumber(it.firstNumber) }

        on<SumIntent.Sum>() updateState reducers.updateSum

        on<SumIntent.SaveSum>() sideEffect {
            repository.saveSum(currentState.sum)
            _uiEffect.emit(SumUIEffect.ShowSumSavedNotification)
        }
    }
}
```

With the library extension for Android ViewModel we can utilize the `stateMachine` that calls for `viewModelScope` as coroutine scope.

```kotlin
import com.gionni2d.mvi.viewmodel.stateMachine

class SumModel : ViewModel(), Model<SumState, SumIntent> {

    override fun subscribeTo(intents: Flow<SumIntent>) = stateMachine(
        initialState = SumState(),
        intents = intents
    ) {
        // 
    }
}
```


### Wire up the Model with Jetpack Compose

```kotlin
@Composable
fun SumScreen(model: Model<SumState, SumIntent>) {
    val (stateFlow, onIntent) = rememberMviComponent(model)
    val state by stateFlow.collectAsState()

    SumScreen(
        state = state,
        onTypeFirstNumber = { SumIntent.TypeFirstNumber(it).let(onIntent) },
        onTypeSecondNumber = { SumIntent.TypeSecondNumber(it).let(onIntent) },
        onSum = { SumIntent.Sum.let(onIntent) },
        onSaveSum = { SumIntent.SaveSum.let(onIntent) },
    )
}

@Composable
private fun SumScreen(
    state: SumState,
    onTypeFirstNumber: (Int) -> Unit,
    onTypeSecondNumber: (Int) -> Unit,
    onSum: () -> Unit,
    onSaveSum: () -> Unit,
) {
    // render UI using data from 'state' and wire intents to UI components actions
}
```
