package de.bybackfish.avalonaddons.core.components

import gg.essential.elementa.components.UIText
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.times
import gg.essential.universal.UMatrixStack

class UIMultilineText(val content: String) : UIText() {

    override fun draw(matrixStack: UMatrixStack) {
        val lines = content.split("\n")

        lines.forEachIndexed { index, line ->
            super.setText(line)
            super.draw(matrixStack)
            val fontHeight = super.getFontProvider().getBelowLineHeight()
            super.setY(super.getHeight().pixels().plus(fontHeight.pixels() * index))
        }
    }
}