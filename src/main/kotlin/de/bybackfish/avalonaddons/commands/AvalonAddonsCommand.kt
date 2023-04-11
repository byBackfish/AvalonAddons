package de.bybackfish.avalonaddons.commands

import com.mojang.brigadier.arguments.StringArgumentType
import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.config.FriendStatus
import de.bybackfish.avalonaddons.core.config.FriendsConfig
import de.bybackfish.avalonaddons.core.config.ItemConfig
import de.bybackfish.avalonaddons.events.ClientChatEvent
import de.bybackfish.avalonaddons.features.bosses.BetterBossTimer
import de.bybackfish.avalonaddons.features.ui.ItemViewer
import gg.essential.universal.UChat
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

class AvalonAddonsCommand {

    init {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("avalonaddons")
                    .executes { _ ->
                        AvalonAddons.guiToOpen = AvalonAddons.config.gui()
                        1
                    }
                    .then(literal("reloadcache").executes { ctx ->
                        UChat.chat(AvalonAddons.PREFIX + "§7Reloading cache...")
                        Thread {
                            ItemConfig.loadFromAPI()
                            AvalonAddons.featureManager.getFeature<ItemViewer>()?.postInit()
                            UChat.chat(AvalonAddons.PREFIX + "§aSuccessfully reloaded the cache!")
                        }.start()
                        1
                    })
                    .then(
                        literal("dm").then(
                            argument(
                                "message",
                                StringArgumentType.greedyString()
                            ).executes { ctx ->
                                val message = StringArgumentType.getString(ctx, "message")
                                AvalonAddons.bus.post(ClientChatEvent(message.replace("&&", "§")))
                                1
                            })
                    ).then(literal("test").executes { ctx ->

                        1
                    })


                    /* FRIENDS */

                    .then(
                        literal("friend").then(
                            literal("add").then(
                                argument(
                                    "username",
                                    StringArgumentType.word()
                                ).executes { ctx ->
                                    val username = StringArgumentType.getString(ctx, "username")
                                    if(FriendsConfig.friend(username)) {
                                        UChat.chat("Added $username to your friends list")
                                    } else {
                                        UChat.chat("You can't add yourself to your friends list")
                                    }
                                    1
                                })
                        ).then(
                            literal("remove").then(
                                argument(
                                    "username",
                                    StringArgumentType.word()
                                ).executes { ctx ->
                                    val username = StringArgumentType.getString(ctx, "username")
                                    if (FriendsConfig.unfriend(username))
                                        UChat.chat("Removed $username from your friends list")
                                    else
                                        UChat.chat("You can't remove yourself from your friends list")
                                    1
                                })
                        ).then(literal("list").executes {
                            val friends =
                                FriendsConfig.get().get(FriendStatus.FRIEND) ?: mutableListOf()
                            if (friends.isEmpty()) {
                                UChat.chat("You don't have any friends.")
                            } else {
                                UChat.chat("§aFriends:")
                                friends.forEach { UChat.chat("§a- ${it}") }
                            }
                            1
                        })
                    ).then(
                        literal("ignore")
                            .then(
                                literal("add").then(
                                    argument(
                                        "username",
                                        StringArgumentType.word()
                                    ).executes { ctx ->
                                        val username = StringArgumentType.getString(ctx, "username")
                                        if(FriendsConfig.ignore(username))
                                            UChat.chat("Added $username to your ignore list")
                                         else
                                            UChat.chat("You can't ignore a friend or someone who is already ignored.")

                                        1
                                    })
                            ).then(
                                literal("remove").then(
                                    argument(
                                        "username",
                                        StringArgumentType.word()
                                    ).executes { ctx ->
                                        val username = StringArgumentType.getString(ctx, "username")
                                        if(FriendsConfig.unignore(username))
                                            UChat.chat("Removed $username from your ignore list")
                                        else
                                            UChat.chat("You can't unignore someone who isn't ignored.")
                                        1
                                    })
                            ).then(literal("list").executes { ctx ->
                                val ignored =
                                    FriendsConfig.get().get(FriendStatus.IGNORED) ?: listOf()
                                if (ignored.isEmpty()) {
                                    UChat.chat("You don't have any ignored players.")
                                } else {
                                    UChat.chat("§4Ignored players:")
                                    ignored.forEach { UChat.chat("§c- $it") }
                                }
                                1
                            })
                    )
            )
        }
    }


}