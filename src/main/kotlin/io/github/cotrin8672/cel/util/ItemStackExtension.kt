package io.github.cotrin8672.cel.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack

var ItemStack.storageFrequency: StorageFrequency
    get() {
        val tag = tag ?: return StorageFrequency.EMPTY
        if (!tag.contains("StorageFrequency")) return StorageFrequency.EMPTY

        val storageFrequencyTag = tag.getCompound("StorageFrequency")
        return StorageFrequency.parseOptional(storageFrequencyTag)
    }

    set(value) {
        tag ?: run { tag = CompoundTag() }
        tag?.put("StorageFrequency", value.saveOptional())
    }