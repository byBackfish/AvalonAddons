package de.bybackfish.avalonaddons.mixins;

import de.bybackfish.avalonaddons.events.DrawSlotEvent;
import de.bybackfish.avalonaddons.events.GUIKeyPressEvent;
import de.bybackfish.avalonaddons.events.ItemActionEvent;
import de.bybackfish.avalonaddons.events.RenderTooltipEvent;
import java.util.Objects;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

  @Shadow
  protected int backgroundWidth;
  @Shadow
  protected int backgroundHeight;
  @Shadow
  @Nullable
  protected Slot focusedSlot;
  @Shadow
  @Final
  protected T handler;

  protected HandledScreenMixin(Text title) {
    super(title);
  }


  @Inject(at = @At("HEAD"), method = "drawMouseoverTooltip", cancellable = true)
  public void drawMouseOverTooltip(MatrixStack matrices, int x, int y, CallbackInfo ci) {
    if (this.focusedSlot == null || this.focusedSlot.getStack().isEmpty()) {
      return;
    }
    if (new RenderTooltipEvent(matrices, this.focusedSlot, this.focusedSlot.getStack(), x,
        y).call()) {
      ci.cancel();
    }
  }

  @Inject(
      at = @At("HEAD"),
      method = "keyPressed",
      cancellable = true
  )
  void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
    ItemStack itemClickedAt = null;
    if (this.focusedSlot != null) {
      itemClickedAt = this.focusedSlot.getStack();
    }
    if (new GUIKeyPressEvent(keyCode, scanCode, modifiers, this.focusedSlot,
        itemClickedAt).call()) {
      cir.setReturnValue(true);
    }
  }


  /**
   * @author y
   * @reason y
   */
  @Overwrite
  public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
    if (slot != null) {
      slotId = slot.id;
    }

    if (slot != null) {
      if (new ItemActionEvent(actionType, slot, slot.getStack()).call()) {
        return;
      }
    }

    assert Objects.requireNonNull(this.client).interactionManager != null;
    assert this.client.interactionManager != null;
    this.client.interactionManager.clickSlot(this.handler.syncId, slotId, button, actionType,
        this.client.player);
  }


  @Inject(at = @At("TAIL"), method = "drawSlot", cancellable = true)
  public void drawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci) {

    if (slot.inventory instanceof PlayerInventory) {
      if (new DrawSlotEvent(matrices, slot, slot.getStack(), slot.x, slot.y).call()) {
        ci.cancel();
      }
    }
  }

}
