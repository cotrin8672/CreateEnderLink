package io.github.cotrin8672.cel.block

import com.mojang.serialization.MapCodec
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency
import io.github.cotrin8672.cel.registry.CelMountedStorageTypes
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class EnderVaultMountedStorage(
    private val frequencyItem: ItemStack,
) : MountedItemStorage(CelMountedStorageTypes.SHARED_ITEM.get()) {
    companion object {
        val CODEC: MapCodec<EnderVaultMountedStorage> =
            ItemStack.CODEC.xmap(::EnderVaultMountedStorage) { it.frequencyItem }.fieldOf("value")
    }

    private val frequency = Frequency.of(frequencyItem)
    private val sharedItemStackHandler by lazy {
        SharedStorageHandler.instance!!.getOrCreateInventory(frequency)
    }

    override fun getSlots(): Int {
        return sharedItemStackHandler.slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return sharedItemStackHandler.getStackInSlot(slot)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return sharedItemStackHandler.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return sharedItemStackHandler.extractItem(slot, amount, simulate)
    }

    override fun getSlotLimit(slot: Int): Int {
        return sharedItemStackHandler.getSlotLimit(slot)
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return sharedItemStackHandler.isItemValid(slot, stack)
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        sharedItemStackHandler.setStackInSlot(slot, stack)
    }

    override fun unmount(level: Level?, state: BlockState?, pos: BlockPos?, be: BlockEntity?) {}
}
