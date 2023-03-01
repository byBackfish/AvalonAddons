package de.bybackfish.avalonaddons.listeners

import de.bybackfish.avalonaddons.avalon.Bosses
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.events.BossDefeatedEvent
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.TeleportRequestEvent
import gg.essential.universal.ChatColor

class ChatListener {

    companion object {
        private val hideMessageRegex = mutableMapOf<Int, Regex>()
        private val hideMessageString = mutableMapOf<Int, String>()


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

        if (checkMessage(event.message)) event.isCancelled = true
    }

    fun checkBossDefeated(event: ClientChatEvent.Received) {
        var bossData = Bosses.parseFromMessage(event.message)
        if (bossData != null) {
            BossDefeatedEvent(bossData.boss, bossData.player).call()
        }
    }

    fun checkTeleportRequestIncoming(event: ClientChatEvent.Received) {
        val message = ChatColor.stripColorCodes(event.message) ?: return
        val tpaRegex = Regex("(.+) has requested to teleport to you.+?")

        val match = tpaRegex.find(message) ?: return
        val playerName = match.groupValues[1]
        TeleportRequestEvent.Incoming(playerName).call()
    }


}