package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.network.CelNetworking
import io.github.cotrin8672.cel.network.SyncSharedStoragePacket
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.network.PacketDistributor

@EventBusSubscriber(modid = CreateEnderLink.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
object ServerEvents {
    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        if (event.entity !is ServerPlayer) return
        val nbt = SharedStorageHandler.instance?.save(CompoundTag()) ?: return
        CelNetworking.CHANNEL.send(
            PacketDistributor.PLAYER.with { event.entity as ServerPlayer },
            SyncSharedStoragePacket(nbt)
        )
    }
}
