package de.bybackfish.avalonaddons

import com.mojang.brigadier.arguments.StringArgumentType
import de.bybackfish.avalonaddons.core.event.EventBus
import de.bybackfish.avalonaddons.core.feature.FeatureManager
import de.bybackfish.avalonaddons.core.loadTranslations
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.features.bosses.BetterBossTimer
import de.bybackfish.avalonaddons.features.chat.AutoAcceptTeleportRequest
import de.bybackfish.avalonaddons.features.chat.AutoChat
import de.bybackfish.avalonaddons.features.chat.AutoTeleportToDeadBoss
import de.bybackfish.avalonaddons.features.quests.QuestDisplay
import de.bybackfish.avalonaddons.features.quests.QuestOverlay
import de.bybackfish.avalonaddons.features.render.RarityBackgroundFeature
import de.bybackfish.avalonaddons.features.ui.GearViewer
import de.bybackfish.avalonaddons.features.utility.ArmorQuickSwap
import de.bybackfish.avalonaddons.listeners.AdvancedListeners
import de.bybackfish.avalonaddons.listeners.ChatListener
import de.bybackfish.avalonaddons.listeners.NativeListeners
import gg.essential.universal.UChat
import gg.essential.universal.UScreen
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.gui.screen.Screen
import net.minecraft.server.command.CommandManager


class AvalonAddons : ModInitializer {
    companion object {
        var NAMESPACE = "avalonaddons"

        lateinit var config: AvalonConfig
        lateinit var featureManager: FeatureManager;
        lateinit var bus: EventBus;

        var propertyCollector = JVMAnnotationPropertyCollector()

        public var guiToOpen: Screen? = null
    }

    override fun onInitialize() {
        config = AvalonConfig()

        bus = EventBus()
        featureManager = FeatureManager()

        NativeListeners().load(bus)
        bus.register(AdvancedListeners())
        bus.register(ChatListener())
        bus.register(this)

        loadTranslations()

        featureManager.register(
            BetterBossTimer(),

            AutoChat(),
            QuestOverlay(),
            RarityBackgroundFeature(),
            GearViewer(),
            QuestDisplay(),

            AutoTeleportToDeadBoss(),
            AutoAcceptTeleportRequest(),

            ArmorQuickSwap()
        )
        featureManager.loadToConfig()

        config.initialize()
        config.markDirty()

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                CommandManager.literal("avalonaddons").executes {
                    guiToOpen = config.gui()!!
                    0
                })

            // create a /debugmessage <msg> command, where msg can be as long as you want
            dispatcher.register(
                (
                        CommandManager.literal("debugmessage")
                            .then(CommandManager.argument(
                                "message",
                                StringArgumentType.greedyString()
                            )
                                .executes { context ->
                                    var message = StringArgumentType.getString(context, "message")
                                    println("Message: $message")
                                    message = message.replace("&&", "§")
                                    UChat.chat(message)
                                    ClientChatEvent.Received(message).call()
                                    0
                                })
                        )
            )
        }

        ClientTickEvents.START_CLIENT_TICK.register {
            if (guiToOpen != null) {
                UScreen.displayScreen(guiToOpen)
                guiToOpen = null
            }
        }
    }

}