package de.bybackfish.avalonaddons.core

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty


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
    return key
}