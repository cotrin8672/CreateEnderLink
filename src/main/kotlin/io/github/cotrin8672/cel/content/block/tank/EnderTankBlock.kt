package io.github.cotrin8672.cel.content.block.tank

import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.foundation.block.IBE
import com.simibubi.create.foundation.fluid.FluidHelper
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.util.CelLang
import io.github.cotrin8672.cel.util.StorageFrequency
import io.github.cotrin8672.cel.util.storageFrequency
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class EnderTankBlock(properties: Properties) : Block(properties), IWrenchable, IBE<EnderTankBlockEntity> {
    override fun getBlockEntityClass(): Class<EnderTankBlockEntity> {
        return EnderTankBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out EnderTankBlockEntity> {
        return CelBlockEntityTypes.ENDER_TANK.get()
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
        if (CelBlocks.ENDER_TANK.isIn(heldItem)) {
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

        val be = level.getBlockEntity(pos) as? EnderTankBlockEntity ?: return InteractionResult.FAIL
        val tank = be.getFluidTank() ?: return InteractionResult.PASS
        val prev = tank.getFluidInTank(0).copy()
        val stack = player.getItemInHand(hand)

        val didTransfer = when {
            FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be) -> true
            FluidHelper.tryFillItemFromBE(level, player, hand, stack, be) -> true
            else -> false
        }

        if (!didTransfer) return InteractionResult.PASS

        if (!level.isClientSide) {
            val current = tank.getFluidInTank(0)
            val sound = when {
                current.amount > prev.amount -> FluidHelper.getEmptySound(current)
                current.amount < prev.amount -> FluidHelper.getFillSound(prev)
                else -> null
            }

            sound?.let {
                level.playSound(null, pos, it, SoundSource.BLOCKS, 0.5f, 1f)
            }
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
