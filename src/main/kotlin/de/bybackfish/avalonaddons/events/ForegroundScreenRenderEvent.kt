package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class ForegroundScreenRenderEvent(
    val that: Screen,
    val matrixStack: MatrixStack,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
):
    Event() {
}