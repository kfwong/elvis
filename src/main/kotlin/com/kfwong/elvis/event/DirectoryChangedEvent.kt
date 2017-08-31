package com.kfwong.elvis.event

class DirectoryChangedEvent(val newPath: String) : BaseEvent() {

    override val eventMessage = "Download directory changed to $newPath!"

}