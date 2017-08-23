package com.kfwong.elvis.controller

import com.kfwong.elvis.event.DownloadCompletedEvent
import com.kfwong.elvis.event.DownloadingEvent
import com.kfwong.elvis.event.MessageLogEvent
import com.kfwong.elvis.util.Constants.eventBus
import com.kfwong.elvis.util.Constants.prefs

open class BaseController {

    fun resetRegistry(isResettingAuthToken: Boolean = false, isResettingElvisHome: Boolean = false) {
        val originalAuthToken = prefs.get("AUTH_TOKEN", "(not set)")
        val originalElvisHome = prefs.get("ELVIS_HOME", System.getProperty("user.home") + "/elvis/")

        prefs.clear()

        prefs.put("AUTH_TOKEN", if (isResettingAuthToken) "(not set)" else originalAuthToken)
        prefs.put("ELVIS_HOME", if (isResettingElvisHome) System.getProperty("user.home") + "/elvis/" else originalElvisHome)
    }

    fun setAuthToken(authToken: String) {
        prefs.put("AUTH_TOKEN", authToken)
    }

    fun setElvisHome(directory: String) {
        prefs.put("ELVIS_HOME", directory)
    }

    fun publishMessageLogEvent(message:String){
        eventBus.post(MessageLogEvent(message))
    }

    fun publishDownloadingEvent(){
        eventBus.post(DownloadingEvent())
    }

    fun publishDownloadCompletedEvent(){
        eventBus.post(DownloadCompletedEvent())
    }

}