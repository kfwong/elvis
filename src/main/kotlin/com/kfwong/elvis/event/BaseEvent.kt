package com.kfwong.elvis.event

import java.text.SimpleDateFormat
import java.util.*

abstract class BaseEvent {
    val datetime = Date()

    protected fun prefix(): String {

        val formattedDatetime = SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(datetime)

        return "[$formattedDatetime] "
    }
}