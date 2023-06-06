package de.bybackfish.avalonaddons

import VERSION
import gg.essential.universal.UChat
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Rarity
import java.awt.Color
import java.awt.Desktop
import java.io.File

class AvalonConfig : Vigilant(
    File("./config/AvalonAddons/avalon.toml"),
    "AvalonAddons",
    sortingBehavior = CustomSortingBehavior(),
    propertyCollector = AvalonAddons.propertyCollector
) {

    @Property(
        type = PropertyType.CHECKBOX,
        name = "AvalonAddons $VERSION",
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
        val link = "https://discord.gg/2Nyjb36xt6"
        browse(link)
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
        val link = "https://discord.gg/rdwBGTAKq7"
        browse(link)
    }

    private fun browse(link: String) {
        kotlin.runCatching {
            Desktop.getDesktop().browse(java.net.URI(link));
        }.onFailure {
            MinecraftClient.getInstance().setScreen(null)
            UChat.chat("§a§lInvite Link§r§7: §r$link")
        }
    }

    @Property(
        type = PropertyType.COLOR,
        name = "Item Search Hightlight Color (Light)",
        description = "What color should items that meet the search be highlighted with?",
        category = "General",
        subcategory = "Misc - Item Search",
    )
    var itemSearchHighlightColorLight = Color(255, 255, 255, 120)

    @Property(
        type = PropertyType.COLOR,
        name = "Item Search Hightlight Color (Dark)",
        description = "What color should items that §cdo not §7meet the search be highlighted with?",
        category = "General",
        subcategory = "Misc - Item Search",
    )
    var itemSearchHighlightColorDark = Color(0, 0, 0, 120)

    @Property(
        type = PropertyType.CHECKBOX,
        name = "Test Box 1",
        description = "Test Box 1",
        category = "General",
        subcategory = "Misc - Test Box",
    )
    var testBox1 = false

    @Property(
        type = PropertyType.CHECKBOX,
        name = "Test Box 2",
        description = "Test Box 2",
        category = "General",
        subcategory = "Misc - Test Box",
    )
    var testBox2 = false

    @Property(
        type = PropertyType.CHECKBOX,
        name = "Test Box 3",
        description = "Test Box 3",
        category = "General",
        subcategory = "Misc - Test Box",
    )
    var testBox3 = false


    companion object {

        val rarityOrder = Rarity.values()

        fun compare(o1: PropertyData, o2: PropertyData): Int {
            val o1Name = o1.attributesExt.name
            val o2Name = o2.attributesExt.name


            val sortingOrder = o1.attributesExt.maxF
            val sortingOrder2 = o2.attributesExt.maxF

            if(sortingOrder == 0f && sortingOrder2 == 0f) {
                return o1Name.compareTo(o2Name)
            }

            if(sortingOrder == 0f) {
                return 1
            }

            if(sortingOrder2 == 0f) {
                return -1
            }

            return sortingOrder.compareTo(sortingOrder2)
        }
    }

    class CustomSortingBehavior : SortingBehavior() {
        override fun getPropertyComparator(): Comparator<in PropertyData> {
            // if the property name is "Enabled", put it at the top
            return Comparator { o1, o2 ->
                if (o1.attributesExt.name == "Toggle") {
                    -1
                } else if (o2.attributesExt.name == "Toggle") {
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