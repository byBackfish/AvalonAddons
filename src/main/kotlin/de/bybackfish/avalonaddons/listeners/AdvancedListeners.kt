package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.events.ChestOpenEvent
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.LootableChestEvent
import de.bybackfish.avalonaddons.events.PacketEvent
import net.minecraft.network.Packet
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import java.util.*

class AdvancedListeners {

    @Subscribe
    fun onPacket(event: PacketEvent.Incoming) {
        if (event.packet is GameMessageS2CPacket) {
            if (ClientChatEvent.Received(event.packet.content.string).call()) event.isCancelled =
                true
        }
    }

    @Subscribe
    fun onChestOpen(event: ChestOpenEvent) {
        val lootable = Lootable.getFromContainer(event.container)
        if (lootable != null)
            LootableChestEvent(lootable, event.container).call()
    }

}