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
import net.minecraft.entity.LivingEntity
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
    var displayMode = 0

    @Property(forceType = PropertyType.SWITCH, description = "Should it also work on Non-Players?")
    var nonPlayer = false

    @Keybind(0)
    fun openGearViewer() {
        val playerLookingAt = mc.crosshairTarget
        if (playerLookingAt is EntityHitResult) {
            val entity = playerLookingAt.entity
            if (entity !is LivingEntity) return
            val living = entity as LivingEntity
            if(!nonPlayer && living !is PlayerEntity) return

            if (displayMode == 1) printInChat(living)
            else AvalonAddons.guiToOpen = GearViewerScreen(living)
        }

    }

    fun printInChat(entity: LivingEntity) {
        val armor = entity.armorItems.reversed()
        val armorTypes = listOf("Helmet", "Chestplate", "Leggings", "Boots")

        UChat.chat("§6 || §aGearViewer §8| §7${entity.displayName.string} §6||")

        // Helmet
        armor.forEachIndexed { index, item ->
            hoverableText("§b${armorTypes[index]}: §r", item)?.let { UChat.chat(it) }
        }

        // main hand
        hoverableText("§bMain Hand: §r", entity.mainHandStack)?.let { UChat.chat(it) }
        hoverableText("§bOff Hand: §r", entity.offHandStack)?.let { UChat.chat(it) }

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
