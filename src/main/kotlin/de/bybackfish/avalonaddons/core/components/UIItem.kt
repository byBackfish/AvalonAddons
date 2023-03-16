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
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class UIItem(
    var item: ItemStack?,
    val default: ItemStack = ItemStack(Items.BARRIER).setCustomName(Text.of("None :(")),
    val renderTooltip: Boolean = true
): UIComponent() {

    private val textScaleState = constraints.asState { getTextScale() }
    private val verticallyCenteredState = constraints.asState { y is CenterConstraint }
    private val fontProviderState = constraints.asState { fontProvider }

    private fun <T> UIConstraints.asState(selector: UIConstraints.() -> T) = BasicState(selector(constraints)).also {
        constraints.addObserver { _, _ -> it.set(selector(constraints)) }
    }

    init {
        setWidth(16.pixels())
        setHeight(16.pixels())
    }

    override fun getWidth(): Float {
        return super.getWidth() * getTextScale()
    }

    override fun getHeight(): Float {
        return super.getHeight() * getTextScale()
    }

    override fun draw(matrixStack: UMatrixStack) {
        if (item == null || item!!.isEmpty) item = default
        beforeDrawCompat(matrixStack)

        val scale = getWidth() / getHeight()
        val x = getLeft()
        val y = getTop() + (if (verticallyCenteredState.get()) fontProviderState.get().getBelowLineHeight() * scale else 0f)

        UMinecraft.getMinecraft().itemRenderer.renderInGuiWithOverrides(item, x.toInt(), y.toInt());

        if(isHovered() && renderTooltip) {
            val mousePosition = getMousePosition()
            (UMinecraft.getMinecraft().currentScreen as AccessorScreen).invokeRenderTooltip(matrixStack.toMC(), item!!, mousePosition.first.toInt(), mousePosition.second.toInt())
        }

        super.draw(matrixStack)
    }

}