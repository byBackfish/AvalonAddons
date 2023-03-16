package de.bybackfish.avalonaddons.extensions

import java.io.File

fun File.ensureFile() = (parentFile.exists() || parentFile.mkdirs()) && createNewFile()