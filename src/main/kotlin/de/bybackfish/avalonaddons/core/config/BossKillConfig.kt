package de.bybackfish.avalonaddons.core.config

import de.bybackfish.avalonaddons.avalon.Lootable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Reader
import java.io.Writer

object BossKillConfig: PersistentSave<MutableMap<Lootable, Int>>("bossKills", mutableMapOf()) {
    override fun read(data: Reader) {
        this.data = Json.decodeFromString(data.readText())
    }

    override fun write(writer: Writer) {
        writer.write(Json.encodeToString(data))
    }

    fun getKills(lootable: Lootable): Int {
        return data[lootable] ?: 0
    }

    fun addKill(lootable: Lootable) {
        val current = data[lootable] ?: 0
        get()[lootable] = current + 1
        dirty()
    }
}