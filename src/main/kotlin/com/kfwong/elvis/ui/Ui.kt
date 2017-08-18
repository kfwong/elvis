package com.kfwong.elvis.ui

import com.google.common.eventbus.Subscribe
import com.kfwong.elvis.controller.DirectoryController
import com.kfwong.elvis.controller.ElvisController
import com.kfwong.elvis.controller.LoginController
import com.kfwong.elvis.event.MessageLogEvent
import com.kfwong.elvis.util.API_KEY
import com.kfwong.elvis.util.prefs
import com.kfwong.elvis.util.eventBus
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

    val login: Button by fxid()
    val download: Button by fxid()
    val forceDownload: Button by fxid()
    val changeDirectory: Button by fxid()
    val messageLog: TextArea by fxid()
    val exit: Button by fxid()

    lateinit var controller: ElvisController

    init {
        this.title = "elvis"

        eventBus.register(this)

        login.action {
            find(Login::class).openModal()
        }

        download.action {
            val ELVIS_HOME: String = prefs.get("ELVIS_HOME", System.getProperty("user.home") + "/elvis/")
            val AUTH_TOKEN: String = prefs.get("AUTH_TOKEN", "(not set)")

            if (AUTH_TOKEN != "(not set)") {
                login.disableProperty().set(true)
                forceDownload.disableProperty().set(true)

                controller = ElvisController(API_KEY, AUTH_TOKEN, ELVIS_HOME)
                controller.download()

                login.disableProperty().set(false)
                forceDownload.disableProperty().set(false)
            } else {
                eventBus.post(MessageLogEvent("You must login with your NUSNET account first!"))
            }
        }

        forceDownload.action {
            val ELVIS_HOME: String = prefs.get("ELVIS_HOME", System.getProperty("user.home") + "/elvis/")
            val AUTH_TOKEN: String = prefs.get("AUTH_TOKEN", "(not set)")

            if (AUTH_TOKEN != "(not set)") {
                login.disableProperty().set(true)
                forceDownload.disableProperty().set(true)

                controller = ElvisController(API_KEY, AUTH_TOKEN, ELVIS_HOME)
                controller.download(true)

                login.disableProperty().set(false)
                forceDownload.disableProperty().set(false)
            } else {
                eventBus.post(MessageLogEvent("You must login with your NUSNET account first!"))
            }
        }

        changeDirectory.action {
            find(ChangeDirectory::class).openModal()
        }

        exit.action {
            System.exit(0)
        }

    }

    @Subscribe
    fun handleMessageLogEvent(messageLogEvent: MessageLogEvent) {
        Platform.runLater({
            messageLog.text += "$messageLogEvent\n"
            messageLog.positionCaret(messageLog.text.length)
        })
    }
}

class Login : View() {
    override val root: VBox by fxml("/fxml/Login.fxml")

    val controller = LoginController()

    val webView: WebView by fxid()
    val paramName = "token="
    val loginUrl = "https://ivle.nus.edu.sg/api/login/?apikey=$API_KEY&url=https://localhost/"

    init {
        this.title = "login"

        webView.engine.locationProperty().onChange {
            if (it!!.indexOf(paramName) > 0) {
                val authToken = it.substring(it.indexOf(paramName) + paramName.length)

                controller.saveAuthToken(authToken)

                eventBus.post(MessageLogEvent("Authentication token updated successfully."))

                this.close()
            }
        }

        //webView.engine.setUserStyleSheetLocation(this.javaClass.getResource("/css/login.css").toString());

        webView.engine.load(loginUrl)

    }
}

class ChangeDirectory: View() {
    override val root: VBox by fxml("/fxml/ChangeDirectory.fxml")

    val controller = DirectoryController()

    val directoryPath: TextField by fxid()
    val directorySubmit: Button by fxid()
    val directoryPathError: Label by fxid()

    init {
        this.title = "changeDirectory"

        directorySubmit.action {
            val directory: String = directoryPath.text
            val path = File(directory)

            if (path.exists()) {
                controller.saveElvisHome(directoryPath.text + "/")

                directoryPathError.text = ""
                eventBus.post(MessageLogEvent("Directory changed successfully."))

                this.close()
            } else {
                directoryPathError.text = "Invalid Path"
            }
        }
    }
}