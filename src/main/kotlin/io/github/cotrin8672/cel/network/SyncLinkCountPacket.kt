package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

/**
 * Packet used to synchronize the amount of loaded block entities per frequency
 * from the server to the client.
 */
data class SyncLinkCountPacket(val data: CompoundTag) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<SyncLinkCountPacket>(CreateEnderLink.asResource("sync_link_counts"))

        val STREAM_CODEC: StreamCodec<ByteBuf, SyncLinkCountPacket> = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SyncLinkCountPacket::data,
            ::SyncLinkCountPacket
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}
