package com.kfwong.elvis.lapi

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Reader

data class Modules(
        @SerializedName("Results")
        val modules: Collection<Module>
) {
    class Deserializer : ResponseDeserializable<Modules> {
        override fun deserialize(reader: Reader): Modules = Gson().fromJson(reader, Modules::class.java)
    }
}

data class Module(
        @SerializedName("ID")
        val id: String,

        @SerializedName("CourseName")
        val name: String,

        @SerializedName("CourseCode")
        val code: String,

        @SerializedName("CourseSemester")
        val semester: String,

        @SerializedName("CourseAcadYear")
        val year: String
) {
    class Deserializer : ResponseDeserializable<Module> {
        override fun deserialize(reader: Reader): Module = Gson().fromJson(reader, Module::class.java)
    }
}