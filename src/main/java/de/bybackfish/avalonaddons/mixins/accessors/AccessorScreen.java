package de.bybackfish.avalonaddons.mixins.accessors;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface AccessorScreen {

  @Invoker("renderTooltip")
  void invokeRenderTooltip(MatrixStack matrices, ItemStack item, int x, int y);

  @Invoker("addDrawableChild")
  <T extends Element & Drawable & Selectable> T invokeAddDrawableChild(T drawable);
}
