package de.bybackfish.avalonaddons.events.gui

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class RenderGuiEvent(
    val screen: Screen,
    val matrices: MatrixStack,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
) : Event()