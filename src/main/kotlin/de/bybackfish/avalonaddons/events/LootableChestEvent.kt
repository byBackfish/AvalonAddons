package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen

class LootableChestEvent(val lootable: Lootable, val screen: GenericContainerScreen): Event() {
}