package io.github.cotrin8672.cel.registry

import com.simibubi.create.AllCreativeModeTabs
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType.mountedItemStorage
import com.simibubi.create.foundation.data.AssetLookup
import com.simibubi.create.foundation.data.BlockStateGen
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.pickaxeOnly
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import io.github.cotrin8672.cel.content.block.tank.EnderTankBlock
import io.github.cotrin8672.cel.content.block.vault.EnderVaultBlock
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import java.util.function.Supplier

object CelBlocks {
    init {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB)
    }

    val ENDER_VAULT: BlockEntry<EnderVaultBlock> = REGISTRATE
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
        .transform(mountedItemStorage(CelMountedStorageTypes.SHARED_ITEM))
        .item()
        .build()
        .register()

    val ENDER_TANK: BlockEntry<EnderTankBlock> = REGISTRATE
        .block<EnderTankBlock>("ender_tank", ::EnderTankBlock)
        .initialProperties(SharedProperties::copperMetal)
        .properties {
            it.noOcclusion()
                .isRedstoneConductor { _, _, _ -> true }
        }
        .transform(pickaxeOnly())
        .blockstate { c, p -> p.simpleBlock(c.get(), AssetLookup.standardModel(c, p)) }
        .addLayer { Supplier(RenderType::cutoutMipped) }
        .item()
        .build()
        .register()

    fun register() {}
}
