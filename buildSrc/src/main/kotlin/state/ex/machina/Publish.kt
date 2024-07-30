package state.ex.machina

object Publish {
    const val GROUP_ID = "io.github.gionni2d"
    const val VERSION = "0.1.0"

    const val ARTIFACT_ID_FOUNDATION = "state-ex-machina-foundation"
    const val ARTIFACT_ID_VIEWMODEL = "state-ex-machina-ext-viewmodel"
    const val ARTIFACT_ID_COMPOSE = "state-ex-machina-ext-compose"

    val pomData = mapOf(
        ARTIFACT_ID_FOUNDATION to PomData(
            name = "State Ex Machina - foundation",
            description = "MVI-like library written in Kotlin for Android",
        ),
        ARTIFACT_ID_VIEWMODEL to PomData(
            name = "State Ex Machina - View Model extensions",
            description = "Some utilities to better fit Android View Model with State Ex Machina",
        ),
        ARTIFACT_ID_COMPOSE to PomData(
            name = "State Ex Machina - Compose extensions",
            description = "Some utilities to better fit Jetpack Compose with State Ex Machina",
        ),
    )

    data class PomData(
        val name: String,
        val description: String
    )
}