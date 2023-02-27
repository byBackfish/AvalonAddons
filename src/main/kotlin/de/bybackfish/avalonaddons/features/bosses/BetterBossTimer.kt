package de.bybackfish.avalonaddons.features.bosses

import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.ClientChatEvent
import gg.essential.universal.ChatColor
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType

@Category("Bosses")
class BetterBossTimer : Feature() {

    @Property(forceType = PropertyType.SWITCH, description = "Render Boss Timers on screen")
    var renderBossTimersOnScreen = false


    var lastBoss: Bosses? = null
    var lastBossTimestamp: Long = 0

    val bossTeleportTimeoutDuration =  10*1000
    val bossLootTimeoutDuration = 30*1000

    var stage = 0;
    @Subscribe
    fun onBossDeath(event: BossDefeatedEvent) {
        lastBoss = event.boss

        lastBossTimestamp = System.currentTimeMillis()
        stage = 0;
        UChat.chat("Boss CD step 0")
    }

    val TELEPORT_REGEX = Regex("\\[Avalon] Teleporting you in 5 seconds...")
    val CLASS_XP_REGEX = Regex("You gained (\\\\d+) class exp!")
    @Subscribe
    fun onChat(event: ClientChatEvent.Received) {
        val message = ChatColor.stripColorCodes(event.message)!!
        if(stage == 0 && TELEPORT_REGEX.find(message) != null && lastBoss != null &&  System.currentTimeMillis() - lastBossTimestamp < bossTeleportTimeoutDuration)
        {
            stage = 1;
            UChat.chat("Boss CD step 1")
        }
        if(CLASS_XP_REGEX.find(message) != null && stage == 1 && lastBoss != null &&  System.currentTimeMillis() - lastBossTimestamp < bossLootTimeoutDuration)
        {
            stage = 2;
            UChat.chat("Boss CD step 2")
        }

    }

}