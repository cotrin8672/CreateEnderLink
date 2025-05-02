package io.github.cotrin8672.cel.content.storage

import com.mojang.serialization.Codec
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage
import io.github.cotrin8672.cel.registry.CelMountedStorageTypes
import io.github.cotrin8672.cel.util.SharedStorageHandler
import io.github.cotrin8672.cel.util.StorageFrequency
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

class SharedMountedFluidStorage(
    private val storageFrequency: StorageFrequency,
) : MountedFluidStorage(CelMountedStorageTypes.SHARED_FLUID.get()) {
    companion object {
        val CODEC: Codec<SharedMountedFluidStorage> =
            StorageFrequency.CODEC.xmap(::SharedMountedFluidStorage) { it.storageFrequency }
    }

    private val sharedFluidTank by lazy {
        SharedStorageHandler.instance!!.getOrCreateSharedFluidStorage(storageFrequency)
    }

    override fun getTanks(): Int {
        return sharedFluidTank.tanks
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        return sharedFluidTank.getFluidInTank(tank)
    }

    override fun getTankCapacity(tank: Int): Int {
        return sharedFluidTank.getTankCapacity(tank)
    }

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
        return sharedFluidTank.isFluidValid(tank, stack)
    }

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        return sharedFluidTank.fill(resource, action)
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return sharedFluidTank.drain(resource, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return sharedFluidTank.drain(maxDrain, action)
    }

    override fun unmount(level: Level?, state: BlockState?, pos: BlockPos?, be: BlockEntity?) {}
}
