package io.github.cotrin8672.cel

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.cel.datagen.CelDatagen
import io.github.cotrin8672.cel.registry.CelBlocks
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.EventPriority
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(CreateEnderLink.MOD_ID)
class CreateEnderLink(container: ModContainer) {
    companion object {
        const val MOD_ID = "createenderlink"

        val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)

        fun asResource(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
        }
    }

    init {
        REGISTRATE.registerEventListeners(MOD_BUS)
        CelBlocks.register()
        MOD_BUS.addListener(EventPriority.LOWEST, CelDatagen::gatherData)
    }
}
