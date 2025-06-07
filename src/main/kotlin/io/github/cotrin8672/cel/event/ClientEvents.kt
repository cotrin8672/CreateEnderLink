package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.content.SharedStorageBehaviour
import io.github.cotrin8672.cel.content.item.ScopeFilterItemDecorator
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.registry.CelItems
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent

@EventBusSubscriber(Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
object ClientEvents {
    @SubscribeEvent
    fun onRegisterItemDecoration(event: RegisterItemDecorationsEvent) {
        event.register(CelItems.SCOPE_FILTER, ScopeFilterItemDecorator)
        event.register(CelBlocks.ENDER_VAULT, SharedStorageBehaviour.DECORATOR)
        event.register(CelBlocks.ENDER_TANK, SharedStorageBehaviour.DECORATOR)
    }
}
