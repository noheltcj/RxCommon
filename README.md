# RxCommon
A multi-platform (Native, JVM, iOS, macOS, and JS) ReactiveX implementation.

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
* More coming, _I would gladly accept new collaborators / contributions_.

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
There are several places requiring imports to utilize this library.

### Common Module
```groovy
implementation "com.noheltcj:rx-common:0.5.1"
```

### JVM Module
```groovy
implementation "com.noheltcj:rx-common-jvm:0.5.1"
```

### JavaScript Module
```groovy
implementation "com.noheltcj:rx-common-js:0.5.1"
```

### Native Module
Slightly more complicated. See the [Native Distribution Limitation](#native-library-distribution)

Since native modules require dependencies to be compiled with the same kotlin version,
we will be keeping up with this support map going forward.

**RxCommon to Kotlin Stdlib Version Support Map**:
```
0.4.2 -> 1.3.20
0.5.x -> 1.3.21
```

## Temporary Limitations
As this is a new project with only a couple of contributors, we haven't had time 
to implement many of the things many have come to expect from a complete Rx
implementation, but open up a pull request to solve any issues and we'll work through it.

### Native Library Distribution
Distribution via maven central for the native kotlin library in kotlin/native 
projects hasn't been implemented yet, but you can still use this in native projects.

_You can find the pre-built kotlin libraries zipped in the release tag for each
 version._

To install this and successfully produce a framework which can be 
distributed for use in XCode projects, you'll need to manually install
the .klib files for your target architectures.

For example, the following gradle script looks for the files in in the 
lib directory of the kotlin/native project.

```groovy
apply plugin: 'konan'

konanArtifacts {
    framework('Example', targets: ['ios_x64', 'ios_arm64']) {
        extraOpts '-module_name', 'EX'
        enableMultiplatform true

        target('ios_x64') {
            libraries {
                useRepo 'lib/ios_x64'
                noStdLib true // Avoids linker issues
                klib 'RxCommon'
            }
        }

        target('ios_arm64') {
            libraries {
                useRepo 'lib/ios_arm64'
                noStdLib true
                klib 'RxCommon'
            }
        }
    }
}
```

### Objective-C Generics
Objective-c only has partial generics support, so we lose a bit of 
information when this library is imported as a framework in XCode.

### Concurrency
There is absolutely no thread safety or scheduling in the library yet, 
but it's on the to-do list. In the meantime, it's best to keep any 
application state and logic that utilizes this library on one thread. 
This doesn't mean you can't still operate on different threads, just 
transfer any data back to a single designated thread. I personally use the 
existing platform specific implementations of Rx (RxSwift, RxJava, etc) 
combined with platform scheduling (ExecutorService, DispatchQueue, etc) to do this.
