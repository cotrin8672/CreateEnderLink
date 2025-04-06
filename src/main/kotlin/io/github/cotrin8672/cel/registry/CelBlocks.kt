package io.github.cotrin8672.cel.registry

import com.simibubi.create.AllCreativeModeTabs
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.pickaxeOnly
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.cel.CreateEnderLink.Companion.REGISTRATE
import io.github.cotrin8672.cel.block.EnderVaultBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor

object CelBlocks {
    init {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB)
    }

    val ENDER_CHEST: BlockEntry<EnderVaultBlock> = REGISTRATE
        .block<EnderVaultBlock>("ender_vault", ::EnderVaultBlock)
        .initialProperties(SharedProperties::softMetal)
        .properties {
            it
                .mapColor(MapColor.STONE)
                .sound(SoundType.NETHERITE_BLOCK)
                .explosionResistance(1200f)
        }
        .transform(pickaxeOnly())
        .blockstate(BlockStateGen.horizontalAxisBlockProvider(false))
        .item()
        .build()
        .register()

    fun register() {}
}
