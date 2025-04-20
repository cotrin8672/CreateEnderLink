package io.github.cotrin8672.cel

import com.simibubi.create.foundation.data.CreateRegistrate
import io.github.cotrin8672.cel.content.ponder.CelPonderPlugin
import io.github.cotrin8672.cel.datagen.CelDatagen
import io.github.cotrin8672.cel.registrate.KotlinRegistrate
import io.github.cotrin8672.cel.registry.CelBlockEntityTypes
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.registry.CelCreativeModeTabs
import net.createmod.ponder.foundation.PonderIndex
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod.EventBusSubscriber(modid = CreateEnderLink.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(CreateEnderLink.MOD_ID)
object CreateEnderLink {
    const val MOD_ID = "createenderlink"

    val REGISTRATE: CreateRegistrate = KotlinRegistrate.create(MOD_ID)
        .defaultCreativeTab(CelCreativeModeTabs.CEL_CREATIVE_TAB.key)

    fun asResource(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }

    init {
        REGISTRATE.registerEventListeners(MOD_BUS)
        CelCreativeModeTabs.register(MOD_BUS)
        CelBlocks.register()
        CelBlockEntityTypes.register()
        MOD_BUS.addListener(EventPriority.LOWEST, CelDatagen::gatherData)
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
            Runnable { PonderIndex.addPlugin(CelPonderPlugin) }
        }
    }
}
