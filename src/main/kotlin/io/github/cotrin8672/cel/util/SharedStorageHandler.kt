package io.github.cotrin8672.cel.util

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.saveddata.SavedData

class SharedStorageHandler : SavedData() {
    private val sharedInventories = mutableMapOf<Frequency, SharedItemStackHandler>()

    companion object {
        fun create() = SharedStorageHandler()

        fun load(tag: CompoundTag, registries: Provider) = create().load(tag, registries)

        var instance: SharedStorageHandler? = null
    }

    fun getOrCreateInventory(frequency: Frequency): SharedItemStackHandler {
        return sharedInventories.computeIfAbsent(frequency) {
            SharedItemStackHandler(27, this)
        }
    }

    override fun save(tag: CompoundTag, registries: Provider): CompoundTag {
        val frequencies = sharedInventories.keys
        val listTag = ListTag()
        for (frequency in frequencies) {
            val pairTag = CompoundTag().apply {
                put("Frequency", frequency.stack.saveOptional(registries))
                sharedInventories[frequency]?.serializeNBT(registries)?.let { put("Inventory", it) }
            }
            listTag.add(pairTag)
        }
        tag.put("SharedStorage", listTag)
        return tag
    }

    fun load(tag: CompoundTag, registries: Provider): SharedStorageHandler {
        val list = tag.getList("SharedStorage", Tag.TAG_COMPOUND.toInt())
        for (item in list.asIterable()) {
            if (item !is CompoundTag) continue
            val frequencyTag = item.getCompound("Frequency") ?: continue
            val inventoryTag = item.getCompound("Inventory") ?: continue

            val frequency = Frequency.of(ItemStack.parseOptional(registries, frequencyTag))
            val inventory = SharedItemStackHandler(27, this).apply { deserializeNBT(registries, inventoryTag) }

            sharedInventories[frequency] = inventory
        }
        return this
    }
}
