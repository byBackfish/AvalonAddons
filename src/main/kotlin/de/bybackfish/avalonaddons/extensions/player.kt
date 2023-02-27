package de.bybackfish.avalonaddons.extensions

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

fun PlayerEntity.findItemInHotbar(itemType: Item): ItemStack? {
    for(i in 0..8) {
        val itemStack = this.inventory.getStack(i)
        if(itemStack.item == itemType) {
            this.inventory.selectedSlot = i
            return itemStack
        }
    }
    return null
}

fun PlayerEntity.findItemInInventory(itemType: Item): ItemStack? {
    for(i in 9..35) {
        val itemStack = this.inventory.getStack(i)
        if(itemStack.item == itemType) {
            this.inventory.selectedSlot = i
            return itemStack
        }
    }
    return null
}

fun PlayerEntity.findItemAnywhere(itemType: Item): ItemStack? {
    return this.findItemInHotbar(itemType) ?: this.findItemInInventory(itemType)
}