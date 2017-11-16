package com.kfwong.elvis.lapi

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.Reader
import java.util.*
import java.util.Arrays
import com.google.gson.JsonParseException
import java.util.Locale
import java.text.SimpleDateFormat
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type
import java.text.ParseException


data class Workbins(
        @SerializedName("Results")
        val workbins: Collection<Workbin>
) {
    class Deserializer : ResponseDeserializable<Workbins> {
        override fun deserialize(reader: Reader): Workbins = GsonBuilder().registerTypeAdapter(Date().javaClass, CustomDateDeserializer()).create().fromJson(reader, Workbins::class.java)
    }
}

data class Workbin(
        @SerializedName("ID")
        val id: String,

        @SerializedName("Title")
        val title: String,

        @SerializedName("Folders")
        val folders: Collection<Folder>
) {
    class Deserializer : ResponseDeserializable<Workbin> {
        override fun deserialize(reader: Reader): Workbin = Gson().fromJson(reader, Workbin::class.java)
    }
}

data class Folder(
        @SerializedName("ID")
        val id: String,

        @SerializedName("FolderName")
        val name: String,

        @SerializedName("Folders")
        val folders: Collection<Folder>,

        @SerializedName("Files")
        val files: Collection<File>,

        @SerializedName("FileCount")
        val fileCount: Int
) {
    class Deserializer : ResponseDeserializable<Folder> {
        override fun deserialize(reader: Reader): Folder = Gson().fromJson(reader, Folder::class.java)
    }
}

data class File(
        @SerializedName("ID")
        val id: String,

        @SerializedName("FileName")
        val name: String,

        @SerializedName("FileDescription")
        val description: String,

        @SerializedName("FileSize")
        val size: Long,

        @SerializedName("FileType")
        val type: String,

        @SerializedName("UploadTime_js")
        val datetimeUploaded: Date,

        @SerializedName("isDownloaded")
        val isDownloaded: Boolean
) {
    class Deserializer : ResponseDeserializable<File> {
        override fun deserialize(reader: Reader): File = Gson().fromJson(reader, File::class.java)
        //override fun deserialize(reader: Reader) = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create().fromJson(reader, File::class.java)
    }
}

class CustomDateDeserializer : JsonDeserializer<Date> {

    val DATE_FORMATS = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss.SS",
            "yyyy-MM-dd'T'HH:mm:ss.S",
            "yyyy-MM-dd'T'HH:mm:ss")

    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, typeOF: Type,
                             context: JsonDeserializationContext): Date {
        for (format in DATE_FORMATS) {
            try {
                return SimpleDateFormat(format, Locale.US).parse(jsonElement.asString)
            } catch (e: ParseException) {
            }

        }
        throw JsonParseException("Unparseable date: \"" + jsonElement.asString
                + "\". Supported formats: " + Arrays.toString(DATE_FORMATS))
    }
}
