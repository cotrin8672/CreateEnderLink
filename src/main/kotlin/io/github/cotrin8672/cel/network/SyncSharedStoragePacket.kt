package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.CreateEnderLink
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class SyncSharedStoragePacket(val data: CompoundTag) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<SyncSharedStoragePacket>(CreateEnderLink.asResource("sync_shared_storage"))

        val STREAM_CODEC: StreamCodec<ByteBuf, SyncSharedStoragePacket> = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SyncSharedStoragePacket::data,
            ::SyncSharedStoragePacket
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}
