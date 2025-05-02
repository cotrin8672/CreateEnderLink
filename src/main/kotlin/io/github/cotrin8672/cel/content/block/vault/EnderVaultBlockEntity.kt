package io.github.cotrin8672.cel.content.block.vault

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import java.util.*

class EnderVaultBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SmartBlockEntity(type, pos, state), IHaveGoggleInformation {
    companion object {
        private val blockEntities: MutableSet<EnderVaultBlockEntity> = Collections.newSetFromMap(WeakHashMap())
    }

    init {
        val isAlreadyExists = blockEntities.map { it.blockPos }.contains(this.blockPos)
        if (!isAlreadyExists) blockEntities.add(this)
    }

    private fun getInventory(): IItemHandler? {
        val behaviour = getBehaviour(SharedStorageBehaviour.TYPE) ?: return null
        val nonNullLevel = level ?: return null
        if (nonNullLevel is ServerLevel) {
            val inventory = SharedStorageHandler.instance?.getOrCreateSharedItemStorage(behaviour.getFrequency())
            return inventory
        }
        return null
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        val inventory = getInventory() ?: return super.getCapability(cap, side)
        return if (cap == ForgeCapabilities.ITEM_HANDLER)
            LazyOptional.of<IItemHandler> { inventory }.cast()
        else super.getCapability(cap, side)
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedStorageBehaviour(this, CenteredSideValueBoxTransform { state, direction ->
            state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == direction.axis
        }))
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        getBehaviour(SharedStorageBehaviour.TYPE).addToGoggleTooltip(tooltip, isPlayerSneaking, blockEntities)

        return true
    }

    override fun destroy() {
        super.destroy()
        blockEntities.remove(this)
    }

    override fun remove() {
        super.remove()
        blockEntities.remove(this)
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        blockEntities.remove(this)
    }
}
