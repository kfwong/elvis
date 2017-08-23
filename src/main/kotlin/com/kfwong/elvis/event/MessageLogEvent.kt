package com.kfwong.elvis.event

class MessageLogEvent(message: String) : BaseEvent() {

    private val m = message

    override fun toString(): String {
        return prefix() + m
    }

}