package de.bybackfish.avalonaddons.avalon

import de.bybackfish.avalonaddons.extensions.raw
import de.bybackfish.avalonaddons.utils.hours
import de.bybackfish.avalonaddons.utils.minutes
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen

enum class Lootable(val containerName: String, val displayName: String, val cooldown: Long) {

    SKELETON_KING("Skeleton King Loot", "§6§LThe Skeleton King", hours(18)), //y
    WITHER_QUEEN("Wither Queen's Loot", "§d§lThe Wither Queen", hours(18)), //y
    VARSON("Lord Varson's Loot", "§c§lLord Varson", hours(18)),
    LORD_REVAN("Lord Revan's Loot", "§a§lLord Revan", hours(18)),
    MEDIVH("Medivh's Loot", "§5§lMedivh", hours(18)),
    CORRUPTED_KING_XERO("Corrupted King Xero", "§6§lCorrupted King Xero", hours(18)),
    ARKSHIFT("Arkshift Loot", "§4§lArkshift", hours(18)), //y

    MEGA_REVAN("Mega Lord Revan", "§a§lMega Lord Revan", hours(18)),
    ULTRA_VARSON("Ultra Varson", "§c§lUltra Varson", hours(18)),
    CELOSIA("❀ Celosia, Stolen Throne ❀", "§e❀ §5§lCe§d§llosia, §2St§aolen §2Th§arone §e❀", hours(18)),
    EVELYNN("Evelynn", "§4§lEve§5§ll§d§lynn", hours(18)),


    // Holo
    HOLO_ONE("Holodeck: Four", "§6§lHolodeck: §e§lFour", minutes(30))


    ;
    companion object {
        fun getFromContainer(screen: GenericContainerScreen): Lootable? {
            return values().firstOrNull {
                screen.title.string.equals(it.containerName)
            }
        }
    }

    // Bosses



}

fun test() {
    ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
        if (screen is GenericContainerScreen) {
            println("OPENED CHEST: " + screen.title.string)
        }
    }
}