package de.bybackfish.avalonaddons.core.feature

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.AvalonConfig
import de.bybackfish.avalonaddons.core.annotations.*
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.feature.struct.FeatureState
import de.bybackfish.avalonaddons.core.getKey
import de.bybackfish.avalonaddons.core.getTranslatedName
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberFunctions


class FeatureManager {

    val features = mutableMapOf<KClass<out Feature>, Feature>()

    init {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            // check all keybinds
            features.forEach { (clazz, feature) ->
                feature.featureInfo.keybindings.forEach { keybindSetting ->
                    if (keybindSetting.keybinding.isPressed) {
                        keybindSetting.function.call(feature)
                    }
                }
            }
        })


    }

    fun register(vararg features: Feature) {
        features.forEach { register(it) }
    }

    fun register(feature: Feature) {
        val propertySettings = getProperties(feature::class).map {
            PropertySetting(
                feature,
                it,
                it.annotations.first { annotation -> annotation is Property } as Property)
        }

        val buttons = getButtons(feature::class).map {
            val annotation =
                it.annotations.first { annotation -> annotation is Button } as Button
            ButtonSetting(feature, it, annotation)
        }


        val shouldRegisterKeybindings =
            feature::class.annotations.any { annotation -> annotation is RegisterKeybinds }
        val keybindSettings = if (shouldRegisterKeybindings) {
            getKeybindings(feature::class).map {
                val annotation =
                    it.annotations.first { annotation -> annotation is Keybind } as Keybind
                val mcKeybind = KeyBinding(
                    getTranslatedName(getKey(feature::class, it)),
                    annotation.defaultKey,
                    "AvalonAddons"
                )
                KeyBindingHelper.registerKeyBinding(mcKeybind)

                KeybindSetting(feature, it, annotation, mcKeybind)
            }
        } else listOf()

        val overlaySettings = getOverlays(feature::class).map {
            val overlayProperties = getProperties(it).map {
                PropertySetting(
                    feature,
                    it,
                    it.annotations.first { annotation -> annotation is Property } as Property)
            }
            OverlaySetting(
                feature,
                it,
                it.annotations.first { annotation -> annotation is OverlayInfo } as OverlayInfo,
                overlayProperties)
        }

        val featureCategory =
            feature::class.annotations.firstOrNull { annotation -> annotation is Category }
                ?.let { (it as Category).name } ?: "default"
        val featureInfo = FeatureInfo(
            feature,
            featureCategory,
            propertySettings,
            keybindSettings,
            overlaySettings,
            buttons
        )

        feature.featureInfo = featureInfo

        feature.init()
        features[feature::class] = feature
    }

    fun loadToConfig() {
        println("[Avalon] Loading Settings to Config")
        val config = AvalonAddons.config
        features.forEach { (clazz, feature) ->
            println("[Avalon] Loading Feature ${feature::class.simpleName} with properties: ${feature.featureInfo.properties.size}")
            val featureData = nativeRegisterProperty<Nothing>(
                value = FeatureTogglePropertyValue(feature),
                type = PropertyType.SWITCH,
                name = "Toggle",
                description = "Enable ${getTranslatedName(getKey(feature::class))}",
                category = feature.featureInfo.category,
                subcategory = getTranslatedName(getKey(feature::class)),
                config = config,
            )

            feature.featureInfo.properties.forEach { propertySetting ->
                val subData = nativeRegisterFeaturePropertySetting(
                    config,
                    feature,
                    propertySetting,
                    featureData
                )
            }

            feature.featureInfo.buttons.forEach { buttonSetting ->
                nativeRegisterFeatureButton(
                    config,
                    feature,
                    buttonSetting,
                    featureData
                )
            }
        }

    }

    private fun test(
        feature: Feature,
        config: AvalonConfig,
        builder: Vigilant.CategoryPropertyBuilder
    ) {
        val data = PropertyData(
            PropertyAttributesExt(
                type = PropertyType.SWITCH,
                name = "Test Switch",
                category = "Test",
            ),
            FeatureTogglePropertyValue(feature),
            config
        )

        config.registerProperty(data)
    }

    private fun <T> nativeRegisterProperty(
        config: AvalonConfig,
        value: PropertyValue,
        type: PropertyType,
        name: String,
        description: String = "",
        subcategory: String = "",
        category: String = "",
        searchTags: List<String> = listOf(),
        min: Int = 0,
        max: Int = 0,
        sortingOrder: Int = 0,
        decimalPlaces: Int = 1,
        increment: Int = 1,
        options: List<String> = listOf(),
        allowAlpha: Boolean = true,
        placeholder: String = "",
        protectedText: Boolean = false,
        triggerActionOnInitialization: Boolean = true,
        hidden: Boolean = false,
        action: ((T) -> Unit)? = null,
        customPropertyInfo: KClass<out PropertyInfo> = Nothing::class,
    ): PropertyData {

        val data = PropertyData(
            PropertyAttributesExt(
                type = type,
                name = name,
                category = category,
                subcategory = subcategory,
                description = description,
                min = min,
                max = max,
                decimalPlaces = decimalPlaces,
                maxF = sortingOrder.toFloat(),
                increment = increment,
                options = options,
                allowAlpha = allowAlpha,
                placeholder = placeholder,
                protected = protectedText,
                triggerActionOnInitialization = triggerActionOnInitialization,
                hidden = hidden,
                searchTags = searchTags,
                customPropertyInfo = customPropertyInfo.java,
            ),
            value,
            config
        )

        println("[Avalon] Registering Property $name")


        AvalonAddons.propertyCollector.addProperty(data)

        return data;
    }

    private fun nativeRegisterFeaturePropertySetting(
        config: AvalonConfig,
        feature: Feature,
        propertySetting: PropertySetting,
        featureToggle: PropertyData? = null
    ) {
        val data = nativeRegisterProperty<Any>(
            value = CustomValueProperty(
                feature,
                propertySetting.field as KMutableProperty1<Feature, Any>
            ),
            type = propertySetting.getPropertyType(),
            name = getTranslatedName(getKey(feature::class, propertySetting.field)),
            description = propertySetting.property.description,
            searchTags = propertySetting.property.searchTags.toList(),

            category = propertySetting.feature.featureInfo.category,
            subcategory = getTranslatedName(getKey(feature::class)),

            min = propertySetting.property.min,
            max = propertySetting.property.max,
            sortingOrder = propertySetting.property.sortingOrder,
            decimalPlaces = propertySetting.property.decimalPlaces,
            increment = propertySetting.property.increment,
            allowAlpha = propertySetting.property.allowAlpha,
            options = propertySetting.property.options.toList(),
            placeholder = propertySetting.property.placeholder,
            triggerActionOnInitialization = propertySetting.property.triggerActionOnInitialization,
            hidden = propertySetting.property.hidden,
            config = config,
        )

        if (featureToggle != null) {
            data.dependsOn = featureToggle
            featureToggle.hasDependants = true
        }
    }

    private fun nativeRegisterFeatureButton(
        config: AvalonConfig,
        feature: Feature,
        buttonSetting: ButtonSetting,
        featureToggle: PropertyData? = null
    ) {
        val data = nativeRegisterProperty<Any>(
            value = CustomButtonProperty(
                feature,
                buttonSetting.function
            ),
            type = PropertyType.BUTTON,
            name = getTranslatedName(getKey(buttonSetting.feature::class, buttonSetting.function)),
            description = buttonSetting.annotation.description,
            searchTags = buttonSetting.annotation.searchTags.toList(),

            category = buttonSetting.feature.featureInfo.category,
            subcategory = getTranslatedName(getKey(feature::class)),

            placeholder = buttonSetting.annotation.buttonText,
            hidden = buttonSetting.annotation.hidden,
            action = null,
            config = config,
        )

        if (featureToggle != null) {
            data.dependsOn = featureToggle
            featureToggle.hasDependants = true
        }
    }


    fun getFeature(clazz: KClass<out Feature>): Feature? = features[clazz]

    private fun getProperties(clazz: KClass<*>): List<KMutableProperty1<*, *>> {
        println("[Avalon] Raw Members: ${clazz.declaredMemberProperties.size}")

        clazz.declaredMemberProperties.forEach {
            println("[Avalon] Member: ${it.name}: ${it.annotations}")
        }

        return clazz.declaredMemberProperties.filter { it.annotations.any { annotation -> annotation is Property } }
            .also {
                println("[Avalon] Found Property: ${it.size}")
            }
            .map { it as KMutableProperty1<*, *> }
    }

    private fun getButtons(clazz: KClass<*>): List<KFunction<*>> {
        return clazz.memberFunctions.filter { it.annotations.any { annotation -> annotation is Button } }
    }

    private fun getOverlays(clazz: KClass<*>): List<KClass<*>> {
        return clazz.nestedClasses.filter { it.annotations.any { annotation -> annotation is OverlayInfo } }
    }

    private fun getKeybindings(clazz: KClass<*>): List<KFunction<*>> {
        return clazz.memberFunctions.filter { it.annotations.any { annotation -> annotation is Keybind } }
    }

    data class FeatureInfo(
        val feature: Feature,
        val category: String,
        val properties: List<PropertySetting>,
        val keybindings: List<KeybindSetting>,
        val overlays: List<OverlaySetting>,
        val buttons: List<ButtonSetting>,
    )

    data class PropertySetting(
        val feature: Feature,
        val field: KMutableProperty1<*, *>,
        val property: Property,
    ) {
        fun getPropertyType(): PropertyType {
            if (this.property.forceType != PropertyType.CUSTOM)
                return this.property.forceType

            // check if field is int or boolean
            return when (field.returnType) {
                Int::class.createType() -> {
                    PropertyType.NUMBER
                }

                Boolean::class.createType() -> {
                    PropertyType.SWITCH
                }

                String::class.createType() -> {
                    PropertyType.TEXT
                }

                else -> {
                    PropertyType.TEXT
                }
            }

        }
    }

    data class KeybindSetting(
        val feature: Feature,
        val function: KFunction<*>,
        val annotation: Keybind,
        val keybinding: KeyBinding,
    )

    data class OverlaySetting(
        val feature: Feature,
        val clazz: KClass<*>,
        val overlay: OverlayInfo,
        val properties: List<PropertySetting>,
    )

    data class ButtonSetting(
        val feature: Feature,
        val function: KFunction<*>,
        val annotation: Button,
    )

    class CustomValueProperty<F, T>(val owner: F, internal val property: KMutableProperty1<F, T>) :
        PropertyValue() {
        override fun getValue(instance: Vigilant): Any? {
            return property.get(owner)
        }

        override fun setValue(value: Any?, instance: Vigilant) {
            try {
                property.set(owner, value as T)
            } catch (e: Exception) {
                println("[Avalon] Error setting property: ${property.name} to value: $value")
                e.printStackTrace()
            }
        }
    }

    class CustomButtonProperty(private val owner: Feature, internal val function: KFunction<*>) :
        CallablePropertyValue() {
        override fun invoke(instance: Vigilant) {
            function.call(owner)
        }
    }

    class FeatureTogglePropertyValue(private val feature: Feature) : PropertyValue() {
        override fun getValue(instance: Vigilant): Any? {
            return feature.state == FeatureState.ENABLED
        }

        override fun setValue(value: Any?, instance: Vigilant) {
            println("[Avalon] FeatureTogglePropertyValue: ${feature.state} -> Value: $value")
            if (value as Boolean) {
                if (feature.state == FeatureState.ENABLED) return
                feature.toggle()
            } else {
                if (feature.state == FeatureState.DISABLED) return
                feature.toggle()
            }
        }
    }

}

