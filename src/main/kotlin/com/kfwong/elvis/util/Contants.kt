package com.kfwong.elvis.util

import com.google.common.eventbus.EventBus
import java.util.prefs.Preferences

val prefs: Preferences = Preferences.userRoot().node("com.kfwong.elvis")
val eventBus: EventBus = EventBus()

val API_KEY = "PK3n2PGjXR4OooZPZyelQ"