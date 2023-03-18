package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.event.EventBus
import de.bybackfish.avalonaddons.events.ChestOpenEvent
import de.bybackfish.avalonaddons.events.ClientTickEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen

class NativeListeners {

    var loaded = false
    fun load(bus: EventBus) {

        HudRenderCallback.EVENT.register((HudRenderCallback { matrixStack, tickDelta ->
            // check if the player is in the debug (f3) menu
            if (MinecraftClient.getInstance().options.debugEnabled) return@HudRenderCallback
            bus.post(RenderScreenEvent(matrixStack, tickDelta))
        }))

        ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick { client ->
            bus.post(ClientTickEvent())
            if (!loaded) {
                loaded = true
                AvalonAddons.featureManager.features.forEach { it.value.postInit() }
            }
        }))

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            if (screen is GenericContainerScreen) {
                ChestOpenEvent(screen).call()
            }
        }
    }
}