package de.bybackfish.avalonaddons.core.config

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Reader
import java.io.Writer

object FriendsConfig: PersistentSave<MutableMap<FriendStatus, MutableList<String>>>("friends", mutableMapOf()) {
    override fun read(data: Reader) {
        this.data = Json.decodeFromString(data.readText())
    }

    override fun write(writer: Writer) {
        writer.write(Json.encodeToString(data))
    }
    fun ignore(username: String) :Boolean {
        if (get()[FriendStatus.FRIEND]?.contains(username) == true || get()[FriendStatus.IGNORED]?.contains(username) == true) return false

        val current = get()[FriendStatus.IGNORED] ?: mutableListOf()
        current.add(username)
        get()[FriendStatus.IGNORED] = current
        dirty()
        return true
    }

    fun unignore(username: String): Boolean {
        if (get()[FriendStatus.IGNORED]?.contains(username) == false) return false

        val current = get()[FriendStatus.IGNORED] ?: mutableListOf()
        current.remove(username)
        get()[FriendStatus.IGNORED] = current
        dirty()
        return true
    }

    fun isIgnored(username: String): Boolean {
        return get()[FriendStatus.IGNORED]?.contains(username) ?: false
    }

    fun friend(username: String): Boolean {
        if (get()[FriendStatus.IGNORED]?.contains(username) == true || get()[FriendStatus.FRIEND]?.contains(username) == true) return false

        val current = get()[FriendStatus.FRIEND] ?: mutableListOf()
        current.add(username)
        get()[FriendStatus.FRIEND] = current
        dirty()
        return true
    }

    fun unfriend(username: String): Boolean {
        if (get()[FriendStatus.FRIEND]?.contains(username) == false) return false

        val current = get()[FriendStatus.FRIEND] ?: mutableListOf()
        current.remove(username)
        get()[FriendStatus.FRIEND] = current
        dirty()
        return true
    }

    fun isFriend(username: String): Boolean {
        return get()[FriendStatus.FRIEND]?.contains(username) ?: false
    }

}

enum class FriendStatus {
    FRIEND,
    IGNORED
}