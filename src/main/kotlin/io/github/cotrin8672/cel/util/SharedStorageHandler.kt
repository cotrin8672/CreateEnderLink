package io.github.cotrin8672.cel.util

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency
import io.github.cotrin8672.cel.content.storage.SharedFluidTank
import io.github.cotrin8672.cel.content.storage.SharedItemStackHandler
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.saveddata.SavedData

class SharedStorageHandler : SavedData() {
    private val sharedItemStorage = mutableMapOf<Frequency, SharedItemStackHandler>()
    private val sharedFluidStorage = mutableMapOf<Frequency, SharedFluidTank>()

    companion object {
        fun create() = SharedStorageHandler()

        fun load(tag: CompoundTag, registries: Provider) = create().load(tag, registries)

        var instance: SharedStorageHandler? = null
    }

    fun getOrCreateSharedItemStorage(frequency: Frequency): SharedItemStackHandler {
        return sharedItemStorage.computeIfAbsent(frequency) {
            SharedItemStackHandler(27, this)
        }
    }

    fun getOrCreateSharedFluidStorage(frequency: Frequency): SharedFluidTank {
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
                put("Frequency", frequency.stack.saveOptional(registries))
                sharedItemStorage[frequency]?.serializeNBT(registries)?.let { put("Inventory", it) }
            }
            listTag.add(pairTag)
        }
        tag.put("SharedStorage", listTag)

        val fluidFrequencies = sharedFluidStorage.keys
        val fluidListTag = ListTag()
        for (fluidFrequency in fluidFrequencies) {
            val pairTag = CompoundTag().apply {
                put("Frequency", fluidFrequency.stack.saveOptional(registries))
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
            val frequencyTag = item.getCompound("Frequency") ?: continue
            val inventoryTag = item.getCompound("Inventory") ?: continue

            val frequency = Frequency.of(ItemStack.parseOptional(registries, frequencyTag))
            val inventory = SharedItemStackHandler(27, this).apply { deserializeNBT(registries, inventoryTag) }

            sharedItemStorage[frequency] = inventory
        }

        val fluidList = tag.getList("SharedFluidStorage", Tag.TAG_COMPOUND.toInt())
        for (fluidItem in fluidList.asIterable()) {
            if (fluidItem !is CompoundTag) continue
            val frequencyTag = fluidItem.getCompound("Frequency") ?: continue
            val fluidTankTag = fluidItem.getCompound("Tank") ?: continue

            val frequency = Frequency.of(ItemStack.parseOptional(registries, frequencyTag))
            val fluidTank = SharedFluidTank(10000, this).apply { readFromNBT(registries, fluidTankTag) }

            sharedFluidStorage[frequency] = fluidTank
        }

        return this
    }
}
