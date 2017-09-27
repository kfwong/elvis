package com.kfwong.elvis.controller

import com.kfwong.elvis.event.BaseEvent
import com.kfwong.elvis.event.MessageLogEvent
import com.kfwong.elvis.event.BaseEvent.Type.*
import com.kfwong.elvis.lapi.Folder
import com.kfwong.elvis.lapi.Lapi
import com.kfwong.elvis.util.Constants.prefs
import java.io.File


class ElvisController(apiKey: String, authToken: String, downloadDir: String) : BaseController() {
    private val ELVIS_HOME: String = downloadDir
    private val API_KEY = apiKey
    private val AUTH_TOKEN = authToken

    private val lapi = Lapi(API_KEY, AUTH_TOKEN, ELVIS_HOME)

    /**
     * Spawn a new thread and perform download action for all modules.
     *
     * @param isForceDownload true to unconditionally download all files. Default is false.
     */
    fun download(isForceDownload: Boolean = false) {

        object : Thread() {
            override fun run() {
                // create download directory
                createFolder(ELVIS_HOME)

                publishMessageLogEvent("Files will be downloaded to $ELVIS_HOME")
                publishDownloadingEvent()

                if (isForceDownload) {
                    publishMessageLogEvent("Running force download for all workbins...", IMPORTANT)
                } else {
                    publishMessageLogEvent("Downloading files for all workbins...", DOWNLOAD)
                }

                lapi.modules().fold({ m ->

                    m.modules.forEach {
                        // replace slashes with dashes so it will not interfere with pathname
                        val code = it.code.replace('/', '-')
                        val moduleFolderPath = ELVIS_HOME + code.toUpperCase() + " " + it.name.toUpperCase() + "/"
                        createFolder(moduleFolderPath)

                        publishMessageLogEvent("Downloading files for ${it.name}...", DOWNLOAD)

                        lapi.workbins(it.id).fold({ w ->
                            w.workbins.forEach {
                                downloadFiles(it.folders, moduleFolderPath, isForceDownload)
                            }
                            publishMessageLogEvent("All files downloaded for ${it.name}.", SUCCESS)
                        }, { error ->
                            println(error)
                            System.exit(1)
                        })
                    }
                }, { error ->
                    println(error)
                    System.exit(1)
                })

                publishMessageLogEvent("Download finished!", SUCCESS)
                publishDownloadCompletedEvent()
            }
        }.start()
    }

    /**
     * Recursively download files in folders. By default it skips downloading files that are up-to-date.
     *
     * @param folders a collection of folders
     * @param destDir destination directory saving to
     * @param isForceDownload true to unconditionally download all files. Default is false.
     */
    private fun downloadFiles(folders: Collection<Folder>, destDir: String, isForceDownload: Boolean = false) {
        folders.forEach {
            val subFolderPath = destDir + it.name + "/"
            createFolder(subFolderPath)

            it.files.forEach {
                val datetimeUploaded = prefs.get(it.id, "(no entry)")
                val isFileExist = File(subFolderPath + it.name).exists()
                val isFileRegistered = datetimeUploaded != "(no entry)"
                val isFileUpdated = it.datetimeUploaded.toString() == datetimeUploaded

                addFileRegistry(it.id, it.datetimeUploaded)

                when {
                    isForceDownload -> {
                        lapi.download(it.id, it.name, subFolderPath)
                        publishMessageLogEvent("***** Force downloading ${it.name}", DOWNLOAD)
                    }
                    isFileRegistered && isFileExist && isFileUpdated -> {
                        publishMessageLogEvent("***** Skipping ${it.name}. It is the latest copy!", SKIP)
                    }
                    isFileRegistered && !isFileUpdated -> {
                        lapi.download(it.id, it.name, subFolderPath)
                        publishMessageLogEvent("***** ${it.name} is out-dated. Updating.", UPDATE)
                    }
                    !isFileExist -> {
                        lapi.download(it.id, it.name, subFolderPath)
                        publishMessageLogEvent("***** Downloading ${it.name}", DOWNLOAD)
                    }
                }

            }

            downloadFiles(it.folders, subFolderPath, isForceDownload)

        }
    }

    /**
     * Create folder if the pathname is valid
     *
     * @param fullPath absolute pathname
     */
    private fun createFolder(fullPath: String) {
        val destinationDir = File(fullPath)

        if (!destinationDir.exists()) {
            if (!destinationDir.mkdir()) {
                publishMessageLogEvent("[ERROR] Failed to create directory at $destinationDir!", ERROR)
            }
        }
    }
}