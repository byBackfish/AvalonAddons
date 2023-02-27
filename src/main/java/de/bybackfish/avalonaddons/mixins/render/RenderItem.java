package de.bybackfish.avalonaddons.mixins.render;

import de.bybackfish.avalonaddons.events.ItemRenderGUIEvent;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.item.ItemRenderer.class)
public class RenderItem {

  @Inject(method = "renderGuiItemModel", at = @At("HEAD"), cancellable = true)
  public void renderGUIItemModel(ItemStack itemStack, int x, int y, BakedModel model,
      CallbackInfo ci) {
    if (new ItemRenderGUIEvent(itemStack, x, y, model).call()) {
      ci.cancel();
    }
  }


}
