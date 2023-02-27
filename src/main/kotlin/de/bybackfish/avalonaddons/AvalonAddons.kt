package de.bybackfish.avalonaddons

import de.bybackfish.avalonaddons.core.event.EventBus
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.FeatureManager
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.events.PacketEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import de.bybackfish.avalonaddons.features.bosses.BetterBossTimer
import de.bybackfish.avalonaddons.features.bosses.TPAFeature
import de.bybackfish.avalonaddons.features.chat.AutoChat
import de.bybackfish.avalonaddons.features.quests.QuestDisplay
import de.bybackfish.avalonaddons.features.quests.QuestOverlay
import de.bybackfish.avalonaddons.features.render.RarityBackgroundFeature
import de.bybackfish.avalonaddons.features.ui.GearViewer
import de.bybackfish.avalonaddons.listeners.AdvancedListeners
import de.bybackfish.avalonaddons.listeners.NativeListeners
import gg.essential.universal.UScreen
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
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
        println("Hello Fabric world!")


        bus = EventBus()
        featureManager = FeatureManager()

        NativeListeners().load(bus)
        bus.register(AdvancedListeners())
        bus.register(this)

        // chat event

        featureManager.register(
            BetterBossTimer(),
            TPAFeature(),
            AutoChat(),
            QuestOverlay(),
            RarityBackgroundFeature(),
            GearViewer(),
            QuestDisplay()
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
        }
        ClientTickEvents.START_CLIENT_TICK.register {
            if (guiToOpen != null) {
                UScreen.displayScreen(guiToOpen)
                guiToOpen = null
            }
        }
    }

    @Subscribe
    fun onRenderScreen(event: RenderScreenEvent) {
        DrawableHelper.drawStringWithShadow(
            event.stack,
            MinecraftClient.getInstance().textRenderer,
            "Hello World!",
            25,
            25,
            0xffffff
        )

        // draw a box around it
        val width = MinecraftClient.getInstance().textRenderer.getWidth("Hello World!")
        val height = MinecraftClient.getInstance().textRenderer.fontHeight
    }

    @Subscribe
    fun onPacket(event: PacketEvent.Outgoing) {
        //   println("Outgoing: ${event.packet}")
    }

    @Subscribe
    fun onPacket(event: PacketEvent.Incoming) {
        //  println("Incoming: ${event.packet}")
    }

    @Subscribe
    fun onChat(event: ClientChatEvent.Received) {
        //  println("Received Message: ${event.message}")
    }

}