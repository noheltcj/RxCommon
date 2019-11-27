# RxCommon Binding

In the spirit of keeping things as simple as possible, some new
companion libraries will be becoming available as the need arises.

## Basics

### BindingRelay
This library introduces 
[BindingRelay](src/commonMain/kotlin/com/noheltcj/rxcommon/binding/relay/BindingRelay.kt)
which preserves the same functionality as 
[BehaviorRelay](../rxcommon-core/src/commonMain/kotlin/com/noheltcj/rxcommon/subjects/BehaviorRelay.kt)
and includes a very simple binding technique. It works by using a specialized
[Emitter](../rxcommon-core/src/commonMain/kotlin/com/noheltcj/rxcommon/emitters/Emitter.kt)
that can emit ignoring a specified 
[Observer](../rxcommon-core/src/commonMain/kotlin/com/noheltcj/rxcommon/observers/Observer.kt).

The goal of BindingRelay is to serve as a source of presentation
events. External information sources such as UI can easily bind 
uni/bi-directionally using a BiDirectionalBinding

### Binding Example
```kotlin

```

## Android LiveData
