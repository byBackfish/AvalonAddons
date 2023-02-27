package de.bybackfish.avalonaddons.avalon

import gg.essential.universal.ChatColor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

enum class ItemRarity {

    // rarities of RarityBackgroundFeature
    TRASH,
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    EXOTIC,
    LEGENDARY,
    RELIC,
    ELDER,
    ANCIENT;


    companion object {
        fun getFromItem(stack: ItemStack): ItemRarity? {
            val tooltip = stack.getTooltip(
                MinecraftClient.getInstance().player,
                TooltipContext.Default.ADVANCED
            )
            return getFromTooltip(tooltip)
        }

        private fun getFromTooltip(tooltip: MutableList<Text>): ItemRarity? {
            val rarityText = tooltip.find {
                ChatColor.stripColorCodes(it.string)!!.startsWith("Tier: ")
            } ?: return null

            val rarity = ChatColor.stripColorCodes(rarityText.string)!!.replace("Tier: ", "")

            return try {
                valueOf(rarity)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}