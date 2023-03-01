package de.bybackfish.avalonaddons.avalon

import de.bybackfish.avalonaddons.AvalonAddons
import gg.essential.universal.ChatColor

enum class Bosses(val textString: String, val displayName: String? = textString) {
    SKELETON_KING("The Skeleton King", "§6The Skeleton King"),
    WITHER_QUEEN("The Wither Queen", "§dThe Wither Queen"),
    VARSON("Lord Varson", "§cLord Varson"),
    LORD_REVAN("Lord Revan", "§aLord Revan"),
    MEDIVH("Medivh", "§5Medivh"),
    CORRUPTED_KING_XERO("Corrupted King Xero", "§6Corrupted King Xero"),
    ARKSHIFT("Arkshift", "§4Arkshift"),

    MEGA_REVAN("Mega Lord Revan", "§aMega Lord Revan"),
    ULTRA_VARSON("Ultra Varson", "§cUltra Varson"),
    CELOSIA("❀ Celosia, Stolen Throne ❀", "§e❀ §5§lCe§d§llosia, §2St§aolen §2Th§arone §e❀"),
    EVELYNN("Evelynn");


    companion object {
        const val COOLDOWN = 18 * 60 * 60 * 1000
        fun parseFromMessage(message: String): BossResult? {
            val regex = Regex("(.+) has been defeated by (.+)!.?")

            val match = regex.find(ChatColor.stripColorCodes(message)!!) ?: return null
            val bossName = match.groups[1]?.value ?: return null
            val playerName = match.groups[2]?.value ?: return null

            println("Boss: $bossName was killed by $playerName")

            val boss =
                values().find { it.textString.uppercase() == bossName.uppercase() } ?: return null

            return BossResult(boss, playerName)
        }

        data class BossResult(val boss: Bosses, val player: String)

    }

    fun setLastKill(time: Long) {
        when (this) {
            SKELETON_KING -> AvalonAddons.config.skeletonKingLastKill = time.toString()
            WITHER_QUEEN -> AvalonAddons.config.witherQueenLastKill = time.toString()
            VARSON -> AvalonAddons.config.varsonLastKill = time.toString()
            LORD_REVAN -> AvalonAddons.config.lordRevanLastKill = time.toString()
            MEDIVH -> AvalonAddons.config.medivhLastKill = time.toString()
            CORRUPTED_KING_XERO -> AvalonAddons.config.corruptedKingXeroLastKill = time.toString()
            ARKSHIFT -> AvalonAddons.config.arkshiftLastKill = time.toString()
            MEGA_REVAN -> AvalonAddons.config.megaRevanLastKill = time.toString()
            ULTRA_VARSON -> AvalonAddons.config.ultraVarsonLastKill = time.toString()
            CELOSIA -> AvalonAddons.config.celosiaLastKill = time.toString()
            EVELYNN -> AvalonAddons.config.evelynnLastKill = time.toString()
        }

        AvalonAddons.config.markDirty()
    }

    fun getLastKillTime(): Long {
        return when (this) {
            SKELETON_KING -> AvalonAddons.config.skeletonKingLastKill.toLong()
            WITHER_QUEEN -> AvalonAddons.config.witherQueenLastKill.toLong()
            VARSON -> AvalonAddons.config.varsonLastKill.toLong()
            LORD_REVAN -> AvalonAddons.config.lordRevanLastKill.toLong()
            MEDIVH -> AvalonAddons.config.medivhLastKill.toLong()
            CORRUPTED_KING_XERO -> AvalonAddons.config.corruptedKingXeroLastKill.toLong()
            ARKSHIFT -> AvalonAddons.config.arkshiftLastKill.toLong()
            MEGA_REVAN -> AvalonAddons.config.megaRevanLastKill.toLong()
            ULTRA_VARSON -> AvalonAddons.config.ultraVarsonLastKill.toLong()
            CELOSIA -> AvalonAddons.config.celosiaLastKill.toLong()
            EVELYNN -> AvalonAddons.config.evelynnLastKill.toLong()
        }
    }

    fun isCooldownOver(): Boolean {
        return getLastKillTime() + COOLDOWN < System.currentTimeMillis()
    }

    fun getReadyTime(): Long {
        return getLastKillTime() + COOLDOWN
    }

}