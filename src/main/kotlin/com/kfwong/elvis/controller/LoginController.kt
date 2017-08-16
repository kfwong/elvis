package com.kfwong.elvis.controller

import com.kfwong.elvis.util.prefs

class LoginController {

    fun saveAuthToken(authToken: String) {
        prefs.put("AUTH_TOKEN", authToken)
    }

}