package io.github.cotrin8672.cel.util

import net.minecraft.world.level.block.entity.BlockEntity
import io.github.cotrin8672.cel.util.StorageFrequency

import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.block.tank.EnderTankBlockEntity
import io.github.cotrin8672.cel.content.block.vault.EnderVaultBlockEntity
import io.github.cotrin8672.cel.network.SyncFrequencyCountPacket
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerPlayer
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.network.PacketDistributor

object LinkCountManager {
    fun sync(level: ServerLevel) {
        val tag = CompoundTag()
        tag.put("vault", createList(EnderVaultBlockEntity.getLoadingBlockEntities(), level))
        tag.put("tank", createList(EnderTankBlockEntity.getLoadingBlockEntities(), level))
        PacketDistributor.sendToAllPlayers(SyncFrequencyCountPacket(tag))
    }

    fun sync(player: net.minecraft.server.level.ServerPlayer) {
        val level = player.serverLevel()
        val tag = CompoundTag()
        tag.put("vault", createList(EnderVaultBlockEntity.getLoadingBlockEntities(), level))
        tag.put("tank", createList(EnderTankBlockEntity.getLoadingBlockEntities(), level))
        PacketDistributor.sendToPlayer(player, SyncFrequencyCountPacket(tag))
    }

    private fun createList(blockEntities: Set<out BlockEntity>, level: ServerLevel): ListTag {
        val counts = mutableMapOf<StorageFrequency, Int>()
        for (be in blockEntities) {
            val behaviour = (be as? SmartBlockEntity)?.getBehaviour(SharedStorageBehaviour.TYPE) ?: continue
            val freq = behaviour.getFrequency()
            counts[freq] = (counts[freq] ?: 0) + 1
        }
        val list = ListTag()
        for ((freq, count) in counts) {
            val ct = CompoundTag()
            ct.put("StorageFrequency", freq.saveOptional(level.registryAccess()))
            ct.putInt("Count", count)
            list.add(ct)
        }
        return list
    }
}
