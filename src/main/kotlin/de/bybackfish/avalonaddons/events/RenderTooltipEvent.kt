package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class RenderTooltipEvent(
    val matrices: MatrixStack,
    val item: ItemStack,
    val mouseX: Int,
    val mouseY: Int,
    val forceTooltip: MutableList<Text>
) : Event()
