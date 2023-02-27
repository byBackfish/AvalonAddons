package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.util.math.MatrixStack

class RenderScreenEvent(val stack: MatrixStack, val delta: Float) : Event() {
}