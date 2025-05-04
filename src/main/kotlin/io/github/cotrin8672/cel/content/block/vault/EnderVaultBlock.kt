package io.github.cotrin8672.cel.content.block.vault

import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.foundation.block.IBE
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.util.CelLang
import io.github.cotrin8672.cel.util.StorageFrequency
import io.github.cotrin8672.cel.util.storageFrequency
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult

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

    @Deprecated("Deprecated in Java")
    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        val axis = state.getValue(HORIZONTAL_AXIS)
        val rotatedDir = rot.rotate(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE))
        return state.setValue(HORIZONTAL_AXIS, rotatedDir.axis)
    }

    @Deprecated("Deprecated in Java")
    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return state
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(HORIZONTAL_AXIS, context.horizontalDirection.axis)
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult,
    ): InteractionResult {
        val heldItem = player.getItemInHand(hand)
        if (CelBlocks.ENDER_VAULT.isIn(heldItem)) {
            withBlockEntityDo(level, pos) {
                val storageFrequency = it.getBehaviour(SharedStorageBehaviour.TYPE).getFrequency()
                if (storageFrequency.isEmpty)
                    heldItem.storageFrequency = StorageFrequency.EMPTY
                else
                    heldItem.storageFrequency = storageFrequency
            }
            if (player is ServerPlayer)
                player.displayClientMessage(
                    CelLang.translate("storage_frequency.set").component(),
                    true
                )
            return InteractionResult.SUCCESS
        }

        return InteractionResult.SUCCESS
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
        val storageFrequency = stack.storageFrequency

        if (storageFrequency.isNotEmpty) {
            withBlockEntityDo(level, pos) {
                val behaviour = it.getBehaviour(SharedStorageBehaviour.TYPE)
                behaviour.setStorageFrequency(storageFrequency)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(
        state: BlockState,
        worldIn: Level,
        pos: BlockPos,
        newState: BlockState,
        isMoving: Boolean,
    ) {
        IBE.onRemove(state, worldIn, pos, newState)
    }
}
