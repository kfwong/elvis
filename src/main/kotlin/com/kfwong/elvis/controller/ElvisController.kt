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

        lapi.modules().fold({ m ->
            m.modules.forEach {

                val moduleFolderPath = ELVIS_HOME + it.code.toUpperCase() + " " + it.name.toUpperCase() + "/"
                createFolder(moduleFolderPath)

                lapi.workbins(it.id).fold({ w ->
                    w.workbins.forEach {
                        downloadFiles(it.folders, moduleFolderPath)
                    }
                }, { error ->
                    println(error)
                    //eventBus.post(MessageLogEvent(error.response.httpResponseMessage))
                })
            }
        }, { error ->
            println(error)
            //eventBus.post(MessageLogEvent(error.response.httpResponseMessage))
        })
    }

    private fun downloadFiles(folders: Collection<Folder>, destDir: String) {
        folders.forEach {
            val subFolderPath = destDir + it.name + "/"
            createFolder(subFolderPath)

            it.files.forEach {

                lapi.download(it.id, it.name, subFolderPath).destination { response, url ->
                    createFolder(subFolderPath)

                    File(subFolderPath + it.name)
                }.progress { readBytes, totalBytes ->
                    /*
                    val progress = readBytes / totalBytes
                    println(progress)
                    */
                }.response { request, response, result ->
                    eventBus.post(MessageLogEvent("Downloaded ${it.name} to $subFolderPath."))
                }
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