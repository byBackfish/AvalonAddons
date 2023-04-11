package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.util.math.MatrixStack

class InventoryDrawEvent(
    val matrices: MatrixStack,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
) : Event()