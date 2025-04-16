package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.world.level.saveddata.SavedData.Factory
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent

@EventBusSubscriber
object CommonEvents {
    @SubscribeEvent
    fun onLevelLoad(event: ServerStartingEvent) {
        val level = event.server.overworld()
        val instance = level.dataStorage.computeIfAbsent(
            Factory(SharedStorageHandler::create, SharedStorageHandler::load),
            "ender_vault_storage"
        )
        SharedStorageHandler.instance = instance
    }

    @SubscribeEvent
    fun onLevelUnload(event: ServerStoppedEvent) {
        SharedStorageHandler.instance = null
    }
}
