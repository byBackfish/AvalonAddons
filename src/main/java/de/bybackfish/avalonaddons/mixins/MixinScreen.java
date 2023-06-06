package de.bybackfish.avalonaddons.mixins;

import de.bybackfish.avalonaddons.events.ForegroundScreenRenderEvent;
import de.bybackfish.avalonaddons.events.RenderTooltipEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

  // shadow function renderTooltip

  @Shadow
  public abstract void renderTooltip(MatrixStack matrices, List<Text> lines,
      Optional<TooltipData> data, int x, int y);

  @Inject(method = "render", at = @At("HEAD"))
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {

    Screen that = (Screen) (Object) this;

    if (new ForegroundScreenRenderEvent(that, matrices, mouseX, mouseY, delta).call()) {
      ci.cancel();
    }
  }

  @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", cancellable = true)
  public void drawMouseOverTooltip(MatrixStack matrices, ItemStack stack, int x, int y,
      CallbackInfo ci) {
    List<Text> forceTooltip = new ArrayList<>();

    if (new RenderTooltipEvent(matrices, stack, x,
        y, forceTooltip).call()) {
      ci.cancel();
    }

    if (!forceTooltip.isEmpty()) {
      this.renderTooltip(matrices, forceTooltip, Optional.empty(), x, y);
      ci.cancel();
    }

  }


}
