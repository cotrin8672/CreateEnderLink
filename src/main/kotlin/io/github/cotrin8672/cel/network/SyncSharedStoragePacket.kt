package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier


class SyncSharedStoragePacket(private val data: CompoundTag = CompoundTag()) {
    companion object {
        @JvmStatic
        fun encode(pkt: SyncSharedStoragePacket, buf: FriendlyByteBuf) {
            buf.writeNbt(pkt.data)
        }

        @JvmStatic
        fun decode(buf: FriendlyByteBuf): SyncSharedStoragePacket {
            val tag = buf.readNbt()
            return SyncSharedStoragePacket(tag!!)
        }

        @JvmStatic
        fun handleOnClient(pkt: SyncSharedStoragePacket, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                SharedStorageHandler.instance = SharedStorageHandler.load(pkt.data)
            }

            ctx.get().packetHandled = true
        }
    }
}