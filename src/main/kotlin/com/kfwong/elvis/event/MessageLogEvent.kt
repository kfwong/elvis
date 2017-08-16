package com.kfwong.elvis.event

import java.util.*

class MessageLogEvent(message: String) {

    val datetime = Date()
    val content = message

}