package de.bybackfish.avalonaddons.features.chat

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.EnabledByDefault
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.TeleportRequestEvent
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType

@Category("Chat")
@EnabledByDefault
class AutoAcceptTeleportRequest : Feature() {


    @Property(
        forceType = PropertyType.SWITCH,
        description = "Always accept all teleport requests"
    )
    var alwaysAccept = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Accept all teleport requests after a boss has been defeated"
    )
    var acceptAfterBoss = true

    @Property(
        forceType = PropertyType.SLIDER,
        description = "What duration should the teleport request be accepted after a boss has been defeated",
        min = 1000,
        max = 20000
    )
    var acceptAfterBossDuration = 10000


    var selfKilledABoss = false
    var lastBossKill = 0L

    @Subscribe
    fun onTeleportRequestIncoming(event: TeleportRequestEvent.Incoming) {
        if (alwaysAccept) {
            event.accept()
            event.hideAll()

            UChat.chat("§aAccepted teleport request from §b${event.playerName}§a...")
            return
        }

        if (acceptAfterBoss && selfKilledABoss && System.currentTimeMillis() - lastBossKill < acceptAfterBossDuration) {
            event.accept()
            event.hideAll()
        }
    }

    @Subscribe
    fun onBossKill(event: BossDefeatedEvent) {
        if (event.killer == mc.player?.name?.string) {
            selfKilledABoss = true
            lastBossKill = System.currentTimeMillis()
        }
    }


}