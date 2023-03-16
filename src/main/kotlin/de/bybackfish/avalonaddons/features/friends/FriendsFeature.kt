package de.bybackfish.avalonaddons.features.friends

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.config.FriendStatus
import de.bybackfish.avalonaddons.core.config.FriendsConfig
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.TeleportRequestEvent
import gg.essential.vigilance.data.PropertyType

@Category("Friends")
class FriendsFeature : Feature() {

    @Property(
        description = "Automatically accept teleport requests from friends",
        forceType = PropertyType.SWITCH
    )
    var autoAcceptTeleport = true

    @Subscribe(priority = 100)
    fun onTeleport(event: TeleportRequestEvent.Incoming) {
        if (!autoAcceptTeleport) return
        val friends = FriendsConfig.get()
        if(!FriendsConfig.isFriend(event.playerName)) return

        event.hideAll()
        event.accept()
    }

}