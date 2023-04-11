package de.bybackfish.avalonaddons.core.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class UIItem(
    var item: ItemStack?,
    private val default: ItemStack = ItemStack(Items.BARRIER).setCustomName(Text.of("None :(")),
    private val renderTooltipCallback: ((matrices: MatrixStack, item: ItemStack, x: Int, y: Int) -> Unit)? = null,
): UIComponent() {

    private val verticallyCenteredState = constraints.asState { y is CenterConstraint }
    private val fontProviderState = constraints.asState { fontProvider }

    private fun <T> UIConstraints.asState(selector: UIConstraints.() -> T) = BasicState(selector(constraints)).also {
        constraints.addObserver { _, _ -> it.set(selector(constraints)) }
    }

    init {
        setWidth(16.pixels())
        setHeight(16.pixels())
    }

    override fun draw(matrixStack: UMatrixStack) {
        if (item == null || item!!.isEmpty) item = default
        beforeDrawCompat(matrixStack)

        val x = getLeft()
        val y = getTop() + (if (verticallyCenteredState.get()) fontProviderState.get()
            .getBelowLineHeight() else 0f)

        UMinecraft.getMinecraft().itemRenderer.renderGuiItemIcon(item, x.toInt(), y.toInt())

        if (isHovered()) {
            val mousePosition = getMousePosition()
            matrixStack.translate(0.0, 0.0, 50.0)

            renderTooltipCallback?.invoke(
                matrixStack.toMC(),
                item!!,
                mousePosition.first.toInt(),
                mousePosition.second.toInt()
            )
        }

        super.draw(matrixStack)
    }

}