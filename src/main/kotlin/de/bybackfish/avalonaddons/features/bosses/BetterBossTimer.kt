package de.bybackfish.avalonaddons.features.bosses

import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.annotations.Button
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.config.BossKillConfig
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.core.getKey
import de.bybackfish.avalonaddons.core.translations
import de.bybackfish.avalonaddons.events.*
import de.bybackfish.avalonaddons.extensions.camel
import de.bybackfish.avalonaddons.utils.formatRelativeFutureTime
import gg.essential.elementa.components.UIText
import gg.essential.universal.UChat
import gg.essential.vigilance.data.CallablePropertyValue
import gg.essential.vigilance.data.PropertyType
import java.util.*

@Category("Bosses")
class BetterBossTimer : Feature() {

    @Property(
        description = "Should the Boss Timers be rendered on screen?",
        forceType = PropertyType.SWITCH,
        sortingOrder = 1
    )
    var renderBossTimersOnScreen = false

    @Button(
        description = "Should all Boss Timer Renderers be enabled",
        sortingOrder = 2,
        buttonText = "Enable All"
    )
    fun enableAll() {
        Lootable.values().forEach {
            property("${it.name.camel()}-render", true)
            player!!.closeScreen()
        }
    }

    @Button(
        description = "Should all Boss Timer Renderers be disabled",
        sortingOrder = 3,
        buttonText = "Disable All"
    )
    fun disableAll() {
        Lootable.values().forEach {
            property("${it.name.camel()}-render", false)
            player!!.closeScreen()
        }
    }

    @Button(
        description = "Reset All Boss Timers (Cooldowns)",
        sortingOrder = 4,
        buttonText = "Reset All"
    )
    fun resetAll() {
        Lootable.values().forEach {
            it.setLastKill(this, 0)
            player!!.closeScreen()
        }
    }


    init {
        val total = Lootable.values().size
       Lootable.values().forEachIndexed { index, it ->
           val sortingOrder = index + 5

           val translatedName = getKey(this::class) + "." + it.name.camel()
           translations["${translatedName}-render"] = "Render ${it.name.camel()}'s Timer"
           translations["${translatedName}-resetTimer"] = "Reset ${it.name.camel()}'s Timer"

           addSetting(
               LocalProperty(
                   "${it.name.camel()}-render",
                   default = false,
                   type = PropertyType.SWITCH,
                   description = "Should the ${it.name.camel()} timer be rendered on screen?",
                   sortingOrder = sortingOrder,
               )
           )

           addSetting(
               LocalProperty(
                     "${it.name.camel()}-resetTimer",
                     default = it.name.camel(),
                     type = PropertyType.BUTTON,
                     description = "Reset the cooldown of ${it.name.camel()}",
                     sortingOrder = sortingOrder + total,
                     placeholder = "Reset",
                     value = LocalCallableProperty {
                            it.setLastKill(this, 0)
                            UChat.chat("§aSuccessfully reset ${it.name.camel()}'s timer!")
                     }
               )
           )

           addSetting(
               LocalProperty(
                   "${it.name.camel()}-lastKill",
                   default = "0",
                   type = PropertyType.TEXT,
                   hidden = true,
                   sortingOrder = sortingOrder + total * 2 + index,
               ))
       }
    }

    @Subscribe
    fun onLootableGUI(event: LootableChestEvent) {
        if(event.lootable.isOnCooldown(this)) return

        BossKillConfig.addKill(event.lootable)
        event.lootable.setLastKill(this, System.currentTimeMillis())
        UChat.chat("§aSuccessfully marked ${event.lootable.name.camel()} as looted! You can loot it again in ${event.lootable.cooldown} milliseconds.")
    }

    @Subscribe
    fun onRender(event: RenderScreenEvent) {
        if(!renderBossTimersOnScreen) return
        // set text scale

        val x = 20
        val y = 20

        val yOffset = 10

        Lootable.values().forEachIndexed { index, it ->
            val shouldRender = property<Boolean>("${it.name.camel()}-render") ?: false
            if(!shouldRender) return@forEachIndexed

            val status = if (!it.isOnCooldown(this)) "§aReady" else {
                val longerThanHour = it.getReadyTime(this) - System.currentTimeMillis() > 60 * 60 * 1000L
                "§${if (longerThanHour) "c" else "e"}${formatRelativeFutureTime(Date(it.getReadyTime(this)))}"
            }
            val text = "§l${it.displayName}§r§7: §l$status"

            drawStringWithShadow(event.stack, mc.textRenderer, text, x, (y + (index * yOffset)), -1)
        }
    }

}