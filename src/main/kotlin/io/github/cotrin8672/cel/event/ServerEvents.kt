package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.network.SyncFullLinkPacket
import io.github.cotrin8672.cel.network.SyncSharedStoragePacket
import io.github.cotrin8672.cel.util.LinkCountManager.linkedList
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber
object ServerEvents {
    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity
        if (player !is ServerPlayer) return
        val level = player.serverLevel()
        val nbt = SharedStorageHandler.instance?.save(CompoundTag(), level.registryAccess()) ?: return
        PacketDistributor.sendToPlayer(player, SyncSharedStoragePacket(nbt))
        PacketDistributor.sendToPlayer(player, SyncFullLinkPacket(linkedList))
    }
}
