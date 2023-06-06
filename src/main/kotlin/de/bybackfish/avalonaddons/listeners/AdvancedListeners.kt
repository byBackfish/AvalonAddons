package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.events.*
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket

class AdvancedListeners {

    @Subscribe
    fun onPacket(event: PacketEvent.Incoming) {
        if (event.packet is GameMessageS2CPacket) {
            if (ClientChatEvent.Received(event.packet.content.string).call()) event.isCancelled =
                true
        }

        if (event.packet is ChatMessageC2SPacket) {
            println("Message Event!: ${event.packet.chatMessage}")
        }

        // action bar message
        if (event.packet is GameMessageS2CPacket) {
            //     println("Action bar message: ${event.packet.content.string}")
            if (ActionBarMessageEvent(event.packet).call()) event.isCancelled = true
        }
    }

    @Subscribe
    fun onChestOpen(event: ChestOpenEvent) {
        val lootable = Lootable.getFromContainer(event.container)
        if (lootable != null)
            LootableChestEvent(lootable, event.container).call()
    }
}