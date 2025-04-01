package io.github.cotrin8672.cel

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(value = CreateEnderLink.MOD_ID, dist = [Dist.CLIENT])
class CreateEnderLinkClient {
    init {
        MOD_BUS.addListener<FMLClientSetupEvent> {
        }
    }
}
