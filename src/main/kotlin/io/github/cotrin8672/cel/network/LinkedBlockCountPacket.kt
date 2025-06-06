package io.github.cotrin8672.cel.network

import io.github.cotrin8672.cel.util.LinkedCountManager
import io.github.cotrin8672.cel.util.StorageFrequency
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class FullLinkedCountPacket(private val allCounts: Map<ResourceKey<out Block>, Map<StorageFrequency, Int>>) {
    companion object {
        @JvmStatic
        fun decode(buf: FriendlyByteBuf): FullLinkedCountPacket {
            val tag = buf.readNbt() ?: return FullLinkedCountPacket(emptyMap())

            val allCounts: Map<ResourceKey<out Block>, Map<StorageFrequency, Int>> = tag.allKeys.associate { resource ->
                ResourceKey.create(Registries.BLOCK, ResourceLocation.parse(resource)) to tag.getList(
                    resource,
                    Tag.TAG_COMPOUND.toInt()
                ).associate {
                    val item = it as CompoundTag
                    StorageFrequency.parseOptional(item.get("Frequency") as CompoundTag) to item.getInt("Count")
                }
            }

            return FullLinkedCountPacket(allCounts)
        }
    }

    fun encode(buf: FriendlyByteBuf) {
        val tag = CompoundTag()

        val blocks = this.allCounts.keys
        for (block in blocks) {
            val list = ListTag()
            this.allCounts[block]!!.map {
                CompoundTag().apply {
                    put("Frequency", it.key.saveOptional())
                    putInt("Count", it.value)
                }
            }.forEach(list::add)

            tag.put(block.toString(), list)
        }

        buf.writeNbt(tag)
    }

    fun handleOnClient(ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork {
            LinkedCountManager.syncFullLinkedCount(this.allCounts)
        }

        ctx.get().packetHandled = true
    }
}

class PartialLinkedCountPacket(
    private val blockId: ResourceLocation,
    private val storageFrequency: StorageFrequency,
    private val newCount: Int,
) {
    companion object {
//        @JvmStatic
//        fun decode(buf: FriendlyByteBuf): PartialLinkedCountPacket {
//            val tag = buf.readNbt() ?: return PartialLinkedCountPacket(ResourceLocation)
//
//            val allCounts = tag.allKeys.associate { resource ->
//                ResourceLocation.parse(resource) to tag.getList(resource, Tag.TAG_COMPOUND.toInt()).associate {
//                    val item = it as CompoundTag
//                    StorageFrequency.parseOptional(item.get("Frequency") as CompoundTag) to item.getInt("Count")
//                }
//            }
//
//            return PartialLinkedCountPacket(allCounts)
//        }
    }
}
