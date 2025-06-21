package io.github.cotrin8672.cel.util

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.model.LinkedCount
import io.github.cotrin8672.cel.model.StorageFrequency
import io.github.cotrin8672.cel.network.SyncFullLinkPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.network.PacketDistributor
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

object LinkCountManager {
    private val serverEntities: ConcurrentHashMap<
            ResourceKey<Block>,
            MutableList<WeakReference<SmartBlockEntity>>> = ConcurrentHashMap()

    val linkedList: List<LinkedCount>
        get() = serverEntities.map {
            LinkedCount(
                it.key,
                it.value
                    .mapNotNull { ref -> ref.get() }
                    .groupingBy { be -> be.getBehaviour(SharedStorageBehaviour.TYPE).getFrequency() }
                    .eachCount()
            )
        }

    fun registerEntity(key: ResourceKey<Block>?, entity: SmartBlockEntity) {
        key?.let {
            serverEntities.compute(it) { _, list ->
                val newList = list ?: mutableListOf()
                newList.add(WeakReference(entity))
                newList
            }
        }
        PacketDistributor.sendToAllPlayers(SyncFullLinkPacket(linkedList))
    }

    fun unregisterEntity(key: ResourceKey<Block>?, entity: SmartBlockEntity) {
        serverEntities[key]?.let { list ->
            list.removeAll { ref -> ref.get() == entity || ref.get() == null }
            if (list.isEmpty()) serverEntities.remove(key)
            PacketDistributor.sendToAllPlayers(SyncFullLinkPacket(linkedList))
        }
    }

    fun getLoadingBlockEntities(key: ResourceKey<Block>): List<SmartBlockEntity> {
        return serverEntities.getOrDefault(key, listOf()).mapNotNull(WeakReference<SmartBlockEntity>::get)
    }

    private val clientCache: MutableList<LinkedCount> = mutableListOf()

    fun onClientPacketReceived(newCounts: List<LinkedCount>) {
        synchronized(clientCache) {
            clientCache.clear()
            clientCache.addAll(newCounts)
        }
    }

    fun getLinkedCount(key: ResourceKey<Block>, storageFrequency: StorageFrequency): Int {
        return clientCache.find { it.blockKey == key }?.linkedList?.get(storageFrequency) ?: -1
    }
}
