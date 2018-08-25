package com.noheltcj.rxcommon

sealed class Operator<E, U>(protected val upSource: Source<U>) : Source<E> {
  protected val activeSubscriptions = mutableListOf<Subscription<E>>()
  protected var upstreamDisposable: Disposable? = null

  override fun subscribe(operation: (E) -> Unit): Subscription<E> {
    return Subscription(this, operation)
        .also { activeSubscriptions.add(it) }
  }

  override fun unsubscribe(subscription: Subscription<E>) {
    upstreamDisposable?.dispose()
    activeSubscriptions.remove(subscription)
  }

  override fun <T> map(transformation: (E) -> T): Source<T> {
    return Map(this, transformation)
  }

  override fun <T, O> combine(otherSource: Source<O>, combinationMode: Source.CombinationMode, transform: (E, O) -> T): Source<T> {
    return Combine(this, otherSource, combinationMode, transform)
  }

  class Map<E, R>(upSource: Source<E>, private val mapTransform: (E) -> R): Operator<R, E>(upSource) {
    override fun subscribe(operation: (R) -> Unit): Subscription<R> {
      upstreamDisposable = upSource.subscribe {
        operation(mapTransform(it))
      }
      return super.subscribe(operation)
    }
  }

  class Combine<S1, S2, R>(
      private val sourceOne: Source<S1>,
      private val sourceTwo: Source<S2>,
      private val combinationMode: Source.CombinationMode,
      private inline val transform: (S1, S2) -> R
  ) : Operator<R, S1>(sourceOne) {

    private val compositeDisposeBag = CompositeDisposeBag()
    private var sourceOneLastElement: S1? = null
    private var sourceTwoLastElement: S2? = null

    override fun subscribe(operation: (R) -> Unit): Subscription<R> {
      when (combinationMode) {
        Source.CombinationMode.CombineLatest -> {
          compositeDisposeBag.add(sourceOne.subscribe {
            sourceOneLastElement = it
            sourceTwoLastElement?.run { operation(transform(it, this)) }
          })
          compositeDisposeBag.add(sourceTwo.subscribe {
            sourceTwoLastElement = it
            sourceOneLastElement?.run { operation(transform(this, it)) }
          })
        }
      }

      return super.subscribe(operation)
    }

    override fun unsubscribe(subscription: Subscription<R>) {
      compositeDisposeBag.dispose()
      activeSubscriptions.remove(subscription)
    }
  }
}