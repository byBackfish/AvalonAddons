package de.bybackfish.avalonaddons.mixins;

import de.bybackfish.avalonaddons.events.ForegroundScreenRenderEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen  {

  @Inject(method = "render", at = @At("HEAD"))
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {

      Screen that = (Screen) (Object) this;

      if(new ForegroundScreenRenderEvent(that, matrices, mouseX, mouseY, delta).call())
          ci.cancel();
  }

}
