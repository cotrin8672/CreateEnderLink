package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.item.ScopeFilterItemDecorator
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.registry.CelItems
import net.minecraftforge.client.event.RegisterItemDecorationsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CreateEnderLink.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ClientEvents {
    @SubscribeEvent
    fun onRegisterItemDecoration(event: RegisterItemDecorationsEvent) {
        event.register(CelItems.SCOPE_FILTER, ScopeFilterItemDecorator)
        event.register(CelBlocks.ENDER_VAULT, SharedStorageBehaviour.DECORATOR)
        event.register(CelBlocks.ENDER_TANK, SharedStorageBehaviour.DECORATOR)
    }
}
