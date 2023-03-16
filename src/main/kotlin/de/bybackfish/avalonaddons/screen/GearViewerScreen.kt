package de.bybackfish.avalonaddons.screen

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UGraphics
import gg.essential.universal.UResolution
import gg.essential.universal.USound
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import java.awt.Color
import java.util.function.Consumer
import kotlin.math.roundToInt

class GearViewerScreen(val entity: LivingEntity) : Screen(Text.of("Yes Screen")) {

    private lateinit var armorItems: List<ItemStack>
    private lateinit var mainHandItem: ItemStack
    private lateinit var offHandItem: ItemStack

    private val armorTypes = arrayOf("Helmet", "Chestplate", "Leggings", "Boots")

    init {
        update()
    }

    private fun update() {
        USound.playExpSound()
        armorItems = entity.armorItems.toList().reversed()
        mainHandItem = entity.mainHandStack
        offHandItem = entity.offHandStack
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        DrawableHelper.fill(matrices, 0, 0, width, height, 0x80000000.toInt())


        super.render(matrices, mouseX, mouseY, delta)
        val itemRenderer = MinecraftClient.getInstance().itemRenderer


        val offset = 20;
        val startX = width / 2;
        val startY = 300;


        MinecraftClient.getInstance().textRenderer.drawWithShadow(
            matrices,
            entity.displayName.string + "'s Gear",
            startX.toFloat() - 50,
            startY.toFloat() - 30,
            0xffffff
        )

        // draw entity on screen
        InventoryScreen.drawEntity(
            startX - 110,
            startY + 85,
            50,
            (startX + 40 - mouseX).toFloat(),
            (startY + 40 - mouseY).toFloat(),
            entity
        )



        for (i in armorItems.indices) {
            val item = getItemOrDefault(armorItems[i])

            val x = startX
            val y = startY + i * offset
            renderItem(
                matrices!!,
                armorTypes[i],
                item,
                x,
                y,
                mouseX,
                mouseY,
                itemRenderer,
                -MinecraftClient.getInstance().textRenderer.getWidth(armorTypes[i]) - 10
            )
        }

        var item = getItemOrDefault(mainHandItem)
        renderItem(
            matrices!!,
            "Main Hand",
            item,
            startX + 44,
            startY + (offset / 2),
            mouseX,
            mouseY,
            itemRenderer,
            22
        )

        item = getItemOrDefault(offHandItem)
        renderItem(
            matrices, "Off Hand", item, startX + 44,
            (startY + (offset * 2.5)).roundToInt(), mouseX, mouseY, itemRenderer, 22
        )
    }

    private fun renderItem(
        matrices: MatrixStack,
        type: String,
        item: ItemStack,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int,
        itemRenderer: ItemRenderer,
        typeXOffset: Int = -50
    ) {
        DrawableHelper.fill(matrices, x - 4, y - 4, x + 20, y + 20, 0x7F000000)
        MinecraftClient.getInstance().textRenderer.draw(
            matrices,
            type,
            x.toFloat() + typeXOffset,
            y.toFloat() + 4,
            0xffffff
        )

        itemRenderer.renderGuiItemIcon(item, x, y)
        itemRenderer.renderGuiItemOverlay(
            MinecraftClient.getInstance().textRenderer,
            item,
            x,
            y,
            null
        )

        if (mouseX > x && mouseX < x + 16 && mouseY > y && mouseY < y + 16) {
            renderTooltip(matrices, item, mouseX, mouseY)
        }
    }

    fun getItemOrDefault(item: ItemStack): ItemStack {
        return if (item.item == Items.AIR)
            ItemStack(Items.BARRIER).setCustomName(Text.of("None :("))
        else item
    }

}