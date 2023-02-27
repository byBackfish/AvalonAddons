package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.core.event.Event

class BossDefeatedEvent(val boss: Bosses, val killer: String) : Event() {


}