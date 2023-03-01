package de.bybackfish.avalonaddons.screen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import kotlin.math.roundToInt

class GearViewerScreen(val player: PlayerEntity) : Screen(Text.of("Yes Screen")) {

    var armorItems: List<ItemStack>
    var mainHandItem: ItemStack
    var offHandItem: ItemStack

    var allItems: List<ItemStack>

    val armorTypes = arrayOf("Helmet", "Chestplate", "Leggings", "Boots")

    init {
        armorItems = player.armorItems.toList().reversed()
        mainHandItem = player.mainHandStack
        offHandItem = player.offHandStack

        allItems = armorItems + mainHandItem + offHandItem
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        DrawableHelper.fill(matrices, 0, 0, width, height, 0x80000000.toInt())


        super.render(matrices, mouseX, mouseY, delta)
        val itemRenderer = MinecraftClient.getInstance().itemRenderer


        val offset = 20;
        var startX = width / 2;
        val startY = 300;

        MinecraftClient.getInstance().textRenderer.drawWithShadow(
            matrices,
            player.displayName.string + "'s Gear",
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
            player
        )



        for (i in armorItems.indices) {
            var item = getItemOrDefault(armorItems[i])

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

    fun renderItem(
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