package com.noheltcj.rxcommon.exceptions

class UndeliverableTerminationException(val undeliverableTerminalError: Throwable) : RuntimeException(
    "Unable to deliver exception: ${undeliverableTerminalError.message}. Emitter has already finished."
)