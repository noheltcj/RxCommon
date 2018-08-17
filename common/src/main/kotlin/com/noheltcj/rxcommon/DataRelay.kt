package com.noheltcj.rxcommon

class DataRelay<Element> constructor(private val emitOnSubscribe: Boolean = false) : Channel<Element> {
  var value: Element? = null
    private set

  private val activeSubscriptions = mutableListOf<Subscription<Element>>()

  fun publish(value: Element) {
    this.value = value
    activeSubscriptions.forEach { it.operation(value) }
  }

  fun publishOmitting(subscription: Subscription<Element>, value: Element) {
    this.value = value
    activeSubscriptions
        .filter { it != subscription }
        .forEach { it.operation(value) }
  }

  override fun subscribe(operation: (Element) -> Unit) : Subscription<Element> {
    return Subscription(this, operation)
        .also {
          activeSubscriptions.add(it)
          if (emitOnSubscribe) {
            value?.run(operation)
          }
        }
  }

  override fun unsubscribe(subscription: Subscription<Element>) {
    activeSubscriptions.remove(subscription)
  }

  override fun <Transformed> map(transformation: (Element) -> Transformed): Channel<Transformed> {
    return Transformation.Map(this, transformation)
  }

  override fun <Transformed, OtherElement> combine(
      otherSource: Channel<OtherElement>,
      combinationMode: Channel.CombinationMode,
      transform: (Element, OtherElement) -> Transformed): Channel<Transformed> {
    return Transformation.Combine(this, otherSource, combinationMode, transform)
  }
}