package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.TeleportRequestEvent
import gg.essential.universal.ChatColor
import net.minecraft.client.MinecraftClient

class ChatListener {

    companion object {
        private val hideMessageRegex = mutableMapOf<Int, Regex>()
        private val hideMessageString = mutableMapOf<Int, String>()

        private val unignoredRegex = Regex("You are not ignoring player (\\w+) anymore.")
        private val ingoreRegex = Regex("You ignore player (\\w+) from now on.")
        private val joinRegex = Regex("(.+) joined the game")

        val ignoredPeople = listOf<String>()

        fun hide(message: String) {
            val randomId = (0..100000).random()
            hideMessageString[randomId] = message
        }

        fun hide(regex: Regex) {
            val randomId = (0..100000).random()
            hideMessageRegex[randomId] = regex
        }


        fun checkMessage(text: String): Boolean {
            val message = ChatColor.stripColorCodes(text) ?: return false
            for ((id, regex) in hideMessageRegex) {
                if (regex.matches(message)) {
                    hideMessageRegex.remove(id)
                    return true
                }
            }

            for ((id, string) in hideMessageString) {
                if (message == string) {
                    hideMessageString.remove(id)
                    return true
                }
            }

            return false
        }
    }

    @Subscribe
    fun onChat(event: ClientChatEvent.Received) {
        checkBossDefeated(event)
        checkTeleportRequestIncoming(event)

        val strippedMessage = ChatColor.stripColorCodes(event.message) ?: return
        if(unignoredRegex.matches(strippedMessage)) {
            val user = unignoredRegex.find(event.message)?.groups?.get(1)?.value ?: return
            if(ignoredPeople.contains(user)) {
                event.isCancelled = true
                MinecraftClient.getInstance().player!!.networkHandler.sendCommand("ignore $user")
                return
            }
        }

        if(ingoreRegex.matches(strippedMessage)) {
            val user = ingoreRegex.find(event.message)?.groups?.get(1)?.value ?: return
            if(ignoredPeople.contains(user)) {
                event.isCancelled = true
                return
            }
        }

        if(joinRegex.matches(strippedMessage)) {
            val user = joinRegex.find(event.message)?.groups?.get(1)?.value ?: return
            println("user joined: $user")
            val myUsername = MinecraftClient.getInstance().session!!.username

            if(!user.contentEquals(myUsername, true)) return
            for(person in ignoredPeople) {
                MinecraftClient.getInstance().player!!.networkHandler.sendCommand("ignore $person")
            }
        }

        if (checkMessage(event.message)) event.isCancelled = true
    }

    private fun checkBossDefeated(event: ClientChatEvent.Received) {
        var bossData = Bosses.parseFromMessage(event.message)
        if (bossData != null) {
            BossDefeatedEvent(bossData.boss.lootable, bossData.player).call()
        }
    }

    fun checkTeleportRequestIncoming(event: ClientChatEvent.Received) {
        val message = ChatColor.stripColorCodes(event.message) ?: return
        val tpaRegex = Regex("(.+) has requested to teleport to you.+?")

        val match = tpaRegex.find(message) ?: return
        val playerName = match.groups[1] ?: return
        // the player might have a rank, so the name is the last split of the name
        val actualName = playerName.value.split(" ").last()
        TeleportRequestEvent.Incoming(actualName).call()
    }


}