package de.bybackfish.avalonaddons.features.friends

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.config.FriendStatus
import de.bybackfish.avalonaddons.core.config.FriendsConfig
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.TeleportRequestEvent
import gg.essential.universal.ChatColor
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import java.util.*


@Category("Friends")
class IgnoredFeature : Feature() {

    @Property(
        description = "Ingore teleport rqeuests from ignored players",
        forceType = PropertyType.SWITCH
    )
    var ignoreTeleport = true

    @Property(
        description = "Don't welcome back ignored players",
        forceType = PropertyType.SWITCH
    )
    var preventWelcomeBack = true

    @Subscribe(priority = 100)
    fun onTeleport(event: TeleportRequestEvent.Incoming) {
        if (!ignoreTeleport) return
        if(!FriendsConfig.isIgnored(event.playerName)) return

        event.hideAll()
    }

    @Subscribe(priority = 100)
    fun onChat(event: ClientChatEvent.Received) {
        if (!preventWelcomeBack) return
        val message = ChatColor.stripColorCodes(event.message) ?: return

        val joinRegex = Regex("(.+) joined the game")
        val match = joinRegex.find(message) ?: return
        val playerName = match.groups.get(1) ?: return

        if (playerName.value == mc.player?.name?.string) return
        if(!FriendsConfig.isIgnored(playerName.value)) return

        event.isCancelled = true
        UChat.say("$playerName joined the game")
    }


}