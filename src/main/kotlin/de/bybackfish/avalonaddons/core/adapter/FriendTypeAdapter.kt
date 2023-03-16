package de.bybackfish.avalonaddons.core.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import de.bybackfish.avalonaddons.core.config.FriendStatus

class FriendTypeAdapter: TypeAdapter<FriendStatus>() {
    override fun write(out: JsonWriter?, value: FriendStatus?) {
        out!!.value(value!!.name)
        println("Writing: ${value.name}")
    }

    override fun read(`in`: JsonReader?): FriendStatus {
        val str = `in`!!.nextString()
        println("Reading: $str")
        return FriendStatus.valueOf(str)
    }


}