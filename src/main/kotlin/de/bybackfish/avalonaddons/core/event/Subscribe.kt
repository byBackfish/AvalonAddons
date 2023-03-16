package de.bybackfish.avalonaddons.core.event

annotation class Subscribe(
    val ignoreCancelled: Boolean = false,
    val ignoreCondition: Boolean = false,
    val priority: Int = 0
)
