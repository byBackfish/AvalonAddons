package de.bybackfish.avalonaddons.utils

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer.BEAM_TEXTURE
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3f
import java.awt.Color
import java.lang.Integer.max
import java.lang.Integer.min


fun drawLine(matrices: MatrixStack, x1: Int, y1: Int, x2: Int, y2: Int, color: Int = -1) {
    RenderSystem.enableBlend()
    RenderSystem.disableTexture()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader(GameRenderer::getPositionColorShader)
    val bufferBuilder = Tessellator.getInstance().buffer
    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
    val matrix = matrices.peek().positionMatrix

    val startX = min(x1, x2)
    val startY = min(y1, y2)
    val endX = max(x1, x2)
    val endY = max(y1, y2)
    val dx = endX - startX
    val dy = endY - startY
    val dirX = if (x1 < x2) 1 else -1
    val dirY = if (y1 < y2) 1 else -1

    bufferBuilder.vertex(matrix, (startX - dirY).toFloat(), (startY + dirX).toFloat(), 0f)
        .color(color).next()
    bufferBuilder.vertex(matrix, (endX - dirY).toFloat(), (endY + dirX).toFloat(), 0f).color(color)
        .next()
    bufferBuilder.vertex(matrix, (endX + dirY).toFloat(), (endY - dirX).toFloat(), 0f).color(color)
        .next()
    bufferBuilder.vertex(matrix, (startX + dirY).toFloat(), (startY - dirX).toFloat(), 0f)
        .color(color).next()

    Tessellator.getInstance().draw()
    RenderSystem.enableTexture()
    RenderSystem.disableBlend()
}


fun renderBeaconBeam(matrices: MatrixStack, beaconPos: BlockPos) {
    val minecraft = MinecraftClient.getInstance()
    matrices.push()
    matrices.translate(beaconPos.x.toDouble(), 0.0, beaconPos.z.toDouble())
    BeaconBlockEntityRenderer.renderBeam(
        matrices,
        minecraft.bufferBuilders.entityVertexConsumers,
        BEAM_TEXTURE,
        minecraft.tickDelta,
        1f,
        minecraft.player!!.entityWorld.time,
        0,
        255,
        DyeColor.WHITE.colorComponents,
        0.15f,
        0.175f
    )
    matrices.pop()
}

fun drawTextInWorld(
    text: Text,
    x: Double,
    y: Double,
    z: Double,
    offX: Double,
    offY: Double,
    scale: Double,
    background: Boolean,
    shadow: Boolean,
    color: Color = Color.WHITE
) {
    val mc = MinecraftClient.getInstance()

    val camera = mc.gameRenderer.camera
    val matrix = camera.matrixFrom(x, y, z) // Is below
    val vertex = mc.bufferBuilders.outlineVertexConsumers

    matrix.push()
    matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.yaw))
    matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.pitch))
    matrix.translate(offX, offY, 0.0)
    matrix.scale(-0.025f * scale.toFloat(), -0.025f * scale.toFloat(), 1f)

    if (background) {
        mc.textRenderer.draw(
            text,
            -mc.textRenderer.getWidth(text) / 2f,
            0f,
            553648127,
            false,
            matrix.peek().positionMatrix,
            vertex,
            true,
            (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f) * 255.0f).toInt() shl 24,
            0xf000f0
        )
    }

    if (shadow) {
        matrix.translate(1.0, 1.0, 0.0)
        mc.textRenderer.draw(
            text.copy(),
            -mc.textRenderer.getWidth(text) / 2f,
            0f,
            0x202020,
            false,
            matrix.peek().positionMatrix,
            vertex,
            true,
            0,
            0xf000f0
        )
    }

    mc.textRenderer.draw(
        text,
        -mc.textRenderer.getWidth(text) / 2f,
        0f,
        color.rgb,
        false,
        matrix.peek().positionMatrix,
        vertex,
        true,
        0,
        0xf000f0
    )
    vertex.draw()
    matrix.pop()
}


fun Camera.matrixFrom(x: Double, y: Double, z: Double): MatrixStack {
    val matrix = MatrixStack()
    matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch))
    matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw + 180.0f))
    matrix.translate(x - pos.x, y - pos.y, z - pos.z)
    return matrix
}


fun drawTexturedRect(
    matrices: MatrixStack,
    texture: Identifier,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) {
    drawTexturedRect(
        matrices,
        texture,
        x,
        y,
        width,
        height,
        width,
        height
    )
}

fun drawTexturedRect(
    matrices: MatrixStack,
    tex: Identifier?,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    textureWidth: Int,
    textureHeight: Int
) {
    drawTexturedRect(
        matrices,
        tex,
        x,
        y,
        0,
        width,
        height,
        0,
        0,
        width.toInt(),
        height.toInt(),
        textureWidth,
        textureHeight
    )
}

fun drawTexturedRect(
    matrices: MatrixStack,
    tex: Identifier?,
    x: Int,
    y: Int,
    z: Int,
    width: Int,
    height: Int,
    textureWidth: Int,
    textureHeight: Int
) {
    drawTexturedRect(
        matrices,
        tex,
        x,
        y,
        z,
        width,
        height,
        0,
        0,
        width.toInt(),
        height.toInt(),
        textureWidth,
        textureHeight
    )
}

fun drawTexturedRect(
    matrices: MatrixStack,
    tex: Identifier?,
    x: Int,
    y: Int,
    z: Int,
    width: Int,
    height: Int,
    uOffset: Int,
    vOffset: Int,
    u: Int,
    v: Int,
    textureWidth: Int,
    textureHeight: Int
) {
    val uScale = 1f / textureWidth
    val vScale = 1f / textureHeight
    val matrix: Matrix4f = matrices.peek().positionMatrix
    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
    RenderSystem.setShaderTexture(0, tex)
    val bufferBuilder: BufferBuilder = Tessellator.getInstance().buffer
    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
    bufferBuilder
        .vertex(matrix, x.toFloat(), (y + height).toFloat(), z.toFloat())
        .texture(uOffset * uScale, (vOffset + v) * vScale)
        .next()
    bufferBuilder
        .vertex(matrix, (x + width).toFloat(), (y + height).toFloat(), z.toFloat())
        .texture((uOffset + u) * uScale, (vOffset + v) * vScale)
        .next()
    bufferBuilder
        .vertex(matrix, (x + width).toFloat(), y.toFloat(), z.toFloat())
        .texture((uOffset + u) * uScale, vOffset * vScale)
        .next()
    bufferBuilder
        .vertex(matrix, x.toFloat(), y.toFloat(), z.toFloat())
        .texture(uOffset * uScale, vOffset * vScale)
        .next()

    BufferRenderer.drawWithShader(bufferBuilder.end())
}