package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.event.Event

class BossDefeatedEvent(val boss: Lootable, val killer: String) : Event() {


}