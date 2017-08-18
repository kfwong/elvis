package com.kfwong.elvis.controller

import com.kfwong.elvis.event.MessageLogEvent
import com.kfwong.elvis.lapi.Folder
import com.kfwong.elvis.lapi.Lapi
import com.kfwong.elvis.util.eventBus
import com.kfwong.elvis.util.prefs
import java.io.File

class ElvisController(apiKey: String, authToken: String, downloadDir: String) {
    val ELVIS_HOME: String = downloadDir
    val API_KEY = apiKey
    val AUTH_TOKEN = authToken

    val lapi = Lapi(API_KEY, AUTH_TOKEN, ELVIS_HOME)

    fun download(isForceDownload:Boolean = false) {
        if (isForceDownload) {
            eventBus.post(MessageLogEvent("Running force download for all workbins..."))
        } else {
            eventBus.post(MessageLogEvent("Downloading files for all workbins..."))
        }

        object : Thread() {
            override fun run() {
                createFolder(ELVIS_HOME)

                lapi.modules().fold({ m ->

                    m.modules.forEach {
                        val code = it.code.replace('/','-')
                        val moduleFolderPath = ELVIS_HOME + code.toUpperCase() + " " + it.name.toUpperCase() + "/"
                        createFolder(moduleFolderPath)

                        eventBus.post(MessageLogEvent("Downloading files for ${it.name}..."))

                        lapi.workbins(it.id).fold({ w ->
                            w.workbins.forEach {
                                downloadFiles(it.folders, moduleFolderPath, isForceDownload)
                            }
                            eventBus.post(MessageLogEvent("All files downloaded for ${it.name}."))
                        }, { error ->
                            println(error)
                            System.exit(1)
                        })
                    }
                }, { error ->
                    println(error)
                    System.exit(1)
                })
            }
        }.start()
    }

    private fun downloadFiles(folders: Collection<Folder>, destDir: String, isForceDownload: Boolean = false) {
        folders.forEach {
            val subFolderPath = destDir + it.name + "/"
            createFolder(subFolderPath)

            it.files.forEach {
                val datetimeUploaded = prefs.get(it.id, null)

                if (it.datetimeUploaded.toString() == datetimeUploaded && !isForceDownload) {
                    eventBus.post(MessageLogEvent("***** Skipped ${it.name}. It is the latest copy!"))
                } else {
                    prefs.put(it.id, it.datetimeUploaded.toString())
                    lapi.download(it.id, it.name, subFolderPath)
                    eventBus.post(MessageLogEvent("***** Downloaded ${it.name}"))
                }

            }

            downloadFiles(it.folders, subFolderPath)

        }
    }

    private fun createFolder(fullPath: String) {
        val destinationDir = File(fullPath)

        if (!destinationDir.exists()) {
            if (!destinationDir.mkdir()) {
                eventBus.post(MessageLogEvent("[ERROR] Failed to create directory at $destinationDir!"))
            }
        }
    }
}