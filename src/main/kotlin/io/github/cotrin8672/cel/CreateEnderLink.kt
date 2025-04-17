package io.github.cotrin8672.cel

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.cel.content.block.tank.EnderTankBlockEntity
import io.github.cotrin8672.cel.content.block.vault.EnderVaultBlockEntity
import io.github.cotrin8672.cel.datagen.CelDatagen
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.registry.CelBlocks
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@EventBusSubscriber(modid = CreateEnderLink.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
@Mod(CreateEnderLink.MOD_ID)
object CreateEnderLink {
    const val MOD_ID = "createenderlink"

    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)

    fun asResource(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }

    init {
        REGISTRATE.registerEventListeners(MOD_BUS)
        CelBlocks.register()
        CelBlockEntityTypes.register()
        MOD_BUS.addListener(EventPriority.LOWEST, CelDatagen::gatherData)
    }

    @SubscribeEvent
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        EnderVaultBlockEntity.registerCapabilities(event)
        EnderTankBlockEntity.registerCapability(event)
    }
}
