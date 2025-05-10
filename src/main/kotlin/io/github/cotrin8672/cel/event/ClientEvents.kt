package io.github.cotrin8672.cel.event

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.content.item.ScopeFilterItemDecorator
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.registry.CelItems
import io.github.cotrin8672.cel.util.storageFrequency
import io.github.cotrin8672.cel.util.use
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.IItemDecorator
import net.minecraftforge.client.event.RegisterItemDecorationsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CreateEnderLink.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ClientEvents {
    @SubscribeEvent
    fun onRegisterItemDecoration(event: RegisterItemDecorationsEvent) {
        event.register(CelItems.SCOPE_FILTER, ScopeFilterItemDecorator)
        event.register(CelBlocks.ENDER_VAULT, DECORATOR)
        event.register(CelBlocks.ENDER_TANK, DECORATOR)
    }

    private val DECORATOR = IItemDecorator { guiGraphics: GuiGraphics, _: Font, stack: ItemStack, x: Int, y: Int ->
        val storageFrequency = stack.storageFrequency
        val frequencyItem = storageFrequency.stack
        if (frequencyItem.isEmpty) return@IItemDecorator false
        guiGraphics.pose().use {
            val xOffset = x + 15f
            val yOffset = y + 1f
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
                    val scopeXOffset = x + 8f
                    val scopeYOffset = y + 8f
                    translate(scopeXOffset, scopeYOffset, 0f)
                    scale(0.5f, 0.5f, 1f)
                    translate(-scopeXOffset, -scopeYOffset, 10f)
                    guiGraphics.renderItem(storageFrequency.stack, x, y)
                }
            }
        }

        true
    }
}
