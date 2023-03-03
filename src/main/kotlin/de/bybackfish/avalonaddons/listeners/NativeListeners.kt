package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.core.event.EventBus
import de.bybackfish.avalonaddons.events.ClientTickEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.DebugHud

class NativeListeners {
    fun load(bus: EventBus) {
        HudRenderCallback.EVENT.register((HudRenderCallback { matrixStack, tickDelta ->
            // check if the player is in the debug (f3) menu
            if(MinecraftClient.getInstance().options.debugEnabled) return@HudRenderCallback
            bus.post(RenderScreenEvent(matrixStack, tickDelta))
        }))

        ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick { client ->
            bus.post(ClientTickEvent())
        }))
    }
}