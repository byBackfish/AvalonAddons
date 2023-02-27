package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event
import net.minecraft.client.render.model.BakedModel
import net.minecraft.item.ItemStack

class ItemRenderGUIEvent(val item: ItemStack, val x: Int, val y: Int, val model: BakedModel) :
    Event() {
}