package io.github.cotrin8672.cel.util

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity

val BlockEntity.blockKey: ResourceKey<Block>
    get() {
        val state = this.level?.getBlockState(this.blockPos)
        val block = state?.block
        return this.level!!
            .registryAccess()
            .registryOrThrow(Registries.BLOCK)
            .getResourceKey(block)
            .orElseThrow { IllegalStateException("Unknown block: $block") }
    }