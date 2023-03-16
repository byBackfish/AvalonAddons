package de.bybackfish.avalonaddons.core.config

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Reader
import java.io.Writer

object LockedSlotsConfig: PersistentSave<MutableList<Double>>("lockedSlots", mutableListOf()) {

    override fun read(data: Reader) {
        this.data = Json.decodeFromString(data.readText())
    }

    override fun write(writer: Writer) {
        writer.write(Json.encodeToString(data))
    }

    fun isLocked(slot: Int): Boolean {
        return get().contains(slot.toDouble())
    }

    fun toggle(slot: Int): Boolean {
        val list = get()
        return if(list.contains(slot.toDouble())) {
            list.remove(slot.toDouble())
            markDirty<LockedSlotsConfig>()
            false
        } else {
            list.add(slot.toDouble())
            markDirty<LockedSlotsConfig>()
            true
        }
    }

}