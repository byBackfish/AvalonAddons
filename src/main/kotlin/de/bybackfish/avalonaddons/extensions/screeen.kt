package de.bybackfish.avalonaddons.extensions

import de.bybackfish.avalonaddons.mixins.accessors.AccessorScreen
import net.minecraft.client.gui.screen.Screen

/*
val Screen.screenAccessor: AccessorScreen
    get() = this as AccessorScreen */

val Screen.accessor: AccessorScreen
    get() = this as AccessorScreen