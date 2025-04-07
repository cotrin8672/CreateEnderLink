package io.github.cotrin8672.cel.util

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency
import net.createmod.catnip.data.WorldAttached
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler

object SharedStorageHandler {
    private val sharedInventories = WorldAttached { mutableMapOf<Frequency, IItemHandler>() }

    fun getOrCreateInventory(level: Level, frequency: Frequency): IItemHandler {
        return sharedInventories.get(level).computeIfAbsent(frequency) {
            ItemStackHandler(27)
        }
    }
}
