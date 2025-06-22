package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.model.LinkedCount
import io.github.cotrin8672.cel.util.LinkCountManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class SyncFullLinkPacket(private val linkedList: List<LinkedCount>) {
    companion object {
        @JvmStatic
        fun decode(buf: FriendlyByteBuf): SyncFullLinkPacket {
            val tag = buf.readNbt()
            val listTag = tag?.getList("linkedList", Tag.TAG_COMPOUND.toInt()) ?: listOf()

            return SyncFullLinkPacket(
                listTag.map {
                    LinkedCount.parseOptional(it as CompoundTag)
                }
            )
        }
    }

    fun encode(buf: FriendlyByteBuf) {
        val listTag = ListTag()
        linkedList.forEach {
            listTag.add(it.saveOptional())
        }

        buf.writeNbt(CompoundTag().apply {
            put("linkedList", listTag)
        })
    }

    fun handleOnClient(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            LinkCountManager.onClientPacketReceived(linkedList)
        }

        ctx.get().packetHandled = true
    }
}
