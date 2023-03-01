package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.PacketEvent
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket

class AdvancedListeners {

    @Subscribe
    fun onPacket(event: PacketEvent.Incoming) {
        if (event.packet is GameMessageS2CPacket) {
            if (ClientChatEvent.Received(event.packet.content.string).call()) event.isCancelled =
                true
        }
    }

}