package io.github.cotrin8672.cel.content.block.tank

import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.foundation.block.IBE
import com.simibubi.create.foundation.fluid.FluidHelper
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
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

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult,
    ): ItemInteractionResult {
        val be = level.getBlockEntity(pos) as? EnderTankBlockEntity ?: return ItemInteractionResult.FAIL
        val tank = be.getFluidTank() ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        val prev = tank.getFluidInTank(0).copy()

        val didTransfer = when {
            FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be) -> true
            FluidHelper.tryFillItemFromBE(level, player, hand, stack, be) -> true
            else -> false
        }

        if (!didTransfer) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

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

        return ItemInteractionResult.SUCCESS
    }
}
