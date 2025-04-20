package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object CommonEvents {
    @SubscribeEvent
    fun onLevelLoad(event: ServerStartingEvent) {
        val level = event.server.overworld()
        val instance = level.dataStorage.computeIfAbsent(
            SharedStorageHandler::load,
            SharedStorageHandler::create,
            "ender_vault_storage"
        )
        SharedStorageHandler.instance = instance
    }

    @SubscribeEvent
    fun onLevelUnload(event: ServerStoppedEvent) {
        SharedStorageHandler.instance = null
    }

    @SubscribeEvent
    fun onTickEvent(event: TickEvent.LevelTickEvent) {
        SharedStorageHandler.instance?.tick()
    }
}
