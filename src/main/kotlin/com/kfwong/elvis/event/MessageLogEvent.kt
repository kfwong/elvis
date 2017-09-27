package com.kfwong.elvis.event

class MessageLogEvent(message: String, override val type: Type = Type.INFO) : BaseEvent() {
    override val eventMessage = message
}