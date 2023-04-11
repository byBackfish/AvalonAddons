package de.bybackfish.avalonaddons.events.gui

import de.bybackfish.avalonaddons.core.event.Event

class ClickGuiEvent(
    val x: Double,
    val y: Double,
    val button: Int
) : Event()