package de.bybackfish.avalonaddons.core.event

annotation class Subscribe(
    val ignoreCancelled: Boolean = true,
    val ignoreCondition: Boolean = false,
    val priority: Int = 0
)
