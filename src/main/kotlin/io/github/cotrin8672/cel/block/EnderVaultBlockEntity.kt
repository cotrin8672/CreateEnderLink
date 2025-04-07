package io.github.cotrin8672.cel.block

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.IItemHandler

class EnderVaultBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SmartBlockEntity(type, pos, state) {
    companion object {
        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CelBlockEntityTypes.ENDER_VAULT.get()
            ) { be, _ ->
                return@registerBlockEntity be.getInventory()
            }
        }
    }

    fun getInventory(): IItemHandler? {
        val behaviour = getBehaviour(SharedStorageBehaviour.TYPE) ?: return null
        return SharedStorageHandler.getOrCreateInventory(level!!, behaviour.getFrequencyItem())
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedStorageBehaviour(this, CenteredSideValueBoxTransform { state, direction ->
            state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == direction.axis
        }))
    }
}
