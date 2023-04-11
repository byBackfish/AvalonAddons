package de.bybackfish.avalonaddons.core.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.extensions.ensureFile
import kotlinx.serialization.json.Json
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
        markDirty(this::class)
    }

    fun load() {
        if(!file.exists()) {
            println("Does not exist")
            file.ensureFile()
            kotlin.runCatching {
                onCreate()
                writeFile(true)
            }.onFailure {
                it.printStackTrace()
            }
            return
        }

        file.bufferedReader().use {
            read(AvalonAddons.json, it)
        }
    }
    private fun writeFile(force: Boolean = false) {
        if(!dirty && !force) return

        file.writer().use {
            write(AvalonAddons.json, it)
        }
    }

    private fun getType(): Type? {
        return object : TypeToken<T>() {}.type
    }

    private fun init() {
        SAVES.add(this)

        if(NO_PARSE.contains(this::class)) return
        load()
    }

    init {
        init()
    }

    abstract fun read(json: Json, data: Reader)
    abstract fun write(json: Json, writer: Writer)
    open fun onCreate() {}


    companion object {
        val SAVES = mutableListOf<PersistentSave<*>>()
        val NO_PARSE = arrayListOf<KClass<*>>()


        fun addNoParse(clazz: KClass<*>) {
            NO_PARSE.add(clazz)
        }

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

        private fun saveAll() {
            SAVES.filter(PersistentSave<*>::dirty).forEach(PersistentSave<*>::writeFile)
        }

        init {
            fixedRateTimer("AvalonAddons-PersistentSave-Write", period = 30000L) {
                saveAll()
            }
            Runtime.getRuntime().addShutdownHook(Thread({
                saveAll()
            }, "AvalonAddons-PersistentSave-Shutdown"))
        }

    }

}
