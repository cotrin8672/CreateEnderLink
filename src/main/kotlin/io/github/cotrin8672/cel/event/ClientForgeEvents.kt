package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.client.FrequencyRenderer
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CreateEnderLink.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ClientForgeEvents {
    @SubscribeEvent
    fun onTickPre(event: TickEvent.ClientTickEvent) {
        FrequencyRenderer.tick()
    }
}