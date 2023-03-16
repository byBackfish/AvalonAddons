package de.bybackfish.avalonaddons.mixins.accessors;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface AccessorScreen {

  @Invoker("renderTooltip")
  void invokeRenderTooltip(MatrixStack matrices, ItemStack item, int x, int y);

}
