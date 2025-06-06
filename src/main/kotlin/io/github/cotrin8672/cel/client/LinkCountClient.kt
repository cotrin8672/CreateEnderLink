package io.github.cotrin8672.cel.client

import io.github.cotrin8672.cel.content.block.tank.EnderTankBlockEntity
import io.github.cotrin8672.cel.content.block.vault.EnderVaultBlockEntity
import io.github.cotrin8672.cel.util.StorageFrequency
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.core.HolderLookup
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity

object LinkCountClient {
    private val vaultCounts = mutableMapOf<StorageFrequency, Int>()
    private val tankCounts = mutableMapOf<StorageFrequency, Int>()

    fun update(tag: CompoundTag, registries: HolderLookup.Provider) {
        vaultCounts.clear()
        tankCounts.clear()
        if (tag.contains("vault", Tag.TAG_LIST.toInt())) {
            readList(tag.getList("vault", Tag.TAG_COMPOUND.toInt()), registries) { freq, count ->
                vaultCounts[freq] = count
            }
        }
        if (tag.contains("tank", Tag.TAG_LIST.toInt())) {
            readList(tag.getList("tank", Tag.TAG_COMPOUND.toInt()), registries) { freq, count ->
                tankCounts[freq] = count
            }
        }
    }

    fun getCount(be: SmartBlockEntity, frequency: StorageFrequency): Int {
        return when (be) {
            is EnderVaultBlockEntity -> vaultCounts[frequency] ?: 0
            is EnderTankBlockEntity -> tankCounts[frequency] ?: 0
            else -> 0
        }
    }

    private fun readList(list: ListTag, registries: HolderLookup.Provider, accept: (StorageFrequency, Int) -> Unit) {
        for (element in list) {
            if (element !is CompoundTag) continue
            val freq = StorageFrequency.parseOptional(registries, element.getCompound("StorageFrequency"))
            val count = element.getInt("Count")
            accept(freq, count)
        }
    }
}
