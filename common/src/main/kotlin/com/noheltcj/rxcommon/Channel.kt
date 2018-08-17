package com.noheltcj.rxcommon

interface Channel<Element> {
  fun subscribe(operation : (Element) -> Unit) : Subscription<Element>
  fun unsubscribe(subscription: Subscription<Element>)

  fun <Transformed> map(transformation: (Element) -> Transformed) : Channel<Transformed>
  fun <Transformed, OtherElement> combine(otherSource: Channel<OtherElement>,
                                          combinationMode: CombinationMode,
                                          transform: (Element, OtherElement) -> Transformed): Channel<Transformed>

  enum class CombinationMode {
    CombineLatest /** http://reactivex.io/documentation/operators/combinelatest.html */
  }
}