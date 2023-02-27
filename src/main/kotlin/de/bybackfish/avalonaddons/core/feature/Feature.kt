package de.bybackfish.avalonaddons.core.feature

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.annotations.EnabledByDefault
import de.bybackfish.avalonaddons.core.feature.struct.FeatureState
import net.minecraft.client.MinecraftClient

abstract class Feature {
    var state = FeatureState.UNINITIALIZED

    lateinit var featureInfo: FeatureManager.FeatureInfo

    var mc: MinecraftClient = MinecraftClient.getInstance()
        get() = MinecraftClient.getInstance()

    var player = mc.player
        get() = mc.player

    fun init() {
        if (state != FeatureState.UNINITIALIZED) return

        state = if (this::class.annotations.any { annotation -> annotation is EnabledByDefault })
            FeatureState.ENABLED
        else
            FeatureState.DISABLED

        // register to ExampleMod.bus with the condition of state being enabled
        val condition = { state == FeatureState.ENABLED }
        AvalonAddons.bus.register(this, condition)
    }

    fun toggle() {
        state = if (state == FeatureState.ENABLED) FeatureState.DISABLED else FeatureState.ENABLED
    }


}