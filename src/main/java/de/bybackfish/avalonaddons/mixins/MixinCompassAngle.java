package de.bybackfish.avalonaddons.mixins;


import de.bybackfish.avalonaddons.features.quests.QuestDisplay;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassAnglePredicateProvider.class)
public class MixinCompassAngle {

  @Inject(method = "getAngleTo(Lnet/minecraft/entity/Entity;JLnet/minecraft/util/math/BlockPos;)F", at = @At("HEAD"), cancellable = true)
  private void getAngleTo(Entity entity, long time, BlockPos pos, CallbackInfoReturnable<Float> cir) {
    QuestDisplay.setCompassTarget(pos);
  }

}
