package de.bybackfish.avalonaddons.screen

import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class ItemViewerOverlay : Screen(Text.of("ItemViewerScreen")) {
    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        UMinecraft.getMinecraft().textRenderer.drawWithShadow(
            matrices,
            "Hello World!",
            100f,
            100f,
            0xffffff
        )
    }

    override fun shouldPause(): Boolean {
        return false
    }
}