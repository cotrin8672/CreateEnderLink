package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData.Factory
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.LevelEvent

@EventBusSubscriber
object CommonEvents {
    @SubscribeEvent
    fun onLevelLoad(event: LevelEvent.Load) {
        val level = event.level
        if (level is ServerLevel) {
            val instance = level.dataStorage.computeIfAbsent(
                Factory(SharedStorageHandler::create, SharedStorageHandler::load),
                "ender_vault_storage"
            )
            SharedStorageHandler.instance = instance
        }
    }

    @SubscribeEvent
    fun onLevelUnload(event: LevelEvent.Unload) {
        SharedStorageHandler.instance = null
    }
}
