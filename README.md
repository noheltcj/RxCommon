# RxCommon
A multi-platform ReactiveX implementation targetting JVM, iOS, Android, and JS.

More targets can be added upon request.

## Documentation
Please refer to <https://reactivex.io> for documentation.

### Sources
The reactivex documentation covers much of the functionality. If there are any significant discrepancies,
excluding those illuminated within this documentation, please post an issue.

* [Observable](<http://reactivex.io/documentation/observable.html>)
* Single - Similar to observable, but will complete when the first value is emitted.
* BehaviorRelay - [Subject](http://reactivex.io/documentation/subject.html) which ignores all notifications. Relays 
retain and emit the latest element.
* [BehaviorSubject](http://reactivex.io/documentation/subject.html) - Similar to the BehaviorRelay, but acknowledges
notifications
* [PublishSubject](http://reactivex.io/documentation/subject.html)
* More coming, _new collaborators / contributions are greatly appreciated_.

### Operators
More operators are coming quickly, but not all have been implemented.

Currently supported operators:
* [map](http://reactivex.io/documentation/operators/map.html)
* [filter](http://reactivex.io/documentation/operators/filter.html)
* [flatMap](http://reactivex.io/documentation/operators/flatmap.html)
* switchMap (non-interleaving variant of [FlatMap](http://reactivex.io/documentation/operators/flatmap.html))
* [combineLatest](http://reactivex.io/documentation/operators/combinelatest.html)
* [onErrorReturn](http://reactivex.io/documentation/operators/catch.html)
* [toSingle](http://reactivex.io/documentation/operators/first.html)
* [first](http://reactivex.io/documentation/operators/first.html)

### Examples
```kotlin
Single(just = "hello")
  .map { "$it world" }
  .subscribe(NextObserver { result ->
    // result => "hello world"
  })

/* Be sure to dispose when this is no longer needed to prevent leaks. */
val disposable = Observable<String>(createWithEmitter = { emitter ->
  emitter.next("we're happy")
  emitter.next("la la la")

  Disposables.create {
    /*
     * This block is called when this cold observable loses all of its observers or
     * a notification is received. Use this to clean up any open connections, etc.
     */
  }
}).flatMap { happyText ->
  /* Use the text to maybe fetch something from an api. */
  return@flatMap Single<String>(error = UnauthorizedException()) // Uh oh, expired access
    .onErrorReturn { throwable ->
      /* Handle throwable, maybe check for unauthorized and recover */
      return@onErrorReturn Single(just = "$happyText recovery")
    }
}.subscribe(NextTerminalObserver({ emission ->
  /*
   * emission => "we're happy recovery"
   * emission => "la la la recovery"
   */
}, { throwable ->
  /* No terminal notifications in this example */
}))
```

## Installing

Please ensure you're using gradle 5.3+.

Installing has recently become significantly easier. Now it's as simple as including
the following:

### Kotlin Build Script

```kotlin 
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.noheltcj:rxcommon:1.0.0-rc1")
            }
        }
    }
}
```

### Groovy Build Script

```kotlin 
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api 'com.noheltcj:rxcommon:1.0.0-rc1'
            }
        }
    }
}
```

### Kotlin Support Map (For Native)

Since native modules require dependencies to be compiled with the same kotlin version,
we will be keeping up with this support map going forward.

```
0.4.2 -> 1.3.20
0.5.0 -> 1.3.21
0.5.1 -> 1.3.21
0.5.2 -> 1.3.30
0.5.3 -> 1.3.31
1.0.0-rc1 -> 1.3.50
```

### Objective-C Generics
Objective-c only has partial generics support, so we lose a bit of 
information when this library is imported as a framework in XCode.

To help with this, when you produce an Objective-C framework, be sure to
enable generics support.

```^groovy
components.main {
    outputKinds("framework")
    extraOpts "-Xobjc-generics"
}
```

### Concurrency
This library doesn't support concurrency. In the majority of cases, concurrency is
a side effect that can be handled on the platform. If you are doing anything that
requires a significant amount of time to operate, it's important to do this work
off the main thread (Especially if your application has a user interface). Of course
do that using other resources such as RxSwift, RxJava, or basic platform concurrency
frameworks, but ensure you've returned to the main thread before re-entering the common code.
