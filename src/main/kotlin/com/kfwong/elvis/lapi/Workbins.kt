package com.kfwong.elvis.lapi

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Reader
import java.util.*

data class Workbins(
        @SerializedName("Results")
        val workbins: Collection<Workbin>
) {
    class Deserializer : ResponseDeserializable<Workbins> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Workbins::class.java)
    }
}

data class Workbin(
        @SerializedName("ID")
        val id: String,

        @SerializedName("com.kfwong.elvis.Title")
        val title: String,

        @SerializedName("Folders")
        val folders: Collection<Folder>
) {
    class Deserializer : ResponseDeserializable<Workbin> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Workbin::class.java)
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
        val fileCount: Integer
) {
    class Deserializer : ResponseDeserializable<Folder> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Folder::class.java)
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
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, File::class.java)
    }
}