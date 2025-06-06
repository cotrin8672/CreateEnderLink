package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class SyncFrequencyCountPacket(val data: CompoundTag) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<SyncFrequencyCountPacket>(CreateEnderLink.asResource("sync_frequency_count"))
        val STREAM_CODEC: StreamCodec<ByteBuf, SyncFrequencyCountPacket> = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SyncFrequencyCountPacket::data,
            ::SyncFrequencyCountPacket
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
