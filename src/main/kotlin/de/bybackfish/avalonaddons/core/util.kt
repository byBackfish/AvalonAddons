package de.bybackfish.avalonaddons.core

import com.google.gson.Gson
import de.bybackfish.avalonaddons.AvalonAddons
import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty


val translations = mutableMapOf<String, String>()

fun loadTranslations() {
    // load resources/assets/avalonaddons/lang/default.json
    val filePath =
        AvalonAddons.Companion::class.java.getResourceAsStream("/assets/avalonaddons/lang/default.json")!!
    val file = File.createTempFile("default", ".json")
    file.deleteOnExit()
    file.writeBytes(filePath.readBytes())

    val gson = Gson()
    val map = gson.fromJson(file.readText(), Map::class.java)
    map.forEach { (key, value) ->
        translations[key as String] = value as String
    }
}

fun trimKey(key: String): String {
    return key.replace("de.bybackfish.avalonaddons.", "")
}

fun getKey(clazz: KClass<*>, property: KProperty<*>): String {
    return trimKey("${clazz.java.`package`.name}.${clazz.simpleName}.${property.name}")
}

fun getKey(clazz: KClass<*>, callable: KCallable<*>): String {
    return trimKey("${clazz.java.`package`.name}.${clazz.simpleName}.${callable.name}")
}

fun getKey(clazz: KClass<*>): String {
    return trimKey("${clazz.java.`package`.name}.${clazz.simpleName}")
}

fun getKey(clazz: KClass<*>, func: KFunction<*>): String {
    return trimKey("${clazz.java.`package`.name}.${clazz.simpleName}.${func.name}")
}

fun getTranslatedName(key: String): String {
    return translations[key] ?: key
}