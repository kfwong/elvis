package com.kfwong.elvis

import com.kfwong.elvis.ui.Ui
import com.kfwong.elvis.util.API_KEY
import com.kfwong.elvis.util.prefs
import javafx.application.Application

fun main(args: Array<String>) {

    val ELVIS_HOME: String = prefs.get("ELVIS_HOME", System.getProperty("user.home") + "/elvis/")
    val AUTH_TOKEN: String = prefs.get("AUTH_TOKEN", "(not set)")

    println("Welcome to ELVIS!")
    println("API_KEY: $API_KEY")
    println("ELVIS_HOME: $ELVIS_HOME")
    println("AUTH_TOKEN: $AUTH_TOKEN")

    Application.launch(Ui::class.java, *args)
}