package io.github.cotrin8672.cel.util

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.block.tank.EnderTankBlockEntity
import io.github.cotrin8672.cel.content.block.vault.EnderVaultBlockEntity
import io.github.cotrin8672.cel.network.SyncLinkCountPacket
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import java.util.Collections
import java.util.WeakHashMap

/**
 * Tracks loaded Ender Vaults and Ender Tanks on the server and synchronises the
 * amount of loaded block entities per frequency to connected clients.
 */
object LinkCountTracker {
    private val vaults: MutableSet<EnderVaultBlockEntity> = Collections.newSetFromMap(WeakHashMap())
    private val tanks: MutableSet<EnderTankBlockEntity> = Collections.newSetFromMap(WeakHashMap())

    private var dirty = false
    private var lastVault: Map<StorageFrequency, Int> = emptyMap()
    private var lastTank: Map<StorageFrequency, Int> = emptyMap()

    fun track(be: EnderVaultBlockEntity) {
        if (be.level !is ServerLevel) return
        vaults.add(be)
        dirty = true
    }

    fun untrack(be: EnderVaultBlockEntity) {
        if (be.level !is ServerLevel) return
        vaults.remove(be)
        dirty = true
    }

    fun track(be: EnderTankBlockEntity) {
        if (be.level !is ServerLevel) return
        tanks.add(be)
        dirty = true
    }

    fun untrack(be: EnderTankBlockEntity) {
        if (be.level !is ServerLevel) return
        tanks.remove(be)
        dirty = true
    }

    fun tick(level: ServerLevel) {
        if (!dirty) return
        dirty = false

        val vaultCounts = computeCounts(vaults)
        val tankCounts = computeCounts(tanks)
        if (vaultCounts != lastVault || tankCounts != lastTank) {
            lastVault = vaultCounts
            lastTank = tankCounts
            val tag = buildTag(level.registryAccess(), vaultCounts, tankCounts)
            PacketDistributor.sendToAllPlayers(SyncLinkCountPacket(tag))
        }
    }

    fun sendToPlayer(player: ServerPlayer) {
        val level = player.serverLevel()
        val tag = buildTag(level.registryAccess(), computeCounts(vaults), computeCounts(tanks))
        PacketDistributor.sendToPlayer(player, SyncLinkCountPacket(tag))
    }

    private fun computeCounts(set: Set<out SmartBlockEntity>): Map<StorageFrequency, Int> {
        val map = mutableMapOf<StorageFrequency, Int>()
        for (be in set) {
            val behaviour = be.getBehaviour(SharedStorageBehaviour.TYPE) ?: continue
            val freq = behaviour.getFrequency()
            map[freq] = (map[freq] ?: 0) + 1
        }
        return map
    }

    private fun buildTag(
        provider: HolderLookup.Provider,
        vault: Map<StorageFrequency, Int>,
        tank: Map<StorageFrequency, Int>
    ): CompoundTag {
        val tag = CompoundTag()
        tag.put("vaults", countsToList(vault, provider))
        tag.put("tanks", countsToList(tank, provider))
        return tag
    }

    private fun countsToList(counts: Map<StorageFrequency, Int>, provider: HolderLookup.Provider): ListTag {
        val list = ListTag()
        for ((freq, count) in counts) {
            val t = CompoundTag()
            t.put("StorageFrequency", freq.saveOptional(provider))
            t.putInt("Count", count)
            list.add(t)
        }
        return list
    }
}
