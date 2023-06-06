package de.bybackfish.avalonaddons.mixins;

import java.time.Instant;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.LastSeenMessageList.Acknowledgment;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatMessageC2SPacket.class)
public abstract class ChatMessageC2SPacketMixin implements Packet<ServerPlayPacketListener> {

  @Shadow
  @Final
  private String chatMessage;

  @Shadow
  @Final
  private Instant timestamp;

  @Shadow
  @Final
  private long salt;

  @Shadow
  @Final
  private @Nullable MessageSignatureData signature;

  @Shadow
  @Final
  private Acknowledgment acknowledgment;

  // replace the constructor's super call
  @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At(value = "RETURN"))
  private void init(PacketByteBuf buf, CallbackInfo ci) {
    System.out.println("init");
  }

  /**
   * @author a
   * @reason a
   */
  @Overwrite
  public void write(PacketByteBuf buf) {
    System.out.println("write");
    buf.writeString(this.chatMessage, 100000000);
    buf.writeInstant(this.timestamp);
    buf.writeLong(this.salt);
    buf.writeNullable(this.signature, MessageSignatureData::write);
    this.acknowledgment.write(buf);
  }
}
