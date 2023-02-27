package de.bybackfish.avalonaddons

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import net.minecraft.util.Rarity
import java.io.File

class AvalonConfig : Vigilant(
    File("./config/AvalonAddons/avalon.toml"),
    "AvalonAddons",
    sortingBehavior = CustomSortingBehavior(),
    propertyCollector = AvalonAddons.propertyCollector
) {

    @Property(
        type = PropertyType.CHECKBOX,
        name = "AvalonAddons v0.2.1b",
        description = "Coming soonTM\n\nMade by byBackfish#5701",
        category = "General"
    )
    val _avalon = true

    @Property(
        type = PropertyType.TEXT,
        name = "Beta Key",
        description = "Enter your beta key here to get access to beta features.",
        category = "General",
        protectedText = true
    )
    val betaKey = ""

    @Property(
        type = PropertyType.BUTTON,
        name = "Join official Avalon Discord",
        description = "Join the official Avalon Discord server to get support and stay up to date with the latest news.",
        category = "General",
        subcategory = "Discord",
        placeholder = "Join Discord"
    )
    fun joinDiscord() {

    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Join official AvalonAddons Discord",
        description = "Join the AvalonAddons Discord server to stay up to date with the latest updates",
        category = "General",
        subcategory = "Discord",
        placeholder = "Join Discord"
    )
    fun joinADiscord() {

    }


    // Boss Cooldowns for each of the Bosses of Bosses.kt
    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Skeleton King",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var skeletonKingLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Wither Queen",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var witherQueenLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Varson",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var varsonLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Lord Revan",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var lordRevanLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Medivh",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var medivhLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Corrupted King Xero",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var corruptedKingXeroLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Arkshift",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var arkshiftLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Mega Lord Revan",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var megaRevanLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Ultra Varsaon",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var ultraVarsonLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Celosia",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var celosiaLastKill = "0";

    @Property(
        type = PropertyType.TEXT,
        name = "Boss Cooldowns - Evelynn",
        description = "Shows the cooldown of the bosses in the boss menu.",
        category = "Boss Cooldowns",
        hidden = true
    )
    var evelynnLastKill = "0";


    companion object {

        val rarityOrder = Rarity.values()

        fun compare(o1: PropertyData, o2: PropertyData): Int {
            val o1Name = o1.attributesExt.name
            val o2Name = o2.attributesExt.name


            val sortingOrder = o1.attributesExt.maxF ?: 0f
            val sortingOrder2 = o2.attributesExt.maxF ?: 0f

            if (sortingOrder != 0f && sortingOrder2 != 0f) {
                return sortingOrder.compareTo(sortingOrder2)
            }

            return o1Name.compareTo(o2Name)
        }
    }

    class CustomSortingBehavior : SortingBehavior() {
        override fun getPropertyComparator(): Comparator<in PropertyData> {
            // if the property name is "Enabled", put it at the top
            return Comparator { o1, o2 ->
                if (o1.attributesExt.name == "Enabled") {
                    -1
                } else if (o2.attributesExt.name == "Enabled") {
                    1
                } else {
                    compare(o1, o2)
                }
            }
        }

        override fun getCategoryComparator(): Comparator<in Category> {
            // if category is general, put it at the top
            return Comparator { o1, o2 ->
                if (o1.name == "General") {
                    -1
                } else if (o2.name == "General") {
                    1
                } else {
                    o1.name.compareTo(o2.name)
                }
            }
        }

        override fun getSubcategoryComparator(): Comparator<in Map.Entry<String, List<PropertyData>>> {
            // if subcategory is general, put it at the top
            return Comparator { o1, o2 ->
                if (o1.key == "General") {
                    -1
                } else if (o2.key == "General") {
                    1
                } else {
                    o1.key.compareTo(o2.key)
                }
            }
        }

    }
}