package de.bybackfish.avalonaddons.core.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.adapter.FriendTypeAdapter
import de.bybackfish.avalonaddons.extensions.ensureFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.internal.writeJson
import java.io.File
import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type
import kotlin.concurrent.fixedRateTimer
import kotlin.reflect.KClass

abstract class PersistentSave<T : Any>(protected val name: String, val default: T) {
    val file = File("./config/AvalonAddons/${name}.json")
    private var dirty = false

    var data = default
    val s = Gson()
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

     fun get(): T {
        return data
    }

    fun dirty() {
        PersistentSave.markDirty(this::class)
    }

    private fun load() {
        if(!file.exists()) {
            println("Does not exist")
            file.ensureFile()
            writeFile(true)
            return
        }

        file.bufferedReader().use {
            read(it)
        }
    }
    private fun writeFile(force: Boolean = false) {
        if(!dirty && !force) return

        file.writer().use {
            write(it)
        }
    }

    private fun getType(): Type? {
        return object : TypeToken<T>() {}.type
    }

    private fun init() {
        SAVES.add(this)
        load()
    }

    init {
        init()
    }

    abstract fun read(data: Reader)
    abstract fun write(writer: Writer)


    companion object {
        val SAVES = mutableListOf<PersistentSave<*>>()

        fun loadData() {
            SAVES.forEach { it.load() }
        }
        fun markDirty(clazz: KClass<out PersistentSave<*>>) {
            val save =
                SAVES.find { it::class == clazz } ?: throw IllegalAccessException("PersistentSave not found")
            save.dirty = true
        }

        inline fun <reified T : PersistentSave<*>> markDirty() {
            markDirty(T::class)
        }
        init {
            fixedRateTimer("AvalonAddons-PersistentSave-Write", period = 30000L) {
                for (save in SAVES) {
                    if (save.dirty) save.writeFile()
                }
            }
            Runtime.getRuntime().addShutdownHook(Thread({
                for (save in SAVES) {
                    if (save.dirty) save.writeFile()
                }
            }, "AvalonAddons-PersistentSave-Shutdown"))
        }

    }

}
