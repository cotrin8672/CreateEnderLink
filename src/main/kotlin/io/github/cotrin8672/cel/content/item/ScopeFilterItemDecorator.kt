package io.github.cotrin8672.cel.content.item

import io.github.cotrin8672.cel.registry.CelDataComponents
import io.github.cotrin8672.cel.util.use
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.IItemDecorator

object ScopeFilterItemDecorator : IItemDecorator {
    override fun render(guiGraphics: GuiGraphics, font: Font, stack: ItemStack, xOffset: Int, yOffset: Int): Boolean {
        val storageFrequency = stack.get(CelDataComponents.STORAGE_FREQUENCY) ?: return false
        val frequencyItem = storageFrequency.stack
        if (frequencyItem.isEmpty) return false
        guiGraphics.pose().use {
            translate(xOffset + 8f, yOffset + 8f, 0f)
            scale(0.5f, 0.5f, 1f)
            translate(-(xOffset + 8f), -(yOffset + 8f), 40f)
            guiGraphics.renderItem(frequencyItem, xOffset, yOffset)
        }
        return true
    }
}
