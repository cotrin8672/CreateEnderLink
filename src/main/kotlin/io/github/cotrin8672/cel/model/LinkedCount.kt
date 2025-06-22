package io.github.cotrin8672.cel.model

import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block

data class LinkedCount(
    val blockKey: ResourceKey<out Block>,
    val linkedList: Map<StorageFrequency, Int> = mapOf(),
) {
    companion object {
        fun parseOptional(tag: CompoundTag): LinkedCount {
            if (tag.isEmpty) {
                throw IllegalArgumentException("No LinkedCount data present in tag")
            }

            val keyStr = tag.getString("block_key")
            val loc =
                ResourceLocation.tryParse(keyStr) ?: throw IllegalArgumentException("Invalid block_key: '$keyStr'")
            val blockKey = ResourceKey.create(Registries.BLOCK, loc)

            val listTag = tag.getList("linked_list", Tag.TAG_COMPOUND.toInt())
            val map = listTag.associate {
                it as CompoundTag
                val storageFrequency = StorageFrequency.parseOptional(it.getCompound("storageFrequency"))
                val count = it.getInt("count")
                storageFrequency to count
            }

            return LinkedCount(blockKey, map)
        }
    }

    fun saveOptional(): CompoundTag {
        if (linkedList.isEmpty()) return CompoundTag()
        val tag = CompoundTag()
        tag.putString("block_key", blockKey.location().toString())

        val listTag = ListTag()
        for ((freq, count) in linkedList) {
            listTag.add(CompoundTag().apply {
                put("storageFrequency", freq.saveOptional())
                putInt("count", count)
            })
        }
        tag.put("linked_list", listTag)
        return tag
    }
}
