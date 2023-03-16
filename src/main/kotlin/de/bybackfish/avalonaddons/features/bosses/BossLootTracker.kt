package de.bybackfish.avalonaddons.features.bosses

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.config.BossLootConfig
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.LootableChestEvent
import gg.essential.universal.UChat
import java.util.*

@Category("Bosses")
class BossLootTracker: Feature() {

    private val claimedIds = mutableListOf<String>()

    @Subscribe
    fun onBossLoot(event: LootableChestEvent) {
        val handler = event.screen.screenHandler
        if(claimedIds.contains(event.screen.title.string.replace(" ", "_"))) return
        claimedIds.add(event.screen.title.string.replace(" ", "_"))

        Timer().schedule(object: TimerTask() {
            override fun run() {
                val items = handler.inventory.size().let { (0 until it).map { handler.inventory.getStack(it) } }
                    .filter { !it.isEmpty }
                    .map { Pair(it.name.string, it.count) }

                items.forEach {
                    BossLootConfig.addLoot(event.lootable, it.first, it.second)
                }
                UChat.chat("Added loot for ${event.lootable} with ${items.size} items")
            }
        }, 100)

    }

}