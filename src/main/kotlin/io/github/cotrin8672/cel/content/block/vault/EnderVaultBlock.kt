package io.github.cotrin8672.cel.content.block.vault

import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.foundation.block.IBE
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty

class EnderVaultBlock(properties: Properties) : Block(properties), IWrenchable, IBE<EnderVaultBlockEntity> {
    companion object {
        val HORIZONTAL_AXIS: EnumProperty<Direction.Axis> = BlockStateProperties.HORIZONTAL_AXIS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder.add(HORIZONTAL_AXIS))
    }

    override fun getBlockEntityClass(): Class<EnderVaultBlockEntity> {
        return EnderVaultBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out EnderVaultBlockEntity> {
        return CelBlockEntityTypes.ENDER_VAULT.get()
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        val axis = state.getValue(HORIZONTAL_AXIS)
        val rotatedDir = rot.rotate(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE))
        return state.setValue(HORIZONTAL_AXIS, rotatedDir.axis)
    }

    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return state
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(HORIZONTAL_AXIS, context.horizontalDirection.axis)
    }
}
