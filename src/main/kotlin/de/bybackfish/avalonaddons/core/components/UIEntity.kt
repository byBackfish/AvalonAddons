package de.bybackfish.avalonaddons.core.components

import de.bybackfish.avalonaddons.mixins.accessors.AccessorScreen
import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.width
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.MappedState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.pixels
import gg.essential.universal.UGraphics
import gg.essential.universal.UGuiButton
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class UIEntity(
    val entity: LivingEntity,
): UIComponent() {

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val scale = getTextScale()
        val x = getLeft()
        val y = getTop()

        val mousePosition = getMousePosition()

        val mouseX = (x + 40 - mousePosition.first)
        val mouseY = (y + 40 - mousePosition.second)

        InventoryScreen.drawEntity(x.toInt(), y.toInt(), scale.toInt(), mouseX, mouseY, entity)


        super.draw(matrixStack)
    }

}