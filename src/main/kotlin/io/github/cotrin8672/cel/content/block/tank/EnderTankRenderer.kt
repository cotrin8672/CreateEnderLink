package io.github.cotrin8672.cel.content.block.tank

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
import com.simibubi.create.foundation.fluid.FluidRenderer
import io.github.cotrin8672.cel.client.FrequencyRenderer
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.createmod.ponder.api.level.PonderLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.util.Mth

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
        val totalHeight = 1 - 2 * capHeight - minPuddleHeight

        val behaviour = blockEntity.getBehaviour(SharedStorageBehaviour.TYPE)
        val sharedFluidTank = SharedStorageHandler.instance?.getOrCreateSharedFluidStorage(behaviour.getFrequencyItem())
            ?: return
        val level = if (blockEntity.level !is PonderLevel) {
            sharedFluidTank.fluidLevel?.getValue(partialTicks)
        } else {
            blockEntity.getFluidTank()!!.fluidAmount / blockEntity.getFluidTank()!!.capacity.toFloat()
        } ?: return
        if (level <= 0 && blockEntity.level !is PonderLevel) return

        val clampedLevel = Mth.clamp(level * totalHeight, 0f, totalHeight)

        val tank = blockEntity.getFluidTank() ?: return
        val fluidStack = tank.fluid
        if (fluidStack.isEmpty) return

        val xMin = tankHullWidth
        val xMax = 1 - tankHullWidth
        val yMin = capHeight + minPuddleHeight
        val yMax = yMin + clampedLevel
        val zMin = tankHullWidth
        val zMax = 1 - tankHullWidth

        ms.pushPose()
        FluidRenderer.renderFluidBox(
            fluidStack.fluid,
            fluidStack.amount.toLong(),
            xMin, yMin, zMin, xMax, yMax, zMax,
            buffer, ms, light, false, true, fluidStack.tag
        )
        ms.popPose()
    }
}
