package de.bybackfish.avalonaddons.mixins;

import de.bybackfish.avalonaddons.events.ClientChatEvent;
import java.util.ArrayList;
import java.util.List;
import kotlin.text.Regex;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.w3c.dom.stylesheets.LinkStyle;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

  @Inject(method = "sendMessage", at = @At("HEAD"))
  private void sendMessage(String chatText, boolean addToHistory,
      CallbackInfoReturnable<Boolean> cir) {
    ClientChatEvent event = new ClientChatEvent.Sent(chatText);
    boolean cancelled = event.call();
    if (cancelled) {
      cir.cancel();
    }
  }
}
