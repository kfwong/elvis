package com.kfwong.elvis.controller

import com.kfwong.elvis.event.MessageLogEvent
import com.kfwong.elvis.lapi.Folder
import com.kfwong.elvis.lapi.Lapi
import com.kfwong.elvis.util.eventBus
import java.io.File

class ElvisController(apiKey: String, authToken: String, downloadDir: String) {
    val ELVIS_HOME: String = downloadDir
    val API_KEY = apiKey
    val AUTH_TOKEN = authToken

    val lapi = Lapi(API_KEY, AUTH_TOKEN, ELVIS_HOME)

    fun forceDownloadWorkbins() {
        eventBus.post(MessageLogEvent("Running force download for all workbins."))

        object : Thread() {
            override fun run() {
                createFolder(ELVIS_HOME)

                lapi.modules().fold({ m ->

                    m.modules.forEach {

                        val moduleFolderPath = ELVIS_HOME + it.code.toUpperCase() + " " + it.name.toUpperCase() + "/"
                        createFolder(moduleFolderPath)

                        eventBus.post(MessageLogEvent("Downloading files for ${it.name}..."))

                        lapi.workbins(it.id).fold({ w ->
                            w.workbins.forEach {
                                downloadFiles(it.folders, moduleFolderPath)
                            }
                        }, { error ->
                            println(error)
                        })
                    }
                }, { error ->
                    println(error)
                })
            }
        }.start()
    }

    private fun downloadFiles(folders: Collection<Folder>, destDir: String) {
        folders.forEach {
            val subFolderPath = destDir + it.name + "/"
            createFolder(subFolderPath)

            it.files.forEach {
                eventBus.post(MessageLogEvent("Downloading ${it.name}"))
                lapi.download(it.id, it.name, subFolderPath)
            }

            downloadFiles(it.folders, subFolderPath)

        }
    }

    private fun createFolder(fullPath: String) {
        val destinationDir = File(fullPath)

        if (!destinationDir.exists()) {
            if (!destinationDir.mkdir()) {
                eventBus.post(MessageLogEvent("Failed to create directory at ${destinationDir}!"))
            }
        }
    }
}