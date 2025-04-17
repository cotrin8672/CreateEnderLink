package io.github.cotrin8672.cel.content.block.tank

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
import com.simibubi.create.foundation.fluid.FluidRenderer
import io.github.cotrin8672.cel.client.FrequencyRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.util.Mth
import thedarkcolour.kotlinforforge.neoforge.forge.use

class EnderTankRenderer(context: Context) : SmartBlockEntityRenderer<EnderTankBlockEntity>(context) {
    override fun renderSafe(
        blockEntity: EnderTankBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay)
        FrequencyRenderer.renderOnBlockEntity(blockEntity, partialTicks, ms, buffer, 15728640, overlay)

        val capHeight = 1 / 4f
        val tankHullWidth = 1 / 16f + 1 / 128f
        val minPuddleHeight = 1 / 16f
        val totalHeight: Float = be.height - 2 * capHeight - minPuddleHeight

        val clampedLevel = Mth.clamp(level * totalHeight, 0f, totalHeight)

        val xMin = tankHullWidth
        val xMax: Float = xMin + 1 - 2 * tankHullWidth
        val yMin: Float = totalHeight + capHeight + minPuddleHeight - clampedLevel
        val yMax: Float = yMin + clampedLevel

        ms.use {
            FluidRenderer.renderFluidBox(
                blockEntity.getFluidTank()?.fluid?.fluid,
                blockEntity.getFluidTank()?.fluid?.amount,

                )
        }
    }
}
