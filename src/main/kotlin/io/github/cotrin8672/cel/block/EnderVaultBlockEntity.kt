package io.github.cotrin8672.cel.block

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.util.SharedStorageHandler
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
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
        val nonNullLevel = level ?: return null
        if (nonNullLevel is ServerLevel)
            return SharedStorageHandler.instance?.getOrCreateInventory(behaviour.getFrequencyItem())
        return null
    }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(SharedStorageBehaviour(this, CenteredSideValueBoxTransform { state, direction ->
            state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == direction.axis
        }))
        behaviours.add(
            InvManipulationBehaviour.forInsertion(
                this,
                CapManipulationBehaviourBase.InterfaceProvider.oppositeOfBlockFacing()
            )
        )
    }
}
