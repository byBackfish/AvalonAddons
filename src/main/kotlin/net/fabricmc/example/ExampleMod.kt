package net.fabricmc.example

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


class ExampleMod : ModInitializer {


    companion object {
        lateinit var config: MyConfig
    }
    override fun onInitialize() {
        config = MyConfig()

        // Register the config command
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, registryAccess: CommandRegistryAccess?, environment: RegistrationEnvironment? ->
            dispatcher.register(literal("avalonaddons")
                .executes { context ->
                    context.source.sendMessage(Text.literal("Opened Avalon Addons config!"))
                    1
                })
        })


    }





}