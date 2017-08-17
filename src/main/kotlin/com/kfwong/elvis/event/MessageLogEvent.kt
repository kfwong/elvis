package com.kfwong.elvis.event

import java.util.*

class MessageLogEvent(message: String) : BaseEvent() {

    private val m = message

    override fun toString(): String {
        return prefix() + m
    }

}