package de.bybackfish.avalonaddons.screen

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.components.UIEntity
import de.bybackfish.avalonaddons.core.components.UIItem
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMatrixStack
import gg.essential.universal.USound
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import java.awt.Color

class GearViewerScreen(val entity: LivingEntity) : Screen(Text.of("Yes Screen")) {

    private lateinit var armorItems: List<ItemStack>
    private lateinit var mainHandItem: ItemStack
    private lateinit var offHandItem: ItemStack

    private val armorTypes = arrayOf("Helmet", "Chestplate", "Leggings", "Boots")

    private val window = Window(ElementaVersion.V2)

    init {
        update()
    }

    private fun update() {
        USound.playLevelupSound()
        armorItems = entity.armorItems.toList().reversed()
        mainHandItem = entity.mainHandStack
        offHandItem = entity.offHandStack

        val centerX = CenterConstraint()
        val centerY = CenterConstraint()

        val offset = 20.pixels()
        val startY = centerY.minus(offset * 2)

        val xOffset = 20.pixels()

        val boxColor = Color(0, 0, 0, 127)

        UIEntity(entity).constrain {
            x = CenterConstraint().minus(60.pixels())
            y = CenterConstraint().plus(25.pixels())
            textScale = 40.pixels()
        } childOf window

        armorItems.forEachIndexed { index, item ->
            val block = UIBlock(boxColor).constrain {
                x = CenterConstraint().minus(xOffset).minus(2.pixels())
                y = startY.plus(offset * index).minus(2.pixels())
                width = 20.pixels()
                height = 20.pixels()
            } childOf window

            UIItem(item, dummyItem(armorTypes[index]), ::renderTooltip).constrain {
                x = SiblingConstraint().plus(2.pixels())
                y = SiblingConstraint().plus(2.pixels())
                zOffset = -5
            } childOf block
        }

        val mainBlock = UIBlock(boxColor).constrain {
            x = CenterConstraint().plus(xOffset).minus(2.pixels())
            y = startY.plus(offset * 0.5).minus(2.pixels())
            width = 20.pixels()
            height = 20.pixels()
        } childOf window

        UIItem(mainHandItem, dummyItem("Mainhand"), ::renderTooltip).constrain {
            x = SiblingConstraint().plus(2.pixels())
            y = SiblingConstraint().plus(2.pixels())
        } childOf mainBlock

        val offBlock = UIBlock(boxColor).constrain {
            x = CenterConstraint().plus(xOffset).minus(2.pixels())
            y = startY.plus(offset * 2.5).minus(2.pixels())
            width = 20.pixels()
            height = 20.pixels()
        } childOf window

        UIItem(offHandItem, dummyItem("Offhand"), ::renderTooltip).constrain {
            x = SiblingConstraint().plus(2.pixels())
            y = SiblingConstraint().plus(2.pixels())
        } childOf offBlock

        val buttonBlock = UIBlock(boxColor).constrain {
            x = CenterConstraint()
            y = startY.plus(offset * 5)
            width = 60.pixels()
            height = 20.pixels()
        } childOf window

        UIText("§aUpdate").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        }.onMouseClick {
            AvalonAddons.guiToOpen = GearViewerScreen(entity)
        } childOf buttonBlock

    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        window.draw(UMatrixStack(matrices!!))
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        window.mouseClick(mouseX, mouseY, button)
        return super.mouseClicked(mouseX, mouseY, button)
    }



    private fun dummyItem(name: String): ItemStack {
        return ItemStack(Items.BARRIER).setCustomName(Text.of("No $name :("))
    }

}
