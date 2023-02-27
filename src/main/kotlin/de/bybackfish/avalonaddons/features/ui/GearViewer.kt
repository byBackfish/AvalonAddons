package de.bybackfish.avalonaddons.features.ui

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Keybind
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.annotations.RegisterKeybinds
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.screen.GearViewerScreen
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.*
import net.minecraft.text.HoverEvent.ItemStackContent
import net.minecraft.util.hit.EntityHitResult

@Category("UI")
@RegisterKeybinds
class GearViewer : Feature() {

    @Property(forceType = PropertyType.SELECTOR, options = ["GUI", "Chat"])
    var displayMode = 1

    var lastTime = -1L

    @Keybind(0)
    fun openGearViewer() {
        if (System.currentTimeMillis() - lastTime < 1000) return
        lastTime = System.currentTimeMillis()

        val playerLookingAt = mc.crosshairTarget
        if (playerLookingAt is EntityHitResult) {
            val entity = playerLookingAt.entity
            if (entity !is PlayerEntity) return
            val player = entity as PlayerEntity


            if (displayMode == 1) printInChat(player)
            else AvalonAddons.guiToOpen = GearViewerScreen(player)
        }

    }

    fun printInChat(player: PlayerEntity) {
        val armor = player.armorItems.reversed()
        val armorTypes = listOf("Helmet", "Chestplate", "Leggings", "Boots")

        UChat.chat("§6 || §aGearViewer §8| §7${player.displayName.string} §6||")

        // Helmet
        armor.forEachIndexed { index, item ->
            hoverableText("§b${armorTypes[index]}: §r", item)?.let { UChat.chat(it) }
        }

        // main hand
        hoverableText("§bMain Hand: §r", player.mainHandStack)?.let { UChat.chat(it) }
        hoverableText("§bOff Hand: §r", player.offHandStack)?.let { UChat.chat(it) }

        UChat.chat("§6--------------------")
    }

    fun hoverableText(prefix: String, item: ItemStack): MutableText? {
        val itemName = if (item.item == Items.AIR) Text.empty().append("None :(") else item.name
        val mutableText: MutableText = Text.empty().append(prefix).append(itemName)

        if (!item.isEmpty) {
            mutableText.styled { style: Style ->
                style.withHoverEvent(
                    HoverEvent(HoverEvent.Action.SHOW_ITEM, ItemStackContent(item))
                )
            }
        }

        return mutableText
    }

}

private operator fun <T> MutableIterable<T>.get(i: Int): T {
    // return the element at index i
    return this.elementAt(i)
}
