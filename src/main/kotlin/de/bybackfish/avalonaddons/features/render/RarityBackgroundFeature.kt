package de.bybackfish.avalonaddons.features.render

import com.mojang.blaze3d.systems.RenderSystem
import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.avalon.ItemRarity
import de.bybackfish.avalonaddons.core.annotations.*
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.ItemRenderGUIEvent
import de.bybackfish.avalonaddons.extensions.drawColoredTexture
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color


@Category("Render")
class RarityBackgroundFeature : Feature() {


    val options = arrayOf("rarity", "rarity2", "rarity3", "rarity4")

    @Property(
        forceType = PropertyType.SELECTOR,
        sortingOrder = 1,
        description = "What texture to use for the background",
        options = ["Inner Circle", "Inner Square", "Full Square (Colored)", "Border (Colored)"]
    )
    var option = 2;

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 2,
        description = "Color of the background for trash items"
    )
    // with 75% alpha
    var trashColor = Color(64, 64, 64, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 3,
        description = "Color of the background for common items"
    )
    var commonColor = Color(255, 255, 255, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 4,
        description = "Color of the background for uncommon items"
    )
    var uncommonColor = Color(0, 255, 0, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 5,
        description = "Color of the background for rare items"
    )
    var rareColor = Color(0, 0, 255, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 6,
        description = "Color of the background for epic items"
    )
    var epicColor = Color(128, 0, 128, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 7,
        description = "Color of the background for exotic items"
    )
    // light red
    var exoticColor = Color(255, 128, 128, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 8,
        description = "Color of the background for legendary items"
    )
    var legendaryColor = Color(255, 255, 0, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 9,
        description = "Color of the background for mythic items"
    )
    var mythicColor = Color(255, 128, 0, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 10,
        description = "Color of the background for relic items"
    )
    // aqua/cyan
    var relicColor = Color(0, 255, 255, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 11,
        description = "Color of the background for elder items"
    )
    var elderColor = Color(255, 0, 0, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 12,
        description = "Color of the background for ancient items"
    )
    // gray
    var ancientColor = Color(128, 128, 128, 255)

    @Property(
        forceType = PropertyType.COLOR,
        sortingOrder = 13,
        description = "Color of the background for spooky items"
    )
    var spookyColor = Color(225, 100, 0, 255)

    @Subscribe
    fun onEvent(event: ItemRenderGUIEvent) {
        val item = event.item
        val rarity = ItemRarity.getFromItem(item) ?: return
        val color = getColor(rarity) ?: return

        val matrixStack = MatrixStack()

        val depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST)
        if (depthEnabled) RenderSystem.disableDepthTest()


        RenderSystem.enableBlend()

        RenderSystem.setShaderTexture(
            0,
            Identifier(AvalonAddons.NAMESPACE, "${options[option]}.png")
        )
        RenderSystem.blendFunc(770, 771)

        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        drawColoredTexture(matrixStack, color, event.x, event.y, 0f, 0f, 16, 16, 16, 16)
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)

        if (depthEnabled) RenderSystem.enableDepthTest()
    }

    private fun getColor(itemRarity: ItemRarity): Color? {
        return when (itemRarity) {
            ItemRarity.COMMON -> commonColor
            ItemRarity.UNCOMMON -> uncommonColor
            ItemRarity.RARE -> rareColor
            ItemRarity.EPIC -> epicColor
            ItemRarity.EXOTIC -> exoticColor
            ItemRarity.LEGENDARY -> legendaryColor
            ItemRarity.MYTHIC -> mythicColor
            ItemRarity.RELIC -> relicColor
            ItemRarity.ELDER -> elderColor
            ItemRarity.ANCIENT -> ancientColor
            ItemRarity.SPOOKY -> spookyColor
            else -> null
        }
    }


    /*


            val lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING)
        val depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST)
        val alphaEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST)

        if (lightingEnabled) GL11.glDisable(GL11.GL_LIGHTING)
        if (depthEnabled) GL11.glDisable(GL11.GL_DEPTH)
        GL11.glPushMatrix()
        GlStateManager._enableBlend()
        if (!alphaEnabled) GL11.glEnable(GL11.GL_ALPHA)
        RenderSystem.setShaderTexture(0, Identifier("avalonaddons/rarity.png"))
        GL11.glColor4d(
            (color.red / 255f).toDouble(),
            (color.green / 255f).toDouble(),
            (color.blue / 255f).toDouble(),
            alpha.toDouble()
        )
        GL11.glPopMatrix()
        GlStateManager._blendFunc(770, 771)
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND)
        val matrixStack = RenderSystem.getModelViewStack()
        DrawableHelper.drawTexture(matrixStack, event.x, event.y, 0f, 0f, 16, 16, 16, 16)
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE)
        if (lightingEnabled) GL11.glEnable(GL11.GL_LIGHTING)
        if (depthEnabled) GL11.glEnable(GL11.GL_DEPTH)
        if (!alphaEnabled) GL11.glDisable(GL11.GL_ALPHA)

     */


}