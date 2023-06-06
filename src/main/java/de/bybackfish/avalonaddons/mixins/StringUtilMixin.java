package de.bybackfish.avalonaddons.mixins;

import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = StringHelper.class, priority = 1)
public class StringUtilMixin {

  /**
   * @author a
   * @reason a
   */
  @Overwrite
  public static String truncateChat(String string) {
    return string;
  }

}
