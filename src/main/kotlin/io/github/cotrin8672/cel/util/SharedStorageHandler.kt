package io.github.cotrin8672.cel.util

import io.github.cotrin8672.cel.content.storage.SharedFluidTank
import io.github.cotrin8672.cel.content.storage.SharedItemStackHandler
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.saveddata.SavedData

class SharedStorageHandler : SavedData() {
    private val sharedItemStorage = mutableMapOf<StorageFrequency, SharedItemStackHandler>()
    private val sharedFluidStorage = mutableMapOf<StorageFrequency, SharedFluidTank>()

    companion object {
        fun create() = SharedStorageHandler()

        fun load(tag: CompoundTag, registries: Provider) = create().load(tag, registries)

        var instance: SharedStorageHandler? = null
    }

    fun getOrCreateSharedItemStorage(frequency: StorageFrequency): SharedItemStackHandler {
        return sharedItemStorage.computeIfAbsent(frequency) {
            SharedItemStackHandler(27, this)
        }
    }

    fun getOrCreateSharedFluidStorage(frequency: StorageFrequency): SharedFluidTank {
        return sharedFluidStorage.computeIfAbsent(frequency) {
            SharedFluidTank(10000, this)
        }
    }

    fun tick() {
        sharedFluidStorage.values.forEach {
            it.fluidLevel?.tickChaser()
        }
    }

    override fun save(tag: CompoundTag, registries: Provider): CompoundTag {
        val vaultFrequencies = sharedItemStorage.keys
        val listTag = ListTag()
        for (frequency in vaultFrequencies) {
            val pairTag = CompoundTag().apply {
                put("StorageFrequency", frequency.saveOptional(registries))
                sharedItemStorage[frequency]?.serializeNBT(registries)?.let { put("Inventory", it) }
            }
            listTag.add(pairTag)
        }
        tag.put("SharedVaultStorage", listTag)

        val tankFrequencies = sharedFluidStorage.keys
        val fluidListTag = ListTag()
        for (frequency in tankFrequencies) {
            val pairTag = CompoundTag().apply {
                put("StorageFrequency", frequency.saveOptional(registries))
                sharedFluidStorage[frequency]?.writeToNBT(registries, CompoundTag())?.let { put("Tank", it) }
            }
            fluidListTag.add(pairTag)
        }
        tag.put("SharedFluidStorage", fluidListTag)

        return tag
    }

    fun load(tag: CompoundTag, registries: Provider): SharedStorageHandler {
        val list = if (tag.contains("SharedStorage"))
            tag.getList("SharedStorage", Tag.TAG_COMPOUND.toInt())
        else
            tag.getList("SharedVaultStorage", Tag.TAG_COMPOUND.toInt())
        
        for (item in list.asIterable()) {
            if (item !is CompoundTag) continue

            if (item.contains("Frequency", Tag.TAG_COMPOUND.toInt())) {
                val frequencyItemTag = item.getCompound("Frequency")
                val inventoryTag = item.getCompound("Inventory")
                val frequency = StorageFrequency.of(ItemStack.parseOptional(registries, frequencyItemTag))
                val inventory = SharedItemStackHandler(27, this).apply { deserializeNBT(registries, inventoryTag) }

                sharedItemStorage[frequency] = inventory
            } else if (item.contains("StorageFrequency", Tag.TAG_COMPOUND.toInt())) {
                val storageFrequency = StorageFrequency.parseOptional(registries, item.getCompound("StorageFrequency"))
                val inventory = SharedItemStackHandler(27, this).apply {
                    deserializeNBT(registries, item.getCompound("Inventory"))
                }

                sharedItemStorage[storageFrequency] = inventory
            }
        }

        val fluidList = tag.getList("SharedFluidStorage", Tag.TAG_COMPOUND.toInt())
        for (item in fluidList.asIterable()) {
            if (item !is CompoundTag) continue

            if (item.contains("Frequency", Tag.TAG_COMPOUND.toInt())) {
                val frequencyItemTag = item.getCompound("Frequency")
                val inventoryTag = item.getCompound("Tank")
                val frequency = StorageFrequency.of(ItemStack.parseOptional(registries, frequencyItemTag))
                val fluidTank = SharedFluidTank(10000, this).apply { readFromNBT(registries, inventoryTag) }

                sharedFluidStorage[frequency] = fluidTank
            } else if (item.contains("StorageFrequency", Tag.TAG_COMPOUND.toInt())) {
                val storageFrequency = StorageFrequency.parseOptional(registries, item.getCompound("StorageFrequency"))
                val fluidTank = SharedFluidTank(10000, this).apply {
                    readFromNBT(registries, item.getCompound("Tank"))
                }

                sharedFluidStorage[storageFrequency] = fluidTank
            }
        }

        return this
    }
}
