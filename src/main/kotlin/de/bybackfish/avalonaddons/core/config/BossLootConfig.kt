package de.bybackfish.avalonaddons.core.config

import de.bybackfish.avalonaddons.avalon.Lootable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Reader
import java.io.Writer

object BossLootConfig: PersistentSave<
        MutableMap<Lootable, MutableMap<String, Int>>>("bossLoot", mutableMapOf()) {

    override fun read(data: Reader) {
        this.data = Json.decodeFromString(data.readText())
    }

    override fun write(writer: Writer) {
        writer.write(Json.encodeToString(data))
    }

    fun getLoot(lootable: Lootable): MutableMap<String, Int> {
        return get()[lootable] ?: mutableMapOf()
    }

    fun addLoot(lootable: Lootable, loot: String, amount: Int = 1) {
        val current = data[lootable] ?: mutableMapOf()
        current[loot] = current[loot]?.plus(amount) ?: amount
        get()[lootable] = current
        dirty()
    }
}