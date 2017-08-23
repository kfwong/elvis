package com.kfwong.elvis.event

class DownloadingEvent : BaseEvent(){

    override fun toString(): String {
        return prefix() + "Downloading..."
    }
}