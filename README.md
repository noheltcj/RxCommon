# RxCommon
A multi-platform (Native, JVM, iOS, macOS, and JS) implementation of ReactiveX.

## Documentation
Please refer to <https://reactivex.io> for documentation. 

While this is currently only a partial implementation, I'm doing my best 
to follow the spec as closely as possible. 

### Sources
The reactivex documentation covers much of the functionality. If there are any significant discrepancies,
excluding those I've illuminated within this documentation, please post an issue.

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
* [Map](http://reactivex.io/documentation/operators/map.html)
* [FlatMap](http://reactivex.io/documentation/operators/flatmap.html)
* SwitchMap (non-interleaving variant of [FlatMap](http://reactivex.io/documentation/operators/flatmap.html))
* [CombineLatest](http://reactivex.io/documentation/operators/combinelatest.html)

## Installing
There are several places requiring imports to utilize this library.

### Common Module
```groovy
implementation "com.noheltcj:rx-common:0.3.0"
```

### JVM Module
```groovy
implementation "com.noheltcj:rx-common-jvm:0.3.0"
```

### JavaScript Module
```groovy
implementation "com.noheltcj:rx-common-js:0.3.0"
```

### Native Module
Slightly more complicated. See the [Native Distribution Limitation](#native-library-distribution)

## Temporary Limitations
As this is a new project with only one contributor, I haven't had time 
to implement many of the things we've come to expect from a complete Rx
implementation.

### Native Library Distribution
I haven't had time to fully work out distribution via maven central for 
the native kotlin library in kotlin/native projects.

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
transfer any data back to the designated thread. I personally use the 
existing platform specific implementations of Rx (RxSwift, RxJava, etc) 
to do this.
