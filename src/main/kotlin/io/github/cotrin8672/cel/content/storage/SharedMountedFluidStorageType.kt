package io.github.cotrin8672.cel.content.storage

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class SharedMountedFluidStorageType :
    MountedFluidStorageType<SharedMountedFluidStorage>(SharedMountedFluidStorage.CODEC) {
    override fun mount(
        level: Level?,
        state: BlockState?,
        pos: BlockPos?,
        be: BlockEntity?,
    ): SharedMountedFluidStorage? {
        if (level !is ServerLevel) return null
        if (be !is SmartBlockEntity) return null
        val behaviour = be.getBehaviour(SharedStorageBehaviour.TYPE) ?: return null

        val storageFrequency = behaviour.getFrequency()

        return SharedMountedFluidStorage(storageFrequency)
    }
}
