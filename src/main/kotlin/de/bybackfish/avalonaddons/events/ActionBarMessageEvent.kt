package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket

class ActionBarMessageEvent(val packet: GameMessageS2CPacket) : Event()