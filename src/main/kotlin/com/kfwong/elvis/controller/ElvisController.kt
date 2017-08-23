package com.kfwong.elvis.controller

import com.kfwong.elvis.lapi.Folder
import com.kfwong.elvis.lapi.Lapi
import com.kfwong.elvis.util.Constants.prefs
import java.io.File

class ElvisController(apiKey: String, authToken: String, downloadDir: String) : BaseController() {
    private val ELVIS_HOME: String = downloadDir
    private val API_KEY = apiKey
    private val AUTH_TOKEN = authToken

    private val lapi = Lapi(API_KEY, AUTH_TOKEN, ELVIS_HOME)

    fun download(isForceDownload: Boolean = false) {

        object : Thread() {
            override fun run() {
                createFolder(ELVIS_HOME)

                publishMessageLogEvent("Files will be downloaded to $ELVIS_HOME")
                publishDownloadingEvent()

                if (isForceDownload) {
                    publishMessageLogEvent("Running force download for all workbins...")
                } else {
                    publishMessageLogEvent("Downloading files for all workbins...")
                }

                lapi.modules().fold({ m ->

                    m.modules.forEach {
                        val code = it.code.replace('/', '-')
                        val moduleFolderPath = ELVIS_HOME + code.toUpperCase() + " " + it.name.toUpperCase() + "/"
                        createFolder(moduleFolderPath)

                        publishMessageLogEvent("Downloading files for ${it.name}...")

                        lapi.workbins(it.id).fold({ w ->
                            w.workbins.forEach {
                                downloadFiles(it.folders, moduleFolderPath, isForceDownload)
                            }
                            publishMessageLogEvent("All files downloaded for ${it.name}.")
                        }, { error ->
                            println(error)
                            System.exit(1)
                        })
                    }
                }, { error ->
                    println(error)
                    System.exit(1)
                })

                publishMessageLogEvent("Download finished!")
                publishDownloadCompletedEvent()
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
                    publishMessageLogEvent("***** Skipped ${it.name}. It is the latest copy!")
                } else {
                    prefs.put(it.id, it.datetimeUploaded.toString())
                    lapi.download(it.id, it.name, subFolderPath)
                    publishMessageLogEvent("***** Downloaded ${it.name}")
                }

            }

            downloadFiles(it.folders, subFolderPath, isForceDownload)

        }
    }

    private fun createFolder(fullPath: String) {
        val destinationDir = File(fullPath)

        if (!destinationDir.exists()) {
            if (!destinationDir.mkdir()) {
                publishMessageLogEvent("[ERROR] Failed to create directory at $destinationDir!")
            }
        }
    }
}