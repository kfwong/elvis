package com.kfwong.elvis.event

class DownloadCompletedEvent : BaseEvent(){

    override fun toString(): String {
        return prefix() + "Download completed!"
    }
}