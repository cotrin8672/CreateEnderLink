package io.github.cotrin8672.cel.block

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

class FrequencyBehaviour(
    private val blockEntity: SmartBlockEntity,
) : BlockEntityBehaviour(blockEntity) {
    companion object {
        val TYPE = BehaviourType<FrequencyBehaviour>()
    }

    private val valueBox = CenteredSideValueBoxTransform { state, direction ->
        state.getValue(BlockStateProperties.HORIZONTAL_FACING) == direction
    }
    private val direction: Direction
        get() = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

    override fun getType(): BehaviourType<*> {
        return TYPE
    }

    fun testHit(hit: Vec3): Boolean {
        return valueBox.testHit(blockEntity.level, blockEntity.blockPos, blockEntity.blockState, hit)
    }
}
