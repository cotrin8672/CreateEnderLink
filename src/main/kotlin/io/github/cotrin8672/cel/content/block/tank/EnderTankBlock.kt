package io.github.cotrin8672.cel.content.block.tank

import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.foundation.block.IBE
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType

class EnderTankBlock(properties: Properties) : Block(properties), IWrenchable, IBE<EnderTankBlockEntity> {
    override fun getBlockEntityClass(): Class<EnderTankBlockEntity> {
        return EnderTankBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out EnderTankBlockEntity> {
        return CelBlockEntityTypes.ENDER_TANK.get()
    }
}
