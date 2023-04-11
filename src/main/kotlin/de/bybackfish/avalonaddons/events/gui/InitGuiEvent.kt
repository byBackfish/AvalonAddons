package de.bybackfish.avalonaddons.events.gui

import de.bybackfish.avalonaddons.core.event.Event
import de.bybackfish.avalonaddons.mixins.accessors.AccessorScreen
import net.minecraft.client.gui.screen.Screen

class InitGuiEvent(val screen: Screen) : Event() {
    val accessor = screen as AccessorScreen
}