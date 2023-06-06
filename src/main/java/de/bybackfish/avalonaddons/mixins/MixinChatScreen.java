package de.bybackfish.avalonaddons.mixins;

import de.bybackfish.avalonaddons.events.ClientChatEvent;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

  @Shadow
  protected TextFieldWidget chatField;

  @Inject(method = "sendMessage", at = @At("HEAD"))
  private void sendMessage(String chatText, boolean addToHistory,
      CallbackInfoReturnable<Boolean> cir) {
    ClientChatEvent event = new ClientChatEvent.Sent(chatText);
    boolean cancelled = event.call();
    if (cancelled) {
      cir.cancel();
    }
  }

  @Inject(method = "init", at = @At(value = "RETURN"))
  private void setMaxLength(CallbackInfo ci) {
    System.out.println("setMaxLength");
    this.chatField.setMaxLength(100000);
  }

}
