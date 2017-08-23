package com.kfwong.elvis.lapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import okhttp3.OkHttpClient
import java.io.FileOutputStream
import java.io.File as File

class Lapi(apiKey: String, authToken: String, downloadDir: String) {
    val ELVIS_HOME = downloadDir
    val API_KEY = apiKey
    val AUTH_TOKEN = authToken

    val MODULES_ENDPOINT = "https://ivle.nus.edu.sg/api/Lapi.svc/Modules"
    val WORKBIN_ENDPOINT = "https://ivle.nus.edu.sg/api/Lapi.svc/Workbins"

    val FILE_DOWNLOAD_ENDPOINT = "https://ivle.nus.edu.sg/api/downloadfile.ashx"

    fun modules(duration: Long = 0, includeAllInfo: Boolean = false): Result<Modules, FuelError> {
        val MODULES_PARAMS = listOf(
                "APIKey" to API_KEY,
                "AuthToken" to AUTH_TOKEN,
                "Duration" to duration,
                "IncludeAllInfo" to includeAllInfo
        )

        return Fuel.get(MODULES_ENDPOINT, MODULES_PARAMS).responseObject(Modules.Deserializer()).third
    }

    fun workbins(courseId: String, duration: Long = 0, workbinId: String = "", titleOnly: Boolean = false): Result<Workbins, FuelError> {
        val WORKBIN_PARAMS = listOf(
                "APIKey" to API_KEY,
                "AuthToken" to AUTH_TOKEN,
                "CourseID" to courseId,
                "Duration" to duration,
                "WorkbinID" to workbinId,
                "TitleOnly" to titleOnly
        )

        return Fuel.get(WORKBIN_ENDPOINT, WORKBIN_PARAMS).responseObject(Workbins.Deserializer()).third
    }

    fun download(fileId: String, fileName: String, fileDir: String = ELVIS_HOME, target: String = "workbin") {
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
                .url("$FILE_DOWNLOAD_ENDPOINT?APIKey=$API_KEY&AuthToken=$AUTH_TOKEN&ID=$fileId&target=$target")
                .build()
        val response = client.newCall(request).execute()

        val fos = FileOutputStream("$fileDir$fileName")
        fos.write(response.body()?.bytes())
        fos.close()
    }
}