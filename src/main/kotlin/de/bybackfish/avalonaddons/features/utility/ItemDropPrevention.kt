package de.bybackfish.avalonaddons.features.utility

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Keybind
import de.bybackfish.avalonaddons.core.annotations.RegisterKeybinds
import de.bybackfish.avalonaddons.core.config.LockedSlotsConfig
import de.bybackfish.avalonaddons.core.config.PersistentSave
import de.bybackfish.avalonaddons.core.event.Event
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.*
import de.bybackfish.avalonaddons.utils.drawTexturedRect
import gg.essential.universal.UChat
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

@Category("Utility")
@RegisterKeybinds
class ItemDropPrevention : Feature() {

    val LOCK_TEXTURE = Identifier(AvalonAddons.NAMESPACE, "textures/gui/item_lock.png")

    @Keybind(defaultKey = GLFW.GLFW_KEY_L, inGUI = true)
    fun lockItem(event: GUIKeyPressEvent) {
        println("Clicked on: ${event.itemClickedAt}")
        if (event.slot == null) return
        val slot = event.slot.index

        if(LockedSlotsConfig.toggle(slot)) {
            UChat.chat(AvalonAddons.PREFIX + "§aLocked slot §e$slot§a.")
            mc.player!!.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        } else {
            UChat.chat(AvalonAddons.PREFIX + "§aUnlocked slot §e$slot§a.")
            mc.player!!.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.1f)
        }

    }

    @Subscribe
    fun onItemAction(event: ItemActionEvent) {
        if (event.slot.inventory !is PlayerInventory) return
        checkEventForSlot(event.slot.index, event)
    }

    @Subscribe
    fun onItemDrop(event: ItemDropEvent.FromHotbar) {
        checkEventForSlot(event.slot, event)
    }

    fun checkEventForSlot(slot: Int, event: Event) {
        if (!LockedSlotsConfig.isLocked(slot)) return

        UChat.chat(AvalonAddons.PREFIX + "§cPrevented you from dropping this item. Please unlock it first.")
        mc.player!!.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f)
        event.isCancelled = true
    }

    @Subscribe()
    fun onEvent(event: DrawSlotEvent) {
        if (!LockedSlotsConfig.isLocked(event.slot.index)) return

        drawTexturedRect(
            event.matrices,
            LOCK_TEXTURE,
            (event.x + 12),
            event.y - 4,
            400,
            8,
            8,
            16 / 2,
            16 / 2
        );
    }

}