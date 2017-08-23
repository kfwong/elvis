package com.kfwong.elvis.event

class MessageLogEvent(message: String) : BaseEvent() {
    override val eventMessage = message
}