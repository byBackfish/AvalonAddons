package net.fabricmc.example

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

class ConfigCommand: Command<CommandSource> {

    override fun run(context: CommandContext<CommandSource>?): Int {
        ExampleMod.config.openGui()
        return 1
    }
}