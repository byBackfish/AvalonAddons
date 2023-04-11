package de.bybackfish.avalonaddons.extensions

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color


public fun drawColoredTexture(
    matrices: MatrixStack,
    color: Color,
    x: Int,
    y: Int,
    u: Float,
    v: Float,
    width: Int,
    height: Int,
    textureWidth: Int,
    textureHeight: Int
) {

    val x0 = x;
    val x1 = x + width;

    val y0 = y;
    val y1 = y + height;

    val z = -11;

    val regionWidth = width
    val regionHeight = height

    val matrix = matrices.peek().positionMatrix

    val u0 = (u + 0.0f) / textureWidth.toFloat()
    val u1 = (u + regionWidth.toFloat()) / textureWidth.toFloat()

    val v0 = (v + 0.0f) / textureHeight.toFloat()
    val v1 = (v + regionHeight.toFloat()) / textureHeight.toFloat()

    RenderSystem.setShader { GameRenderer.getPositionColorTexProgram() }
    val bufferBuilder = Tessellator.getInstance().buffer
    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE)

    bufferBuilder.color(color.red, color.green, color.blue, color.alpha)
    bufferBuilder.vertex(matrix, x0.toFloat(), y1.toFloat(), z.toFloat())
        .color(color.red, color.green, color.blue, color.alpha)
        .texture(u0, v1).next()

    bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), z.toFloat())
        .color(color.red, color.green, color.blue, color.alpha)
        .texture(u1, v1).next()

    bufferBuilder.vertex(matrix, x1.toFloat(), y0.toFloat(), z.toFloat())
        .color(color.red, color.green, color.blue, color.alpha)
        .texture(u1, v0).next()

    bufferBuilder.vertex(matrix, x0.toFloat(), y0.toFloat(), z.toFloat())
        .color(color.red, color.green, color.blue, color.alpha)
        .texture(u0, v0).next()

    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
}
