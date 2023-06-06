package de.bybackfish.avalonaddons

import de.bybackfish.avalonaddons.commands.AvalonAddonsCommand
import de.bybackfish.avalonaddons.commands.LootTrackCommand
import de.bybackfish.avalonaddons.core.config.*
import de.bybackfish.avalonaddons.core.event.EventBus
import de.bybackfish.avalonaddons.core.feature.FeatureManager
import de.bybackfish.avalonaddons.core.loadTranslations
import de.bybackfish.avalonaddons.features.bosses.BetterBossTimer
import de.bybackfish.avalonaddons.features.bosses.BossLootTracker
import de.bybackfish.avalonaddons.features.chat.AutoAcceptTeleportRequest
import de.bybackfish.avalonaddons.features.chat.AutoChat
import de.bybackfish.avalonaddons.features.chat.AutoTeleportToDeadBoss
import de.bybackfish.avalonaddons.features.dev.NPCExploit
import de.bybackfish.avalonaddons.features.friends.FriendsFeature
import de.bybackfish.avalonaddons.features.friends.IgnoredFeature
import de.bybackfish.avalonaddons.features.render.RarityBackgroundFeature
import de.bybackfish.avalonaddons.features.ui.BackpackPreview
import de.bybackfish.avalonaddons.features.ui.GearViewer
import de.bybackfish.avalonaddons.features.ui.ItemViewer
import de.bybackfish.avalonaddons.features.utility.AdvancedItemInfo
import de.bybackfish.avalonaddons.features.utility.ArmorQuickSwap
import de.bybackfish.avalonaddons.features.utility.ItemDropPrevention
import de.bybackfish.avalonaddons.features.utility.QuickSkills
import de.bybackfish.avalonaddons.listeners.AdvancedListeners
import de.bybackfish.avalonaddons.listeners.ChatListener
import de.bybackfish.avalonaddons.listeners.NativeListeners
import gg.essential.universal.UScreen
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen


class AvalonAddons : ModInitializer {

    companion object {
        var NAMESPACE = "avalonaddons"
        var PREFIX = "§7§l[§b§lAvalon§3§lAddons§7§l]§r §b> §f"


        lateinit var config: AvalonConfig
        lateinit var featureManager: FeatureManager;
        lateinit var bus: EventBus;

        var propertyCollector = JVMAnnotationPropertyCollector()

        public var guiToOpen: Screen? = null

        val mc: MinecraftClient
            get() = MinecraftClient.getInstance()

        lateinit var json: Json
    }

    override fun onInitialize() {
        config = AvalonConfig()
        PersistentSave.loadData()

        json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                include(serializersModule)
            }

        }

        bus = EventBus()
        featureManager = FeatureManager()

        NativeListeners().load(bus)
        bus.register(AdvancedListeners())
        bus.register(ChatListener())
        bus.register(featureManager)
        bus.register(this)

        loadTranslations()


        featureManager.register(
            BetterBossTimer(),

            AutoChat(),
            //   QuestOverlay(),
            RarityBackgroundFeature(),
            GearViewer(),
            //    QuestDisplay(),

            AutoTeleportToDeadBoss(),
            AutoAcceptTeleportRequest(),

            ArmorQuickSwap(),
            BackpackPreview(),
            FriendsFeature(),
            IgnoredFeature(),
            ItemDropPrevention(),
            BossLootTracker(),

            ItemViewer(),
            QuickSkills(),

            AdvancedItemInfo()
        )

        featureManager.register(NPCExploit())

        featureManager.loadToConfig()

        config.initialize()
        config.markDirty()

        tryInitializeItems()

        FriendsConfig
        LockedSlotsConfig
        BossKillConfig


        AvalonAddonsCommand()
        LootTrackCommand()

        ClientTickEvents.START_CLIENT_TICK.register {
            if (guiToOpen != null) {
                UScreen.displayScreen(guiToOpen)
                guiToOpen = null
            }
        }

    }

    private fun tryInitializeItems() {
        try {
            PersistentSave.addNoParse(ItemConfig::class)
            ItemConfig.loadFromAPI()
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                ItemConfig.load()
            } catch (e: Exception) {
                ItemConfig.data.clear()
                e.printStackTrace()
            }
        }
    }

}