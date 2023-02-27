package de.bybackfish.avalonaddons.core.feature.config

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Property(
    val name: String,
    val internalName: String,
    val description: String,
)