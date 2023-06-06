package de.bybackfish.avalonaddons.features.utility

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Keybind
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.annotations.RegisterKeybinds
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.ActionBarMessageEvent
import de.bybackfish.avalonaddons.events.ClientTickEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import de.bybackfish.avalonaddons.utils.Quadret
import gg.essential.universal.UChat
import gg.essential.universal.UResolution
import gg.essential.vigilance.data.PropertyType
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

@RegisterKeybinds
@Category("Utility")
class QuickSkills : Feature() {

    val skillBarCancelledRegex = Regex("You cancelled skill casting.", RegexOption.MULTILINE)

    val skillBarActivatedRegex =
        Regex("\\[(\\d)\\] (\\w+(?:\\s\\w+)?) *(?:\\((\\d+)\\))?", RegexOption.MULTILINE)


    val a = arrayListOf<Quadret<Int, String, Long, Int>>()
    var skills = Collections.synchronizedList(a)

    var calculatedSkills =
        Collections.synchronizedList(arrayListOf<Quadret<Int, String, Long, Int>>())

    var active = false

    val skillQueue = LinkedList<Int>()
    var using = AtomicBoolean(false)

    @Property(
        forceType = PropertyType.SLIDER,
        description = "Delay between actions",
        min = 5,
        max = 250,
    )
    var delay = 50

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Only execute skill if off cooldown"
    )
    var onlyIfNoCooldown = true

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Re-Toggle the skill bar to update the cooldowns after using a skill hotkey"
    )
    var retoggleBar = false

    val enabledFutures = mutableListOf<CompletableFuture<Boolean>>()
    val disabledFutures = mutableListOf<CompletableFuture<Boolean>>()


    @Keybind(-1)
    fun use1FirstSkill() {
        queueSkill(1)
    }

    @Keybind(-1)
    fun use2SecondSkill() {
        queueSkill(2)
    }

    @Keybind(-1)
    fun use3ThirdSkill() {
        queueSkill(3)
    }

    @Keybind(-1)
    fun use4FourthSkill() {
        queueSkill(4)
    }

    @Keybind(-1)
    fun use5FifthSkill() {
        queueSkill(5)
    }

    @Keybind(-1)
    fun use6SixthSkill() {
        queueSkill(6)
    }

    @Keybind(-1)
    fun use7SeventhSkill() {
        queueSkill(7)
    }

    @Keybind(-1)
    fun use8EighthSkill() {
        queueSkill(8)
    }

    @Keybind(-1)
    fun use9NinthSkill() {
        queueSkill(9)
    }

    fun queueSkill(skillSlot: Int) {
        skillQueue.add(skillSlot)
    }


    fun executeQueue() {
        if (skillQueue.isEmpty()) {
            return
        }

        if (using.get()) {
            return
        }


        Thread {
            val wasActive = active
            try {
                val skillSlot = skillQueue.poll()
                if (skillSlot - 1 > calculatedSkills.size) {
                    UChat.chat("Invalid skill slot. Please use a number between 1 and ${calculatedSkills.size}")
                    return@Thread
                }
                val skill = calculatedSkills[skillSlot - 1]!!

                val estimatedCooldownRemaining =
                    skill.d - ((System.currentTimeMillis() - skill.c) / 1000)
                if (onlyIfNoCooldown && estimatedCooldownRemaining > 0) {
                    UChat.chat("Skill ${skill.b} is on cooldown. Please wait ${estimatedCooldownRemaining}s")
                    return@Thread
                }

                UChat.chat("Executing skill ${skill.b} in slot $skillSlot")

                using.set(true)

                if (active) {
                    mc.execute {
                        pressSlotKey(skill.a - 1)
                    }
                } else {
                    mc.execute {
                        toggleBar()
                    }
                    waitForBar().get()
                    Thread.sleep(delay.toLong())

                    mc.execute {
                        pressSlotKey(skill.a - 1)
                    }
                }

                Thread.sleep(delay.toLong())

                if (wasActive) {
                    using.set(false)
                    return@Thread
                }
                // only toggle the bar if the queue is empty, otherwise the bar will be toggled by the next skill
                if (skillQueue.isEmpty()) {
                    mc.execute {
                        toggleBar()
                    }

                    if (retoggleBar) {
                        Thread.sleep(delay.toLong())
                        mc.execute {
                            toggleBar()
                        }
                        Thread.sleep(delay.toLong())
                        mc.execute {
                            toggleBar()
                        }
                    }
                }
                using.set(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun waitForBar(value: Boolean = true): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        if (value) {
            enabledFutures.add(future)
        } else {
            disabledFutures.add(future)
        }

        return future
    }

    fun pressSlotKey(slot: Int) {
        mc.player!!.inventory.selectedSlot = slot
    }

    private fun toggleBar() {
        mc.networkHandler?.sendPacket(
            PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                BlockPos.ORIGIN,
                Direction.DOWN
            )
        )
    }


    var lastSlot = -1

    @Subscribe
    fun onTick(event: ClientTickEvent) {
        if (mc.currentScreen == null) {
            executeQueue()
        }


        if (!active) {
            if (lastSlot != mc.player?.inventory?.selectedSlot) {
                lastSlot = mc.player?.inventory?.selectedSlot ?: -1
            }

            recalculateSlots(lastSlot)
        }

    }

    fun recalculateSlots(currentSlot: Int) {
        val newSkills = Collections.synchronizedList(arrayListOf<Quadret<Int, String, Long, Int>>())
        var slot = currentSlot + 1
        for (skill in skills) {
            val copy = Quadret(skill.a, skill.b, skill.c, skill.d)
            if (copy.a >= slot) {
                copy.a++
            }

            newSkills.add(copy)
        }
        calculatedSkills = newSkills
    }

    @Subscribe
    fun onActionBar(event: ActionBarMessageEvent) {
        val matches = skillBarActivatedRegex.findAll(event.packet.content.string)
        if (matches.count() != 0) {
            // wait 10ms without blocking the main thread
            for (match in matches) {
                val skillSlot = match.groupValues[1].toInt()

                val cooldown = match.groupValues[3].toIntOrNull() ?: 0


                // remove the skill from the list if it's already there
                skills.removeIf { it.a == skillSlot || it.b == match.groupValues[2] }

                val slot = if (lastSlot + 1 <= skillSlot) skillSlot - 1 else skillSlot
                skills.add(
                    Quadret(
                        slot,
                        match.groupValues[2].trim(),
                        System.currentTimeMillis(),
                        cooldown
                    )
                )

                recalculateSlots(lastSlot)
                active = true
                enabledFutures.forEach {
                    it.complete(true)
                }
                enabledFutures.clear()
            }

        }

        if (skillBarCancelledRegex.matches(event.packet.content.string)) {
            active = false
            disabledFutures.forEach {
                it.complete(true)
            }
            disabledFutures.clear()
        }
    }

    @Subscribe
    fun onScreenRender(event: RenderScreenEvent) {
        val centerY = UResolution.scaledHeight / 2
        val yOffset = 10

        // the startY should be the centerY - the yOffset * the amount of skills
        val startY = centerY - yOffset * (skills.size / 2 + 1)

        val startX = 10

        // render if you are in the skill menu or not
        val text = if (active) "§a§lActive" else "§c§lInactive"
        mc.textRenderer.drawWithShadow(
            event.stack,
            "§eSkill Bar: $text",
            startX.toFloat(),
            startY.toFloat() - yOffset - 2,
            0xFFFFFF
        )

        // for with index, slot number and skill name
        for ((index, entry) in calculatedSkills.iterator().withIndex()) {
            val y = startY + index * yOffset

            // estimate the cooldown remaining. entry.d is the cooldown in seconds, entry.c is the time the skill was activated in millis
            val estimatedCooldownRemaining =
                entry.d - ((System.currentTimeMillis() - entry.c) / 1000)

            var skillSlot = entry.a

            if (estimatedCooldownRemaining > 0) {
                mc.textRenderer.drawWithShadow(
                    event.stack,
                    "§e[${skillSlot}] §c${entry.b} (${estimatedCooldownRemaining})",
                    startX.toFloat(),
                    y.toFloat(),
                    0xFFFFFF
                )
            } else {
                mc.textRenderer.drawWithShadow(
                    event.stack,
                    "§e[${skillSlot}] §a${entry.b}",
                    startX.toFloat(),
                    y.toFloat(),
                    0xFFFFFF
                )
            }
        }

    }

}