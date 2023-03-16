package de.bybackfish.avalonaddons.avalon

import de.bybackfish.avalonaddons.extensions.camel
import de.bybackfish.avalonaddons.extensions.raw
import de.bybackfish.avalonaddons.features.bosses.BetterBossTimer
import de.bybackfish.avalonaddons.utils.hours
import de.bybackfish.avalonaddons.utils.minutes
import gg.essential.universal.ChatColor
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen

enum class Lootable(val containerName: List<String>, val displayName: String, val cooldown: Long) {

    SKELETON_KING(listOf("Skeleton King Loot"), "§6§LThe Skeleton King", hours(18)), //y
    WITHER_QUEEN(listOf("Wither Queen's Loot"), "§d§lThe Wither Queen", hours(18)), //y
    VARSON(listOf("Varson Loot"), "§c§lLord Varson", hours(18)),
    LORD_REVAN(listOf("Revan Loot", "Revan's Loot Chest"), "§a§lLord Revan", hours(18)), //y
    MEDIVH(listOf("Medivh Loot"), "§5§lMedivh", hours(18)),
    CORRUPTED_KING_XERO(listOf("King Xero's Chest"), "§6§lCorrupted King Xero", hours(18)),
    ARKSHIFT(listOf("Arkshift Loot"), "§4§lArkshift", hours(18)), //y

    MEGA_REVAN(listOf("MegaRevanLoot"), "§a§lMega Lord Revan", hours(18)), //y
    ULTRA_VARSON(listOf("Shroomsteel Chest", "Ultra Varson Loot"), "§c§lUltra Varson", hours(18)), // y
    CELOSIA(listOf("Floral Loot"), "§e❀ §5§lCe§d§llosia, §2St§aolen §2Th§arone §e❀", hours(18)), //y
    EVELYNN(listOf("Eve Loot 1", "Eve Loot 2", "Eve Loot 3"), "§4§lEve§5§ll§d§lynn", hours(18)),


    // Holo
    HOLO_ONE(listOf("Holodeck One"), "§6§lHolodeck: §e§lOne", minutes(30)),
    HOLO_TWO(listOf("Holodeck Two"), "§6§lHolodeck: §e§lTwo", minutes(30)),
    HOLO_THREE(listOf("Holodeck Three"), "§6§lHolodeck: §e§lThree", minutes(30)),
    HOLO_FOUR(listOf("Holodeck Four"), "§6§lHolodeck: §e§lFour", minutes(30)),


    // Dungeons
    ICE_DUNGEON(listOf("icecave geode"), "§b§lIce Dungeon", hours(3)),
    THUNDER_DUNGEON(listOf("Thunder Geode"), "§e§lThunder Dungeon", hours(3)),
    AIR_DUNGEON(listOf("Aerial Atrium Geode"), "§f§lAir Dungeon", hours(3)),
    FIRE_DUNGEON(listOf("Charred Chest"), "§c§lFire Dungeon", hours(3)),


    ;

    companion object {
        fun getFromContainer(screen: GenericContainerScreen): Lootable? {
            return values().firstOrNull {
                it.containerName.any { name ->
                    screen.title.string.trim() == name
                }
            }
        }

        fun get(name: String): Lootable? {
            return values().firstOrNull { it ->
                it.name == name.uppercase() || it.displayName == name || it.containerName.any { name == it }
            }
        }
    }


    fun getLastKill(betterBossTimer: BetterBossTimer): Long {
        return betterBossTimer.property<String>("${name.camel()}-lastKill")!!.toLong()
    }
    fun setLastKill(betterBossTimer: BetterBossTimer, time: Long) {
        betterBossTimer.property("${name.camel()}-lastKill", time.toString())
    }
    fun isOnCooldown(betterBossTimer: BetterBossTimer): Boolean {
        return getLastKill(betterBossTimer) + cooldown > System.currentTimeMillis()
    }
    fun getReadyTime(betterBossTimer: BetterBossTimer): Long {
        return getLastKill(betterBossTimer) + cooldown
    }

}