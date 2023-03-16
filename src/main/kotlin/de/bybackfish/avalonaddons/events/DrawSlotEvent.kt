package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class DrawSlotEvent(
    val matrices: MatrixStack,
    val slot: Slot,
    val stack: ItemStack,
    val x: Int,
    val y: Int
) : Event()