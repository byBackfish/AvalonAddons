package de.bybackfish.avalonaddons.mixins;

import de.bybackfish.avalonaddons.events.PacketEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

  @Shadow
  private Channel channel;

  @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/Packet;)V", cancellable = true)
  private void onSendPacketHead(Packet<?> packet, CallbackInfo info) {
    if (new PacketEvent.Outgoing(packet).call()) {
      info.cancel();
    }
  }

  @Inject(at = @At("HEAD"), method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", cancellable = true)
  private void onReceivePacketHead(ChannelHandlerContext channelHandlerContext, Packet<?> packet,
      CallbackInfo ci) {
    if (channel.isOpen() && packet != null) {
      if (new PacketEvent.Incoming(packet).call()) {
        ci.cancel();
      }
    }
  }
}
