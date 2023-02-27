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
        description = "Custom Welcome Back Message"
    )
    var customWelcomeBackMessage = "wb"

    @Property(
        sortingOrder = 3,
        forceType = PropertyType.SWITCH,
        description = "Automatically say 'gg gl' to players when a boss died"
    )
    var automaticallySayGoodLuck = false

    @Property(
        sortingOrder = 4,
        description = "Custom Good Luck Message"
    )
    var customGoodLuckMessage = "gg gl"

    @Property(
        sortingOrder = 5,
        forceType = PropertyType.SWITCH,
        description = "Automatically say 'welcome' to new players"
    )
    var automaticallySayWelcome = false

    @Property
        (
        description = "Custom Welcome Message",
        sortingOrder = 6
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
            UChat.say(customWelcomeBackMessage)
        }
    }

    @Subscribe
    fun onBossDeath(event: BossDefeatedEvent) {
        if (automaticallySayGoodLuck) {
            UChat.say(customGoodLuckMessage)
        }
    }

}