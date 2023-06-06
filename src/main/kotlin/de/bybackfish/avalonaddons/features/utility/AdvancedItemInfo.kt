package de.bybackfish.avalonaddons.features.utility

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Keybind
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.annotations.RegisterKeybinds
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.GUIKeyPressEvent
import de.bybackfish.avalonaddons.events.RenderTooltipEvent
import de.bybackfish.avalonaddons.features.ui.ItemViewer
import gg.essential.universal.UChat
import gg.essential.universal.USound
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text

@Category("Utility")
@RegisterKeybinds
class AdvancedItemInfo : Feature() {

    val extraTooltip = hashMapOf<ItemStack, List<Text>>()

    @Property(description = "Banana")
    val a = 0

    @Keybind(-1, true)
    fun copyNBT(event: GUIKeyPressEvent) {
        if (event.itemClickedAt == null) {
            USound.playSoundStatic(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 1f, 1f)
            return
        }
        val nbt = event.itemClickedAt.nbt.toString()

        MinecraftClient.getInstance().keyboard.clipboard = nbt
        UChat.chat("§7§l[§b§lAvalon§3§lAddons§7§l]§r §b> §fCopied NBT to clipboard!")
        USound.playPlingSound()
    }

    @Subscribe
    fun onRenderTooltip(event: RenderTooltipEvent) {
        val nbt = event.item.nbt
        if (nbt != null) {
            val id = nbt.getString("MMOITEMS_ITEM_ID") ?: "Unknown"
            val type = nbt.getString("MMOITEMS_ITEM_TYPE") ?: "Unknown"

            if (id.trim().isEmpty() && type.trim().isEmpty()) return

            val tooltip = event.item.getTooltip(player, ItemViewer.getTooltipMode())
            tooltip.add(
                Text.of(
                    "§8${type}:${id}"
                )
            )

            if (extraTooltip.containsKey(event.item)) {
                tooltip.addAll(extraTooltip[event.item]!!)
            }

            event.forceTooltip.addAll(tooltip)
        }
    }


}