package com.kfwong.elvis.ui

import com.google.common.eventbus.Subscribe
import com.kfwong.elvis.controller.BaseController
import com.kfwong.elvis.controller.ElvisController
import com.kfwong.elvis.event.DownloadCompletedEvent
import com.kfwong.elvis.event.DownloadingEvent
import com.kfwong.elvis.event.MessageLogEvent
import com.kfwong.elvis.util.Constants.API_KEY
import com.kfwong.elvis.util.Constants.prefs
import com.kfwong.elvis.util.Constants.eventBus
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import tornadofx.*
import java.io.File

class Ui : App() {
    override val primaryView = Gui::class

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)

        eventBus.post(MessageLogEvent("Welcome to ELVIS!"))
        eventBus.post(MessageLogEvent("Application started."))

        val AUTH_TOKEN: String = prefs.get("AUTH_TOKEN", "(not set)")
        if (AUTH_TOKEN != "(not set)") {
            eventBus.post(MessageLogEvent("User has login"))
        }
    }
}

class Gui : View() {
    override val root: BorderPane by fxml("/fxml/Gui.fxml")

    private val login: Button by fxid()
    private val download: Button by fxid()
    private val forceDownload: Button by fxid()
    private val changeDirectory: Button by fxid()
    private val messageLog: TextArea by fxid()
    private val exit: Button by fxid()

    lateinit var controller: ElvisController

    init {
        this.title = "elvis"

        eventBus.register(this)

        login.action {
            find(Login::class).openModal()
        }

        download.action {
            downloadAction(false)
        }

        forceDownload.action {
            downloadAction(true)
        }

        changeDirectory.action {
            find(ChangeDirectory::class).openModal()
        }

        exit.action {
            System.exit(0)
        }

    }

    private fun downloadAction(isForceDownload: Boolean = false){
        val ELVIS_HOME: String = prefs.get("ELVIS_HOME", System.getProperty("user.home") + "/elvis/")
        val AUTH_TOKEN: String = prefs.get("AUTH_TOKEN", "(not set)")

        if (AUTH_TOKEN != "(not set)") {
            controller = ElvisController(API_KEY, AUTH_TOKEN, ELVIS_HOME)
            controller.download(isForceDownload)
        } else {
            controller.publishMessageLogEvent("You must login with your NUSNET account first!")
        }
    }

    @Subscribe
    fun handleMessageLogEvent(messageLogEvent: MessageLogEvent) {
        Platform.runLater({
            messageLog.text += "$messageLogEvent\n"
            messageLog.positionCaret(messageLog.text.length)
        })
    }

    @Subscribe
    fun handleDownloadingFiles(downloadingEvent: DownloadingEvent) {
        login.isDisable = true
        download.isDisable = true
        forceDownload.isDisable = true
        changeDirectory.isDisable = true
    }

    @Subscribe
    fun handleDownloadComplete(downloadCompletedEvent: DownloadCompletedEvent) {
        login.isDisable = false
        download.isDisable = false
        forceDownload.isDisable = false
        changeDirectory.isDisable = false
    }
}

class Login : View() {
    override val root: VBox by fxml("/fxml/Login.fxml")

    private val webView: WebView by fxid()
    private val paramName = "token="
    private val loginUrl = "https://ivle.nus.edu.sg/api/login/?apikey=$API_KEY&url=https://localhost/"

    private val controller = BaseController()

    init {
        this.title = "login"

        webView.engine.locationProperty().onChange {
            if (it!!.indexOf(paramName) > 0) {
                val authToken = it.substring(it.indexOf(paramName) + paramName.length)

                controller.setAuthToken(authToken)
                controller.publishMessageLogEvent("Authentication token updated successfully.")

                this.close()
            }
        }

        //webView.engine.setUserStyleSheetLocation(this.javaClass.getResource("/css/login.css").toString());

        webView.engine.load(loginUrl)

    }
}

class ChangeDirectory : View() {
    override val root: VBox by fxml("/fxml/ChangeDirectory.fxml")

    private val directoryPath: TextField by fxid()
    private val directorySubmit: Button by fxid()
    private val directoryPathError: Label by fxid()

    private val controller = BaseController()

    init {
        this.title = "changeDirectory"

        directorySubmit.action {
            val directory: String = directoryPath.text
            val path = File(directory)

            if (path.exists()) {

                controller.setElvisHome(directoryPath.text + "/")
                controller.publishMessageLogEvent("Directory changed successfully.")

                directoryPathError.text = ""

                this.close()
            } else {
                directoryPathError.text = "Invalid Path"
            }
        }
    }
}