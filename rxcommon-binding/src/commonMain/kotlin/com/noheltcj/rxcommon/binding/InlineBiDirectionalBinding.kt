package com.noheltcj.rxcommon.binding

import com.noheltcj.rxcommon.binding.relay.BindingRelay

class InlineBiDirectionalBinding<E, U>(
    onNext: (E) -> Unit,
    relay: BindingRelay<U>,
    private val transformForUpstreamBlock: (E) -> U,
    private val transformForSubscriberBlock: (U) -> E
) : BiDirectionalBinding<E, U>(relay, onNext) {
    override fun transformForUpstream(localData: E): U = transformForUpstreamBlock(localData)
    override fun transformForSubscriber(emission: U): E = transformForSubscriberBlock(emission)
}