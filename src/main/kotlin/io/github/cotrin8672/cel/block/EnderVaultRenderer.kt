package io.github.cotrin8672.cel.block

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
import io.github.cotrin8672.cel.client.FrequencyRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context

class EnderVaultRenderer(context: Context) : SmartBlockEntityRenderer<EnderVaultBlockEntity>(context) {
    override fun renderSafe(
        blockEntity: EnderVaultBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay)
        FrequencyRenderer.renderOnBlockEntity(blockEntity, partialTicks, ms, buffer, 15728640, overlay)
    }
}
