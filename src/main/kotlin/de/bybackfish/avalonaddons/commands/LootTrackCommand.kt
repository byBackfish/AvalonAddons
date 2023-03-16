package de.bybackfish.avalonaddons.commands

import com.mojang.brigadier.arguments.StringArgumentType
import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.config.BossKillConfig
import de.bybackfish.avalonaddons.core.config.BossLootConfig
import gg.essential.universal.UChat
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

class LootTrackCommand {

    init {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("getloot").then(
                    argument("boss", StringArgumentType.word())
                    .executes {
                        val boss = StringArgumentType.getString(it, "boss")
                        val lootable = Lootable.get(boss.uppercase())
                        if(lootable == null) {
                            UChat.chat("§cBoss not found")
                            return@executes 1
                        }

                        UChat.chat("§b§lBoss Loot For §3${lootable.displayName}")
                        UChat.chat("§7- §6§lBoss Kills§r§7: §b§l${BossKillConfig.getKills(lootable)}")
                        UChat.chat("  §7- §a§lBoss Loot:")
                        val allLoot = BossLootConfig.getLoot(lootable)
                        val sortedLoot = allLoot.toList().sortedBy { (_, value) -> value }.toMap()
                        sortedLoot.forEach { (item, amount) ->
                            UChat.chat("    §7- §e§l${amount}§6x §r§6§l$item")
                        }
                        1
                    }
            ))
        }
    }

}