package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.model.LinkedCount
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class SyncFullLinkPacket(
    val linkedList: List<LinkedCount>,
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<SyncFullLinkPacket>(CreateEnderLink.asResource("sync_linked_count"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncFullLinkPacket> = StreamCodec.composite(
            LinkedCount.STREAM_CODEC.apply(ByteBufCodecs.list(256)),
            { pkt -> pkt.linkedList },
            ::SyncFullLinkPacket
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}