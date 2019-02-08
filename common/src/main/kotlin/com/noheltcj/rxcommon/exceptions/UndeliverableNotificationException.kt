package com.noheltcj.rxcommon.exceptions

class UndeliverableNotificationException(val notification: Notification) : RuntimeException(
    "Unable to deliver notification: $notification. Emitter has already finished."
) {
    sealed class Notification {
        object Completed : Notification()
        data class Terminated(val reason: Throwable) : Notification()
    }
}