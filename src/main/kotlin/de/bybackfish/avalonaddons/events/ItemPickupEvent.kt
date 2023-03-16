package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.item.ItemStack

class ItemPickupEvent(val item: ItemStack, val slot: Int) : Event()