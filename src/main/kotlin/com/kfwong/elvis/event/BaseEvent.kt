package com.kfwong.elvis.event

import java.text.SimpleDateFormat
import java.util.*

abstract class BaseEvent {
    private val datetime = Date()
    open val eventMessage = "(no message)"

    override fun toString(): String {

        val formattedDatetime = SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(datetime)

        return "[$formattedDatetime] $eventMessage"
    }
}