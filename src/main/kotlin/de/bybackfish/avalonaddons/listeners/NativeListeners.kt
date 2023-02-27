package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.core.event.EventBus
import de.bybackfish.avalonaddons.events.ClientTickEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

class NativeListeners {
    fun load(bus: EventBus) {
        HudRenderCallback.EVENT.register((HudRenderCallback { matrixStack, tickDelta ->
            bus.post(RenderScreenEvent(matrixStack, tickDelta))
        }))

        ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick { client ->
            bus.post(ClientTickEvent())
        }))
    }
}