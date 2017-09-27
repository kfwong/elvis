package com.kfwong.elvis.event

import java.text.SimpleDateFormat
import java.util.*

abstract class BaseEvent {
    val datetime = Date()
    val formattedDatetime = SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(datetime)
    open val eventMessage = "(no message)"
    open val type = "INFO"

    override fun toString(): String {
        return eventMessage
    }
}