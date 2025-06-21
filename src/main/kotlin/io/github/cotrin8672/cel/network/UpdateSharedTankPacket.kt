package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.model.StorageFrequency
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.fluids.FluidStack

class UpdateSharedTankPacket(
    val storageFrequency: StorageFrequency,
    val fluidStack: FluidStack,
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<UpdateSharedTankPacket>(CreateEnderLink.asResource("update_shared_tank"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, UpdateSharedTankPacket> = StreamCodec.composite(
            StorageFrequency.STREAM_CODEC,
            UpdateSharedTankPacket::storageFrequency,
            FluidStack.OPTIONAL_STREAM_CODEC,
            UpdateSharedTankPacket::fluidStack,
            ::UpdateSharedTankPacket
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}
