package com.kfwong.elvis.ui

import com.google.common.eventbus.Subscribe
import com.kfwong.elvis.controller.BaseController
import com.kfwong.elvis.controller.ElvisController
import com.kfwong.elvis.event.*
import com.kfwong.elvis.util.Constants.API_KEY
import com.kfwong.elvis.util.Constants.prefs
import com.kfwong.elvis.util.Constants.eventBus
import com.kfwong.elvis.event.BaseEvent.Type.*
import de.jensd.fx.glyphs.icons525.Icons525
import de.jensd.fx.glyphs.icons525.utils.Icon525Factory
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.scene.web.WebView
import javafx.stage.StageStyle
import tornadofx.*

class Ui : App(Gui::class) {
    init {
        importStylesheet("/css/main.css")
    }

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
    private val about: Button by fxid()
    private val exit: Button by fxid()
    private val downloadDirectory: Label by fxid()
    private val messageLog: ListView<BaseEvent> by fxid()
    private val messageLogs: ObservableList<BaseEvent> = FXCollections.observableArrayList<BaseEvent>()
    private val baseController: BaseController = BaseController()

    private lateinit var controller: ElvisController

    init {
        this.title = "elvis"
        this.downloadDirectory.text = prefs.get("ELVIS_HOME", "(not set)")
        this.messageLog.items = messageLogs
        this.primaryStage.initStyle(StageStyle.UNDECORATED)

        eventBus.register(this)

        messageLog.cellFormat {
            graphic = MessageLogEntry(it).root
        }

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
            changeDirectoryAction()
        }

        about.action {
            find(About::class).openModal()
        }

        exit.action {
            System.exit(0)
        }

    }

    private fun downloadAction(isForceDownload: Boolean = false) {
        val ELVIS_HOME: String = prefs.get("ELVIS_HOME", System.getProperty("user.home") + "/elvis/")
        val AUTH_TOKEN: String = prefs.get("AUTH_TOKEN", "(not set)")

        if (AUTH_TOKEN != "(not set)") {
            controller = ElvisController(API_KEY, AUTH_TOKEN, ELVIS_HOME)
            if (isForceDownload) confirm("Force Download", "This will OVERWRITE all existing files. Are you sure?", ButtonType.OK, ButtonType.CANCEL) {
                controller.download(isForceDownload)
            } else {
                controller.download()
            }
        } else {
            controller.publishMessageLogEvent("You must login with your NUSNET account first!", FAILURE)
        }
    }

    private fun changeDirectoryAction() {
        val file = chooseDirectory()

        if (file != null) {
            baseController.setElvisHome(file.path)
            baseController.publishDirectoryChangedEvent(file.path)
            baseController.publishMessageLogEvent("Directory changed successfully.", SUCCESS)
        }
    }

    @Subscribe
    fun handleMessageLogEvent(messageLogEvent: MessageLogEvent) {
        Platform.runLater({
            messageLogs.add(messageLogEvent)
            messageLog.scrollTo(messageLog.items.size - 1)
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

    @Subscribe
    fun handleDirectoryChanged(directoryChangedEvent: DirectoryChangedEvent) {
        downloadDirectory.text = directoryChangedEvent.newPath
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
                controller.publishMessageLogEvent("Authentication token updated successfully.", SUCCESS)

                this.close()
            }
        }

        //webView.engine.setUserStyleSheetLocation(this.javaClass.getResource("/css/login.css").toString());

        webView.engine.load(loginUrl)

    }
}

class About : View() {
    override val root: AnchorPane by fxml("/fxml/About.fxml")

    private val content: TextArea by fxid()

    init {

        this.content.text = "Copyright @ 2017, Wong Kang Fei under GPL-3.0 license.\n" +
                "\n" +
                "Source Code: https://github.com/kfwong/elvis\n" +
                "\n" +
                "Github Contributors' handles:\n" +
                "@kfwong (Main author)\n" +
                "@zavfel (Contributor)\n" +
                "\n" +
                "Found a bug? Send an email to me@kfwong.com or raise on issue on our Github Page!"
    }
}

// A reusable HBox with a Label inside (useless, but it gets the point across)
class MessageLogEntry(event: BaseEvent) : Fragment() {
    override val root: AnchorPane by fxml("/fxml/MessageLogEntry.fxml")

    private val datetime: Label by fxid()
    private val message: Label by fxid()
    private val messageIcon: Label by fxid()

    init {
        root.tooltip(event.eventMessage)
        datetime.text = event.formattedDatetime
        message.text = event.eventMessage

        val icon: Text

        when (event.type) {
            INFO -> {
                icon = Icon525Factory.get().createIcon(Icons525.INFO_CIRCLE, "2em")
                icon.styleClass.add("ICON_INFO")
                messageIcon.graphic = icon
            }
            WARNING -> {
                icon = Icon525Factory.get().createIcon(Icons525.WARNING_SIGN, "2em")
                icon.styleClass.add("ICON_WARNING")
                messageIcon.graphic = icon
            }
            CRITICAL -> {
                icon = Icon525Factory.get().createIcon(Icons525.EXCLAMATION_CIRCLE, "2em")
                icon.styleClass.add("ICON_CRITICAL")
                messageIcon.graphic = icon
            }
            IMPORTANT -> {
                icon = Icon525Factory.get().createIcon(Icons525.ASTERISK2, "2em")
                icon.styleClass.add("ICON_IMPORTANT")
                messageIcon.graphic = icon
            }
            ERROR -> {
                icon = Icon525Factory.get().createIcon(Icons525.FROWNING_FACE, "2em")
                icon.styleClass.add("ICON_ERROR")
                messageIcon.graphic = icon
            }
            SUCCESS -> {
                icon = Icon525Factory.get().createIcon(Icons525.CIRCLESELECT, "2em")
                icon.styleClass.add("ICON_SUCCESS")
                messageIcon.graphic = icon
            }
            FAILURE -> {
                icon = Icon525Factory.get().createIcon(Icons525.CIRCLEDELETE, "2em")
                icon.styleClass.add("ICON_FAILURE")
                messageIcon.graphic = icon
            }
            DOWNLOAD -> {
                icon = Icon525Factory.get().createIcon(Icons525.DOWNLOAD, "2em")
                icon.styleClass.add("ICON_DOWNLOAD")
                messageIcon.graphic = icon
            }
            SKIP -> {
                icon = Icon525Factory.get().createIcon(Icons525.ELLIPSIS, "2em")
                icon.styleClass.add("ICON_SKIP")
                messageIcon.graphic = icon
            }
            UPDATE -> {
                icon = Icon525Factory.get().createIcon(Icons525.REFRESH, "2em")
                icon.styleClass.add("ICON_UPDATE")
                messageIcon.graphic = icon
            }
        }
    }
}