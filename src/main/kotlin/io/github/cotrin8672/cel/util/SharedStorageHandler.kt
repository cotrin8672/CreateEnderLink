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
        val frequencies = sharedItemStorage.keys
        val listTag = ListTag()
        for (frequency in frequencies) {
            val pairTag = CompoundTag().apply {
                put("FrequencyItem", frequency.stack.saveOptional(registries))
                putString("FrequencyOwner", frequency.playerUuid.toString())
                sharedItemStorage[frequency]?.serializeNBT(registries)?.let { put("Inventory", it) }
            }
            listTag.add(pairTag)
        }
        tag.put("SharedStorage", listTag)

        val fluidFrequencies = sharedFluidStorage.keys
        val fluidListTag = ListTag()
        for (fluidFrequency in fluidFrequencies) {
            val pairTag = CompoundTag().apply {
                put("FrequencyItem", fluidFrequency.stack.saveOptional(registries))
                putString("FrequencyOwner", fluidFrequency.playerUuid.toString())
                sharedFluidStorage[fluidFrequency]?.writeToNBT(registries, CompoundTag())?.let { put("Tank", it) }
            }
            fluidListTag.add(pairTag)
        }
        tag.put("SharedFluidStorage", fluidListTag)

        return tag
    }

    fun load(tag: CompoundTag, registries: Provider): SharedStorageHandler {
        val list = tag.getList("SharedStorage", Tag.TAG_COMPOUND.toInt())
        for (item in list.asIterable()) {
            if (item !is CompoundTag) continue

            val frequencyItemTag = when {
                item.contains("Frequency", Tag.TAG_COMPOUND.toInt()) -> item.getCompound("Frequency")
                item.contains("FrequencyItem", Tag.TAG_COMPOUND.toInt()) -> item.getCompound("FrequencyItem")
                else -> continue
            }
            val frequencyUUID = item.getString("FrequencyOwner")
            val inventoryTag = item.getCompound("Inventory")

            val frequency = StorageFrequency.of(ItemStack.parseOptional(registries, frequencyItemTag), frequencyUUID)
            val inventory = SharedItemStackHandler(27, this).apply { deserializeNBT(registries, inventoryTag) }

            sharedItemStorage[frequency] = inventory
        }

        val fluidList = tag.getList("SharedFluidStorage", Tag.TAG_COMPOUND.toInt())
        for (fluidItem in fluidList.asIterable()) {
            if (fluidItem !is CompoundTag) continue

            val frequencyItemTag = when {
                fluidItem.contains("Frequency", Tag.TAG_COMPOUND.toInt()) -> fluidItem.getCompound("Frequency")
                fluidItem.contains("FrequencyItem", Tag.TAG_COMPOUND.toInt()) -> fluidItem.getCompound("FrequencyItem")
                else -> continue
            }
            val frequencyUUID = fluidItem.getString("FrequencyOwner")
            val fluidTankTag = fluidItem.getCompound("Tank")

            val frequency = StorageFrequency.of(ItemStack.parseOptional(registries, frequencyItemTag), frequencyUUID)
            val fluidTank = SharedFluidTank(10000, this).apply { readFromNBT(registries, fluidTankTag) }

            sharedFluidStorage[frequency] = fluidTank
        }

        return this
    }
}
