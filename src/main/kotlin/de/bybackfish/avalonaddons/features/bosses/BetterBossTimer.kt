package de.bybackfish.avalonaddons.features.bosses

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.core.annotations.Button
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import de.bybackfish.avalonaddons.utils.formatRelativeFutureTime
import gg.essential.universal.ChatColor
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.util.math.MatrixStack
import java.util.*

@Category("Bosses")
class BetterBossTimer : Feature() {

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Render Boss Timers on screen",
        sortingOrder = 1
    )
    var renderBossTimersOnScreen = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Skeleton King be rendered on screen?",
        sortingOrder = 2
    )
    var renderSkeletonKing = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Wither Queen be rendered on screen?",
        sortingOrder = 3
    )
    var renderWitherQueen = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Varson be rendered on screen?",
        sortingOrder = 4
    )
    var renderVarson = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Lord Revan be rendered on screen?",
        sortingOrder = 5
    )
    var renderLordRevan = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Medivh be rendered on screen?",
        sortingOrder = 6
    )
    var renderMedivh = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Corrupted King Xero be rendered on screen?",
        sortingOrder = 7
    )
    var renderCorruptedKingXero = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Arkshift be rendered on screen?",
        sortingOrder = 8
    )
    var renderArkshift = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Mega Lord Revan be rendered on screen?",
        sortingOrder = 9
    )
    var renderMegaRevan = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Ultra Varson be rendered on screen?",
        sortingOrder = 10
    )
    var renderUltraVarson = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Celosia be rendered on screen?",
        sortingOrder = 11
    )
    var renderCelosia = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the cooldown of Evelynn be rendered on screen?",
        sortingOrder = 12
    )
    var renderEvelynn = false

    @Button(
        description = "Reset all Boss Timers",
        sortingOrder = 13,
        buttonText = "Reset Cooldowns"
    )
    fun resetTimers() {
        Bosses.values().forEach { it.setLastKill(0) }
        UChat.chat("§aSuccessfully reset all Boss Timers!")
    }

    @Button(
        description = "Enables all Boss Timers",
        sortingOrder = 14,
        buttonText = "Enable All"
    )
    fun enableAll() {
        toggleAll(true)
        UChat.chat("§aSuccessfully enabled all Boss Timers!")
    }

    @Button(
        description = "Disables all Boss Timers",
        sortingOrder = 15,
        buttonText = "Disable All"
    )
    fun disableAll() {
        toggleAll(false)
        UChat.chat("§aSuccessfully disabled all Boss Timers!")
    }

    private fun toggleAll(value: Boolean) {
        renderSkeletonKing = value
        renderWitherQueen = value
        renderVarson = value
        renderLordRevan = value
        renderMedivh = value
        renderCorruptedKingXero = value
        renderArkshift = value
        renderMegaRevan = value
        renderUltraVarson = value
        renderCelosia = value
        renderEvelynn = value
        AvalonAddons.config.markDirty()
    }


    var lastBoss: Bosses? = null
    var lastBossTimestamp: Long = 0

    val bossTeleportTimeoutDuration = 30 * 1000L
    val bossLootTimeoutDuration = 30 * 1000L

    var stage = 0;

    @Subscribe
    fun onBossDeath(event: BossDefeatedEvent) {
        lastBoss = event.boss

        lastBossTimestamp = System.currentTimeMillis()
        stage = 0;
        UChat.chat("Boss CD step 0")
    }


    private val steps = arrayOf(
        Pair(
            Regex("\\[Avalon] Teleporting you in 5 seconds\\.\\.\\.", RegexOption.MULTILINE),
            bossTeleportTimeoutDuration
        ),
        //     Pair(Regex("You gained (\\d+) class exp!", RegexOption.MULTILINE), bossLootTimeoutDuration)
    )

    @Subscribe
    fun onChat(event: ClientChatEvent.Received) {
        var message = ChatColor.stripColorCodes(event.message)!!
        message = ChatColor.stripControlCodes(message)!!

        if (lastBoss == null || stage == -1) return

        val currentStep = steps[stage]

        if (currentStep.first.matches(message) && !timeExpired(
                lastBossTimestamp,
                currentStep.second
            )
        ) {
            lastBossTimestamp = System.currentTimeMillis()
            stage++
            println("Boss CD step $stage")
        }

        if (stage == steps.size) {
            UChat.chat("§aYou have successfully looted and defeated §r§l${lastBoss?.displayName}!")
            stage = -1
            lastBoss!!.setLastKill(System.currentTimeMillis());

            lastBoss = null;

            return
        }
    }

    @Subscribe
    fun onRender(event: RenderScreenEvent) {
        if (renderBossTimersOnScreen) {
            renderAllTimers(event.stack)
        }
    }

    private fun renderAllTimers(matrices: MatrixStack) {
        val bossesToRender = getBossesToRender()

        val x = 20
        val y = 20

        val yOffset = 10

        bossesToRender.forEachIndexed { index, boss ->
            val lastKill = boss.getLastKillTime()

            val status = if (boss.isCooldownOver()) "§aReady" else {
                val longerThanHour = boss.getReadyTime() - lastKill > 60 * 60 * 1000L
                "§${if (longerThanHour) "c" else "e"}${formatRelativeFutureTime(Date(boss.getReadyTime()))}"
            }
            val text = "§l${boss.displayName}§r§7: §l$status"

            drawStringWithShadow(matrices, mc.textRenderer, text, x, (y + (index * yOffset)), -1)
        }
    }

    private fun getBossesToRender(): List<Bosses> {
        val list = mutableListOf<Bosses>()

        if (renderSkeletonKing) list.add(Bosses.SKELETON_KING)
        if (renderWitherQueen) list.add(Bosses.WITHER_QUEEN)
        if (renderVarson) list.add(Bosses.VARSON)
        if (renderLordRevan) list.add(Bosses.LORD_REVAN)
        if (renderMedivh) list.add(Bosses.MEDIVH)
        if (renderCorruptedKingXero) list.add(Bosses.CORRUPTED_KING_XERO)
        if (renderArkshift) list.add(Bosses.ARKSHIFT)
        if (renderMegaRevan) list.add(Bosses.MEGA_REVAN)
        if (renderUltraVarson) list.add(Bosses.ULTRA_VARSON)
        if (renderCelosia) list.add(Bosses.CELOSIA)
        if (renderEvelynn) list.add(Bosses.EVELYNN)

        return list
    }

}