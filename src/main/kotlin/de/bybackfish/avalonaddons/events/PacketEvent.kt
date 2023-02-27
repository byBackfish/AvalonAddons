package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.network.Packet

open class PacketEvent(val packet: Packet<*>) : Event() {
    class Outgoing(packet: Packet<*>) : PacketEvent(packet) {}
    class Incoming(packet: Packet<*>) : PacketEvent(packet) {}
}