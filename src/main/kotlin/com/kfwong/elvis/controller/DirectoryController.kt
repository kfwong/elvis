package com.kfwong.elvis.controller

import com.kfwong.elvis.util.prefs

class DirectoryController {

    fun saveElvisHome(directory: String) {
        prefs.put("ELVIS_HOME", directory)
    }
}