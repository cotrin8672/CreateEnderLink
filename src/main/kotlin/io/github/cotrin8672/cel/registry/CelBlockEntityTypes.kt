package io.github.cotrin8672.cel.registry

import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import io.github.cotrin8672.cel.content.block.tank.EnderTankBlockEntity
import io.github.cotrin8672.cel.content.block.tank.EnderTankRenderer
import io.github.cotrin8672.cel.content.block.vault.EnderVaultBlockEntity
import io.github.cotrin8672.cel.content.block.vault.EnderVaultRenderer

object CelBlockEntityTypes {
    val ENDER_VAULT: BlockEntityEntry<EnderVaultBlockEntity> = REGISTRATE
        .blockEntity<EnderVaultBlockEntity>("ender_vault", ::EnderVaultBlockEntity)
        .validBlocks(CelBlocks.ENDER_VAULT)
        .renderer { NonNullFunction(::EnderVaultRenderer) }
        .register()

    val ENDER_TANK: BlockEntityEntry<EnderTankBlockEntity> = REGISTRATE
        .blockEntity<EnderTankBlockEntity>("ender_tank", ::EnderTankBlockEntity)
        .validBlocks(CelBlocks.ENDER_TANK)
        .renderer { NonNullFunction(::EnderTankRenderer) }
        .register()

    fun register() {}
}
