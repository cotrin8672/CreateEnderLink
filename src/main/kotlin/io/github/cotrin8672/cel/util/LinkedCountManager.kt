package io.github.cotrin8672.cel.util

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object LinkedCountManager {
    private val blockEntityInstances: MutableMap<ResourceKey<out Block>, MutableSet<SmartBlockEntity>> =
        ConcurrentHashMap()
    private val linkCache: MutableMap<ResourceKey<out Block>, MutableMap<StorageFrequency, Int>> = ConcurrentHashMap()

    fun registerLinkableBlock(block: BlockEntry<out Block>) {
        blockEntityInstances[block.key] = Collections.newSetFromMap(WeakHashMap())
    }

    fun clearAll() {
        blockEntityInstances.clear()
        linkCache.clear()
    }

    fun addLinkedBlock(key: ResourceKey<out Block>, be: SmartBlockEntity) {
        blockEntityInstances[key]?.add(be)
    }

    fun removeLinkedBlock(key: ResourceKey<out Block>, be: SmartBlockEntity) {
        blockEntityInstances[key]?.remove(be)
    }

    fun getLinkedCount(key: ResourceKey<out Block>, storageFrequency: StorageFrequency): Int {
        val innerMap = linkCache[key]
        return innerMap?.get(storageFrequency) ?: 0
    }

    fun syncFullLinkedCount(allCounts: Map<ResourceKey<out Block>, Map<StorageFrequency, Int>>) {
        linkCache.clear()
        linkCache.putAll(allCounts.mapValues { it.value.toMutableMap() })
    }

    fun getFullLinkedCount(): Map<ResourceKey<out Block>, Map<StorageFrequency, Int>> {
        return blockEntityInstances.mapValues { entry ->
            val freqList: List<StorageFrequency> = entry.value.map { be ->
                be.getBehaviour(SharedStorageBehaviour.TYPE).getFrequency()
            }
            freqList.groupingBy { it }.eachCount()
        }
    }
}
