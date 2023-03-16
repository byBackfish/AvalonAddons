package de.bybackfish.avalonaddons.mixins;

import com.mojang.authlib.GameProfile;
import de.bybackfish.avalonaddons.events.ItemDropEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends AbstractClientPlayerEntity {

  public PlayerEntityMixin(ClientWorld world,
      GameProfile profile,
      @Nullable PlayerPublicKey publicKey) {
    super(world, profile, publicKey);
  }

  @Shadow
  public abstract Hand getActiveHand();

  @Inject(
      method = "dropSelectedItem",
      at = @At("HEAD"),
      cancellable = true)
  private void dropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<ItemEntity> cir) {
    if (new ItemDropEvent.FromHotbar(this.getMainHandStack(),
        this.getInventory().selectedSlot).call()) {
      cir.setReturnValue(null);
    }
  }

}
