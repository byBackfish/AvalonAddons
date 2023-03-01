package de.bybackfish.avalonaddons.features.chat

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.EnabledByDefault
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.ClientChatEvent
import gg.essential.universal.ChatColor
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.Timer
import kotlin.concurrent.schedule

@EnabledByDefault
@Category("Chat")
class AutoChat : Feature() {

    @Property(
        sortingOrder = 1,
        forceType = PropertyType.SWITCH,
        description = "Automatically say 'wb' to returning players",

    )
    var automaticallySayWelcomeBack = false

    @Property(
        sortingOrder = 2,
        description = "Custom Welcome Back Message\nReplaces %s with the player name"
    )
    var customWelcomeBackMessage = "wb %s"

    @Property(
        sortingOrder = 3,
        forceType = PropertyType.SWITCH,
        description = "Automatically say 'gg gl' to players when a boss died"
    )
    var automaticallySayGoodLuck = false

    @Property(
        sortingOrder = 4,
        description = "Custom Good Luck Message\nUse %s to replace it with the boss name"
    )
    var customGoodLuckMessage = "gg gl on %s"

    @Property(
        sortingOrder = 5,
        forceType = PropertyType.SWITCH,
        description = "Automatically say 'welcome' to new players",
        hidden = true
    )
    var automaticallySayWelcome = false

    @Property
        (
        description = "Custom Welcome Message",
        sortingOrder = 6,
        hidden = true
    )
    var customWelcomeMessage = "Welcome!"

    @Subscribe
    fun onChat(event: ClientChatEvent.Received) {
        val message = ChatColor.stripColorCodes(event.message) ?: return

        if (automaticallySayWelcomeBack) {
            val joinRegex = Regex("(.+) joined the game")
            val match = joinRegex.find(message) ?: return
            val playerName = match.groups.get(1) ?: return

            if (playerName.value == mc.player?.name?.string) return


            // run something async and wait 5 seconds
            // use ExecutingThread to run something on the main thread
            Timer().schedule((Math.random() * 3000 + 500).toLong()) {
                UChat.say(customWelcomeBackMessage.replace("%s", playerName.value))
            }

        }
    }

    @Subscribe
    fun onBossDeath(event: BossDefeatedEvent) {
        if (automaticallySayGoodLuck) {
            Timer().schedule((Math.random() * 3000 + 500).toLong()) {
                UChat.say(customGoodLuckMessage.replace("%s", event.boss.name))
            }
        }
    }

}