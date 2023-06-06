package net.fabricmc.example

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType

class MyConfig: Config(Mod("avalonaddons", ModType.UTIL_QOL), "config.toml") {
    init {
        initialize()
    }

    @Switch(
        name = "Enable Example",
        description = "Enables the example feature",
    )
    var enableExample = false

    @Switch(
        name = "Enable Example 2",
        description = "Enables the example feature 2",
    )
    var enableExample2 = false

}