package de.bybackfish.avalonaddons.features.bosses

import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Keybind
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.annotations.RegisterKeybinds
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import gg.essential.universal.ChatColor
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.gui.DrawableHelper

@Category("Bosses")
@RegisterKeybinds
class TPAFeature : Feature() {

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Automatically prompt you to send a tpa to players near a dead boss"
    )
    var automaticallyPromptTeleportToLoot = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Automatically accept tpa requests from players, if you are near a dead boss"
    )
    var automaticallyAcceptTPA = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Automatically send a tpa requests to a players near a dead boss, without confirmation"
    )
    var automaticallyTeleportWithoutConfirmation = false

    @Property(
        forceType = PropertyType.SLIDER,
        max = 30000,
        min = 1000,
        increment = 500,
        description = "After what duration (ms) should the popup for the tpa request disappear?"
    )
    var tpPopupDuration = 3000

    var teleportRequestTarget: String? = null
    var teleportBossName: Bosses? = null
    var teleportRequestTimestamp: Long = 0

    var selfKilledABoss = false;
    var selfKilledBossDuration = 15000;
    var selfKilledBossTimestamp = 0L;


    var cancelNextTPARequestIncoming = false;
    var cancelNextTPARequestOutgoing = false;

    @Keybind(
        defaultKey = 89
    )
    fun acceptRequest() {
        if (resetIfExpired()) return

        player?.sendCommand("tpa $teleportRequestTarget")
        UChat.chat("§aTeleporting to §b${teleportRequestTarget}§a...")
        teleportRequestTarget = null
        teleportRequestTimestamp = 0
        teleportBossName = null
        cancelNextTPARequestOutgoing = true
    }

    @Keybind(
        defaultKey = 78
    )
    fun cancelRequest() {
        if (resetIfExpired()) return

        UChat.chat("§cCancelled teleport request to §b${teleportRequestTarget}§c.")
        teleportRequestTarget = null
        teleportRequestTimestamp = 0L
        teleportBossName = null
    }

    @Subscribe
    fun onBossKill(event: BossDefeatedEvent) {
        if (event.killer == mc.player?.name?.string) {
            selfKilledABoss = true
            selfKilledBossTimestamp = System.currentTimeMillis()

            resetIfExpired()
            return
        }
        if (!automaticallyAcceptTPA) return

        if (automaticallyTeleportWithoutConfirmation) {
            player?.sendCommand("tpa ${event.killer}", null)
            UChat.chat("§aAutomatically teleporting to §b${event.killer}§a...")
            cancelNextTPARequestOutgoing = true
            return
        }

        teleportRequestTarget = event.killer
        teleportBossName = event.boss
        teleportRequestTimestamp = System.currentTimeMillis()

        UChat.chat("§a${event.killer} §7is near a dead boss. §b§lPress §aY§7/§cN §b to §aaccept§7/§ccancel §b the request.")
    }

    @Subscribe
    fun onMessage(event: ClientChatEvent.Received) {
        if(checkCancelNextMessage(event)) {
            println("Cancelling ${event.message} ||| NI: $cancelNextTPARequestIncoming ||| NO: $cancelNextTPARequestOutgoing")
            event.isCancelled = true
        }

        if (!selfKilledABoss) return
        if (System.currentTimeMillis() - selfKilledBossTimestamp > selfKilledBossDuration) {
            selfKilledABoss = false
            selfKilledBossTimestamp = 0L
        }

        if (resetIfSelfExpired()) return

        val message = ChatColor.stripColorCodes(event.message) ?: return
        val tpaRegex = Regex("(.+) has requested to teleport to you.+?")

        val match = tpaRegex.find(message) ?: return

        event.isCancelled = true
        cancelNextTPARequestIncoming = true
        println("Cancelling original incoming tpa request message")
        player?.sendCommand("tpyes", null)
        UChat.chat("§aAutomatically accepted teleport request from §b${match.groupValues[1]}§a...")
    }

    @Subscribe
    fun onRender(event: RenderScreenEvent) {
        if (resetIfExpired()) return


        val resolution = mc.window
        val screenWidth = resolution.scaledWidth
        val screenHeight = resolution.scaledHeight

        val renderX = screenWidth / 2
        val renderY = screenHeight / 2 - 100

        val text =
            "§a${teleportRequestTarget} §7has defeated §a§l${teleportBossName?.textString} §b§lPress §aY§7/§cN §b to §aaccept§7/§ccancel §b the request."

        DrawableHelper.fill(
            event.stack,
            renderX - mc.textRenderer.getWidth(text) / 2 - 20,
            renderY - 20,
            renderX + mc.textRenderer.getWidth(text) / 2 + 20,
            renderY + 20,
            0x80000000.toInt()
        )

        DrawableHelper.drawCenteredText(
            event.stack,
            mc.textRenderer,
            net.minecraft.text.Text.of(text),
            renderX,
            renderY,
            0xFFFFFF
        )
    }

    private fun resetIfSelfExpired(): Boolean {
        if (!selfKilledABoss) return true
        if (System.currentTimeMillis() > selfKilledBossDuration + selfKilledBossTimestamp) {
            selfKilledABoss = false
            selfKilledBossTimestamp = 0L
            return true
        }
        return false
    }
    private fun resetIfExpired(): Boolean {
        if (teleportRequestTarget == null || teleportRequestTimestamp == 0L) return true
        if (System.currentTimeMillis() > tpPopupDuration + teleportRequestTimestamp) {
            teleportRequestTarget = null
            teleportRequestTimestamp = 0L
            teleportBossName = null
            return true
        }
        return false
    }

    fun checkCancelNextMessage(event: ClientChatEvent.Received): Boolean {
        val message = ChatColor.stripColorCodes(event.message) ?: return false
        if(cancelNextTPARequestIncoming) {
           if(message == "To teleport, type /tpaccept.") return true
            if(message == "To deny this request, type /tpdeny.") return true
            if(message == "This request will timeout after 120 seconds.") return true
            if(message == "Teleport request accepted.") {
                cancelNextTPARequestIncoming = false
                return true
            }
        }

        if(cancelNextTPARequestOutgoing) {
           if(message.matches("Request sent to (.+)".toRegex())) {
               return true
           }
            if(message == "To cancel this request, type /tpacancel.") {
                cancelNextTPARequestOutgoing = false
                return true
            }

        }

        return false;
    }

}