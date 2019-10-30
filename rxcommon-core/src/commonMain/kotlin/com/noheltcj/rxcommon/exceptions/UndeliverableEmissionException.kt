package com.noheltcj.rxcommon.exceptions

class UndeliverableEmissionException(val undeliverableEmission: Any?) : RuntimeException(
    "Unable to deliver emission: $undeliverableEmission. Emitter has already finished."
)