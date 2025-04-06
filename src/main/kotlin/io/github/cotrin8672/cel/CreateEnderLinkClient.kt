package io.github.cotrin8672.cel

import io.github.cotrin8672.cel.client.FrequencyRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@EventBusSubscriber(Dist.CLIENT)
@Mod(value = CreateEnderLink.MOD_ID, dist = [Dist.CLIENT])
object CreateEnderLinkClient {
    init {
        MOD_BUS.addListener<FMLClientSetupEvent> {}
    }

    @SubscribeEvent
    fun onTickPre(event: ClientTickEvent.Pre) {
        FrequencyRenderer.tick()
    }

    @SubscribeEvent
    fun onTickPost(event: ClientTickEvent.Post) {
        FrequencyRenderer.tick()
    }
}
