package de.bybackfish.avalonaddons.mixins;

import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerListHud.class)
abstract class MixinPlayerListHud extends DrawableHelper {

  @Shadow
  @Final
  private static Ordering<PlayerListEntry> ENTRY_ORDERING;
  List<String> uuids = List.of(
      "8c9b8116-9998-49ea-90f1-6daa2e7df5ea",
      "b73b1363-9e4e-491f-96c6-657367682cb9",
      "0757571f-22d8-4bfe-abc7-395c52121a17",
      "0f90a00e-bd60-41fd-b43f-d365cf7513b6"
  );
  int iconSize = 8;
  @Shadow
  @Final
  private MinecraftClient client;
  @Shadow
  @Nullable
  private Text header;
  @Shadow
  @Nullable
  private Text footer;

  @Shadow
  public abstract Text getPlayerName(PlayerListEntry entry);

  @Shadow
  protected abstract void renderScoreboardObjective(ScoreboardObjective objective, int y,
      String player, int startX, int endX, PlayerListEntry entry, MatrixStack matrices);

  @Shadow
  protected abstract void renderLatencyIcon(MatrixStack matrices, int width, int x, int y,
      PlayerListEntry entry);

  /**
   * @author bybackfish
   * @reason Remove PlayerListHud
   */
  @Overwrite
  public void render(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard,
      @Nullable ScoreboardObjective objective) {
    assert this.client.player != null;
    ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
    List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(
        clientPlayNetworkHandler.getPlayerList());
    int i = 0;
    int j = 0;
    Iterator<PlayerListEntry> var9 = list.iterator();

    int k;
    while (var9.hasNext()) {
      PlayerListEntry playerListEntry = (PlayerListEntry) var9.next();
      k = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
      i = Math.max(i, k);
      if (objective != null && objective.getRenderType() != RenderType.HEARTS) {
        TextRenderer var10000 = this.client.textRenderer;
        ScoreboardPlayerScore var10001 = scoreboard.getPlayerScore(
            playerListEntry.getProfile().getName(), objective);
        k = var10000.getWidth(" " + var10001.getScore());
        j = Math.max(j, k);
      }
    }

    list = list.subList(0, Math.min(list.size(), 80));
    int l = list.size();
    int m = l;

    for (k = 1; m > 20; m = (l + k - 1) / k) {
      ++k;
    }

    boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection()
        .isEncrypted();
    int n;
    if (objective != null) {
      if (objective.getRenderType() == RenderType.HEARTS) {
        n = 90;
      } else {
        n = j;
      }
    } else {
      n = 0;
    }

    int o = Math.min(k * ((bl ? 9 : 0) + i + n + 13), scaledWindowWidth - 50) / k;
    int p = scaledWindowWidth / 2 - (o * k + (k - 1) * 5) / 2;
    int q = 10;
    int r = o * k + (k - 1) * 5;
    List<OrderedText> list2 = null;
    if (this.header != null) {
      list2 = this.client.textRenderer.wrapLines(this.header, scaledWindowWidth - 50);

      OrderedText orderedText;
      for (Iterator<OrderedText> var19 = list2.iterator(); var19.hasNext();
          r = Math.max(r, this.client.textRenderer.getWidth(orderedText))) {
        orderedText = (OrderedText) var19.next();
      }
    }

    List<OrderedText> list3 = null;
    OrderedText orderedText2;
    Iterator var35;
    if (this.footer != null) {
      list3 = this.client.textRenderer.wrapLines(this.footer, scaledWindowWidth - 50);

      for (var35 = list3.iterator(); var35.hasNext();
          r = Math.max(r, this.client.textRenderer.getWidth(orderedText2))) {
        orderedText2 = (OrderedText) var35.next();
      }
    }

    int var10002;
    int var10003;
    int var10005;
    int s;
    int var33;
    if (list2 != null) {
      var33 = scaledWindowWidth / 2 - r / 2 - 1;
      var10002 = q - 1;
      var10003 = scaledWindowWidth / 2 + r / 2 + 1;
      var10005 = list2.size();
      Objects.requireNonNull(this.client.textRenderer);
      fill(matrices, var33, var10002, var10003, q + var10005 * 9, Integer.MIN_VALUE);

      for (var35 = list2.iterator(); var35.hasNext(); q += 9) {
        orderedText2 = (OrderedText) var35.next();
        s = this.client.textRenderer.getWidth(orderedText2);

        this.client.textRenderer.drawWithShadow(matrices, orderedText2,
            (float) (scaledWindowWidth / 2 - s / 2), (float) q, -1);
        Objects.requireNonNull(this.client.textRenderer);
      }

      ++q;
    }

    fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1,
        q + m * 9, Integer.MIN_VALUE);
    int t = this.client.options.getTextBackgroundColor(553648127);

    int v;
    for (int u = 0; u < l; ++u) {
      s = u / m;
      v = u % m;
      int w = p + s * o + s * 5;
      int x = q + v * 9;
      fill(matrices, w, x, w + o, x + 8, t);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      if (u < list.size()) {
        PlayerListEntry playerListEntry2 = (PlayerListEntry) list.get(u);
        GameProfile gameProfile = playerListEntry2.getProfile();
        if (bl) {
          PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
          boolean bl2 =
              playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
          boolean bl3 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT);
          RenderSystem.setShaderTexture(0, playerListEntry2.getSkinTexture());
          PlayerSkinDrawer.draw(matrices, w, x, 8, bl3, bl2);

          // custom

          String uuid = gameProfile.getId().toString();
          boolean shouldRender = uuids.contains(uuid);

          if (shouldRender) {
            RenderSystem.setShaderTexture(0, new Identifier("textures/mob_effect/resistance.png"));
            DrawableHelper.drawTexture(matrices, w - 9, x + 1, 0.0F, 0.0F, iconSize, iconSize,
                iconSize,
                iconSize);
          }

          w += 9;
        }

        this.client.textRenderer.drawWithShadow(matrices, this.getPlayerName(playerListEntry2),
            (float) w, (float) x,
            playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
        if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
          int y = w + i + 1;
          int z = y + n;
          if (z - y > 5) {
            this.renderScoreboardObjective(objective, x, gameProfile.getName(), y, z,
                playerListEntry2, matrices);
          }
        }

        this.renderLatencyIcon(matrices, o, w - (bl ? 9 : 0), x, playerListEntry2);
      }
    }

    if (list3 != null) {
      q += m * 9 + 1;
      var33 = scaledWindowWidth / 2 - r / 2 - 1;
      var10002 = q - 1;
      var10003 = scaledWindowWidth / 2 + r / 2 + 1;
      var10005 = list3.size();
      Objects.requireNonNull(this.client.textRenderer);
      fill(matrices, var33, var10002, var10003, q + var10005 * 9, Integer.MIN_VALUE);

      for (Iterator var38 = list3.iterator(); var38.hasNext(); q += 9) {
        OrderedText orderedText3 = (OrderedText) var38.next();
        v = this.client.textRenderer.getWidth(orderedText3);
        this.client.textRenderer.drawWithShadow(matrices, orderedText3,
            (float) (scaledWindowWidth / 2 - v / 2), (float) q, -1);
        Objects.requireNonNull(this.client.textRenderer);
      }
    }

  }

}
