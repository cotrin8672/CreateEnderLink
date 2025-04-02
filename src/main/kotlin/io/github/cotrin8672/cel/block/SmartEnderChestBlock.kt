package io.github.cotrin8672.cel.block

import com.mojang.serialization.MapCodec
import com.simibubi.create.content.equipment.wrench.IWrenchable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class SmartEnderChestBlock(properties: Properties) : HorizontalDirectionalBlock(properties), IWrenchable {
    companion object {
        val CODEC: MapCodec<SmartEnderChestBlock> = simpleCodec(::SmartEnderChestBlock)
    }

    override fun codec(): MapCodec<out HorizontalDirectionalBlock> {
        return CODEC
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.WATERLOGGED).add(FACING))
    }
}
