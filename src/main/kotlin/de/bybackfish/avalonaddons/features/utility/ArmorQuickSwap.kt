package de.bybackfish.avalonaddons.features.utility

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Keybind
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.annotations.RegisterKeybinds
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.extensions.findItemAnywhere
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import net.minecraft.screen.slot.SlotActionType

@RegisterKeybinds
@Category("Utility")
class ArmorQuickSwap: Feature() {

    val armorSlots = arrayOf(5, 6, 7, 8)

    @Property(
        description = "Name of the Helmet to swap to",
        forceType = PropertyType.TEXT,
        sortingOrder = 1
    )
    var helmet = ""

    @Property(
        description = "Name of the Chestplate to swap to",
        forceType = PropertyType.TEXT,
        sortingOrder = 2
    )
    var chestplate = "✦ Guardian's Wings ✦"

    @Property(
        description = "Name of the Leggings to swap to",
        forceType = PropertyType.TEXT,
        sortingOrder = 3
    )
    var leggings = ""

    @Property(
        description = "Name of the Boots to swap to",
        forceType = PropertyType.TEXT,
        sortingOrder = 4
    )
    var boots = "✦ Rocket Boots ✦"


    var oldBoots = ""
    var oldLeggings = ""
    var oldChestplate = ""
    var oldHelmet = ""

    var swapped = false
    @Keybind(defaultKey = 0)
    fun swapArmor() {
        if(!swapped) {
            oldBoots = equipArmor(8, boots)
            oldLeggings = equipArmor(7, leggings)
            oldChestplate = equipArmor(6, chestplate)
            oldHelmet = equipArmor(5, helmet)

            swapped = true
        } else {
            equipArmor(8, oldBoots)
            equipArmor(7, oldLeggings)
            equipArmor(6, oldChestplate)
            equipArmor(5, oldHelmet)

            swapped = false
        }
    }

    private fun equipArmor(slot: Int, itemName: String): String {
        val itemStack = player!!.findItemAnywhere(itemName) ?: return ""
        val slotOfItem = player!!.inventory.main.indexOf(itemStack)


        mc.interactionManager!!.clickSlot(
            player!!.playerScreenHandler.syncId,
            slot,
            slotOfItem,
            SlotActionType.SWAP,
            player
        )

        val oldItem = player!!.inventory.getStack(slotOfItem)
        val oldName = oldItem.name.string

        UChat.chat("§aSwapped §e${oldName} §awith §e${itemName}§a.")

        return oldName
    }





}