package de.bybackfish.avalonaddons.extensions

import gg.essential.universal.ChatColor

val String.raw get() = ChatColor.stripColorCodes(this)