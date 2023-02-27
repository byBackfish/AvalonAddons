package de.bybackfish.avalonaddons.core.event

import de.bybackfish.avalonaddons.AvalonAddons

abstract class Event(val stage: EventStage = EventStage.NONE) {
    var isCancelled: Boolean = false

    fun call(): Boolean {
        return AvalonAddons.bus.post(this)
    }
}

enum class EventStage {
    PRE, POST, NONE
}