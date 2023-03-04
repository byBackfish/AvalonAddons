package de.bybackfish.avalonaddons.avalon

import de.bybackfish.avalonaddons.AvalonAddons
import gg.essential.universal.ChatColor

enum class Bosses(val textString: String, val lootable: Lootable) {
    SKELETON_KING("The Skeleton King", Lootable.SKELETON_KING),
    WITHER_QUEEN("The Wither Queen", Lootable.WITHER_QUEEN),
    VARSON("Lord Varson", Lootable.VARSON),
    LORD_REVAN("Lord Revan", Lootable.LORD_REVAN),
    MEDIVH("Medivh", Lootable.MEDIVH),
    CORRUPTED_KING_XERO("Corrupted King Xero", Lootable.CORRUPTED_KING_XERO),
    ARKSHIFT("Arkshift", Lootable.ARKSHIFT),

    MEGA_REVAN("Mega Lord Revan", Lootable.MEGA_REVAN),
    ULTRA_VARSON("Ultra Varson", Lootable.VARSON),
    CELOSIA("❀ Celosia, Stolen Throne ❀", Lootable.CELOSIA),
    EVELYNN("Evelynn", Lootable.EVELYNN);


    companion object {
        fun parseFromMessage(message: String): BossResult? {
            val regex = Regex("(.+) [hH]as been defeated by (.+)!.?")

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

}