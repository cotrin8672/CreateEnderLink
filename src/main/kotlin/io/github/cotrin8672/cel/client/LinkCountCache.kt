package io.github.cotrin8672.cel.client

import io.github.cotrin8672.cel.util.StorageFrequency
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

/**
 * Client side cache for storing the latest link counts per frequency
 * sent from the server.
 */
object LinkCountCache {
    private val vaultCounts: MutableMap<StorageFrequency, Int> = mutableMapOf()
    private val tankCounts: MutableMap<StorageFrequency, Int> = mutableMapOf()

    fun handlePacket(tag: CompoundTag, provider: HolderLookup.Provider) {
        vaultCounts.clear()
        tankCounts.clear()
        readList(tag.getList("vaults", Tag.TAG_COMPOUND.toInt()), provider, vaultCounts)
        readList(tag.getList("tanks", Tag.TAG_COMPOUND.toInt()), provider, tankCounts)
    }

    fun getVaultCount(freq: StorageFrequency): Int = vaultCounts[freq] ?: 0
    fun getTankCount(freq: StorageFrequency): Int = tankCounts[freq] ?: 0

    private fun readList(list: ListTag, provider: HolderLookup.Provider, map: MutableMap<StorageFrequency, Int>) {
        for (element in list) {
            if (element !is CompoundTag) continue
            val freq = StorageFrequency.parseOptional(provider, element.getCompound("StorageFrequency"))
            val count = element.getInt("Count")
            map[freq] = count
        }
    }
}
