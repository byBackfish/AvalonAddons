package de.bybackfish.avalonaddons.features.chat

import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Keybind
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.annotations.RegisterKeybinds
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.InputUtil.GLFW_KEY_N
import net.minecraft.client.util.InputUtil.GLFW_KEY_Y

@Category("Chat")
@RegisterKeybinds
class AutoTeleportToDeadBoss : Feature() {

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Only teleport if there is no cooldown on the boss",
        sortingOrder = 1
    )
    var onlyIfNoCooldown = true

    @Property(
        forceType = PropertyType.SLIDER,
        description = "How long should the teleport prompt be shown",
        min = 1000,
        max = 30000,
        sortingOrder = 2
    )
    var teleportPromptDuration = 10000

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Automatically teleport to the dead boss, without a confirmation",
        sortingOrder = 3
    )
    var teleportWithoutConfirmation = false

    var lastDefeatedBossKillerName: String? = null
    var lastDefeatedBoss: Bosses? = null
    var lastDefeatedBossTime = 0L


    @Keybind(
        defaultKey = GLFW_KEY_Y
    )
    fun sendRequest() {
        if (hasExpired()) return

        UChat.chat("§aTeleporting to §b${lastDefeatedBossKillerName}§a...")
        mc.player?.sendCommand("tpa $lastDefeatedBossKillerName")

        lastDefeatedBoss = null
        lastDefeatedBossTime = 0L
        lastDefeatedBossKillerName = null
    }

    @Keybind(
        defaultKey = GLFW_KEY_N
    )
    fun cancelRequest() {
        if (hasExpired()) return
        UChat.chat("§cCancelled teleport request to §b${lastDefeatedBossKillerName}§c.")

        lastDefeatedBoss = null
        lastDefeatedBossTime = 0L
        lastDefeatedBossKillerName = null
    }

    @Subscribe
    fun onBossDefeated(event: BossDefeatedEvent) {
        if (event.killer == mc.player?.name?.string) return
        if (!event.boss.isCooldownOver() && onlyIfNoCooldown) return

        lastDefeatedBoss = event.boss
        lastDefeatedBossKillerName = event.killer
        lastDefeatedBossTime = System.currentTimeMillis()


        UChat.chat("§a${lastDefeatedBossKillerName} §7is near a dead boss. §b§lPress §aY§7/§cN §b to §aaccept§7/§ccancel §b the request.")

        if (teleportWithoutConfirmation) {
            sendRequest()
            return
        }
    }

    @Subscribe
    fun onRender(event: RenderScreenEvent) {
        if (hasExpired()) return


        val resolution = mc.window
        val screenWidth = resolution.scaledWidth
        val screenHeight = resolution.scaledHeight

        val renderX = screenWidth / 2
        val renderY = screenHeight / 2 - 100

        val text =
            "§a${lastDefeatedBossKillerName} §7has defeated §a§l${lastDefeatedBoss?.textString} §b§lPress §aY§7/§cN §b to §aaccept§7/§ccancel §b the request."

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


    private fun hasExpired(): Boolean {
        if (lastDefeatedBoss == null) return true
        if (lastDefeatedBossTime + teleportPromptDuration < System.currentTimeMillis()) {
            return true
        }

        return false
    }

}