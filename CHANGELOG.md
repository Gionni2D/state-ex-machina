# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] - 2025-05-13

### Added

- **Kotlin Multiplatform** support (jvm, android, ios, linux)

### Changed

- *Foundation* - rename `IntentStateMachineScope.on` parameter `intentFilterPredicate`in `predicate
- *Compose extension* - rename `rememberMviComponent` in `rememberStateMachine`

### Removed

- *Foundation* - `Flow.applyReducer` extensions
- *Foundation* - `stateMachine` overload with `initialState` parameter
- *ViewModel extension* - `stateMachine` overload with `initialState` parameter

## [0.1.0] - 2024-07-30

### Added

- *Foundation* module, with MVI core models and state machine DSL implementation
- *ViewModel* extension module, with simple utilities that take advantage of `viewModelScope` to create the state machine
- *Compose* extension module, which simplify the integration with Jetpack Compose UI Framework

[Unreleased]: https://github.com/Gionni2D/state-ex-machina/compare/0.2.0...HEAD
[0.2.0]: https://github.com/Gionni2D/state-ex-machina/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/Gionni2D/state-ex-machina/releases/tag/0.1.0
