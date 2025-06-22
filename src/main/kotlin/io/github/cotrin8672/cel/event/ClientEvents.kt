package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.content.item.ScopeFilterItemDecorator
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.registry.CelDataComponents
import io.github.cotrin8672.cel.registry.CelItems
import io.github.cotrin8672.cel.util.use
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.IItemDecorator
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent

@EventBusSubscriber(Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
object ClientEvents {
    @SubscribeEvent
    fun onRegisterItemDecoration(event: RegisterItemDecorationsEvent) {
        event.register(CelItems.SCOPE_FILTER, ScopeFilterItemDecorator)
        event.register(CelBlocks.ENDER_VAULT, DECORATOR)
        event.register(CelBlocks.ENDER_TANK, DECORATOR)
    }

    val DECORATOR = IItemDecorator { guiGraphics: GuiGraphics, font: Font, stack: ItemStack, x: Int, y: Int ->
        val storageFrequency = stack.get(CelDataComponents.STORAGE_FREQUENCY)
            ?: return@IItemDecorator false
        val frequencyItem = storageFrequency.stack
        if (frequencyItem.isEmpty) false
        guiGraphics.pose().use {
            val xOffset = x + 15f
            val yOffset = y + 15f
            translate(xOffset, yOffset, 0f)
            scale(0.5f, 0.5f, 1f)
            translate(-xOffset, -yOffset, 100f)
            guiGraphics.renderItem(
                if (storageFrequency.isGlobalScope)
                    storageFrequency.stack
                else
                    CelItems.SCOPE_FILTER.asStack(), x, y
            )

            if (storageFrequency.isPersonalScope) {
                use {
                    val xOffset = x + 8f
                    val yOffset = y + 8f
                    translate(xOffset, yOffset, 0f)
                    scale(0.5f, 0.5f, 1f)
                    translate(-xOffset, -yOffset, 10f)
                    guiGraphics.renderItem(storageFrequency.stack, x, y)
                }
            }
        }

        true
    }
}
