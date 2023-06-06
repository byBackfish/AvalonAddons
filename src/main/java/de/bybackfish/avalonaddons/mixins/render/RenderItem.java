package de.bybackfish.avalonaddons.mixins.render;

import static com.mojang.blaze3d.systems.RenderSystem.depthMask;

import com.mojang.blaze3d.systems.RenderSystem;
import de.bybackfish.avalonaddons.AvalonAddons;
import de.bybackfish.avalonaddons.AvalonConfig;
import de.bybackfish.avalonaddons.core.feature.Feature;
import de.bybackfish.avalonaddons.events.ItemRenderGUIEvent;
import de.bybackfish.avalonaddons.features.ui.ItemViewer;
import java.awt.Color;
import kotlin.reflect.KClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.item.ItemRenderer.class)
public class RenderItem {
  @Shadow public float zOffset;

  @Inject(method = "renderGuiItemModel", at = @At("HEAD"), cancellable = true)
  public void renderGUIItemModel(ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
    if (new ItemRenderGUIEvent(stack, x, y).call()) {
      ci.cancel();
    }
  }


  // MIXIN:ItemviewerSearchMode
  @Inject(method = "innerRenderInGui(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V", at = @At("HEAD"))
  public void innerRenderInGui(LivingEntity entity, ItemStack stack, int x, int y, int seed,
      CallbackInfo ci) {
    if (!ItemViewer.Companion.getSearchMode()) {
      return;
    }
    if (MinecraftClient.getInstance().currentScreen == null) {
      return;
    }

    if(stack != null && stack.getNbt() != null && stack.getNbt().getBoolean("itemShowcase")) return;

    boolean matches = ItemViewer.Companion.doesItemMatchFilter(
        ItemViewer.Companion.getCurrentFilters(ItemViewer.Companion.getLastQuery()), stack, false);

    if (matches) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.push();
      matrixStack.translate(0, 0, 100 + zOffset);
      depthMask(false);
      DrawableHelper.fill(matrixStack, x, y, x + 16, y + 16, AvalonAddons.config.getItemSearchHighlightColorLight().getRGB());
      depthMask(true);
      matrixStack.pop();
    }
  }


  // MIXIN:ItemviewerSearchMode
  @Inject(method = "innerRenderInGui(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;IIII)V", at = @At("RETURN"))
  public void innerRenderInGuiReturn(LivingEntity entity, ItemStack stack, int x, int y,
      int seed, int depth, CallbackInfo ci) {
    if (stack != null && stack.getCount() != 1) {
      return;
    }
    if (!ItemViewer.Companion.getSearchMode()) {
      return;
    }
    if (MinecraftClient.getInstance().currentScreen == null) {
      return;
    }
    if(stack != null && stack.getNbt() != null && stack.getNbt().getBoolean("itemShowcase")) return;

    assert stack != null;
    boolean matches = ItemViewer.Companion.doesItemMatchFilter(
        ItemViewer.Companion.getCurrentFilters(ItemViewer.Companion.getLastQuery()), stack, true);

    if (!matches) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.push();
      matrixStack.translate(0, 0, 110 + zOffset);
      DrawableHelper.fill(matrixStack, x, y, x + 16, y + 16, AvalonAddons.config.getItemSearchHighlightColorDark().getRGB());
      matrixStack.pop();
    }
  }

  @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("RETURN"))
  public void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y,
      String countLabel, CallbackInfo ci) {
    if (stack != null
        && stack.getCount() != 1
    ) {
      if (!ItemViewer.Companion.getSearchMode()) {
        return;
      }
      if (MinecraftClient.getInstance().currentScreen == null) {
        return;
      }
      if(stack.getNbt() != null && stack.getNbt().getBoolean("itemShowcase")) return;

      boolean matches = ItemViewer.Companion.doesItemMatchFilter(
          ItemViewer.Companion.getCurrentFilters(ItemViewer.Companion.getLastQuery()), stack, true);

      if (!matches) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate(0, 0, 110 + zOffset);
        depthMask(false);
        DrawableHelper.fill(matrixStack, x, y, x + 16, y + 16, AvalonAddons.config.getItemSearchHighlightColorDark().getRGB());
        depthMask(true);
        matrixStack.pop();
      }
    }
  }


}
