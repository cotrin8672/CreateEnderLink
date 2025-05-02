package io.github.cotrin8672.cel.content.item

import com.mojang.blaze3d.vertex.PoseStack
import io.github.cotrin8672.cel.util.storageFrequency
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.IItemDecorator

object ScopeFilterItemDecorator : IItemDecorator {
    override fun render(
        guiGraphics: GuiGraphics,
        font: Font,
        stack: ItemStack,
        xOffset: Int,
        yOffset: Int,
    ): Boolean {
        val storageFrequency = stack.storageFrequency
        if (storageFrequency.isGlobalScope) return false
        val frequencyItem = storageFrequency.stack
        if (frequencyItem.isEmpty) return false
        guiGraphics.pose().use {
            translate(xOffset + 8f, yOffset + 8f, 0f)
            scale(0.5f, 0.5f, 1f)
            translate(-(xOffset + 8f), -(yOffset + 8f), 100f)
            guiGraphics.renderItem(frequencyItem, xOffset, yOffset)
        }
        return true
    }

    private fun <T> PoseStack.use(block: PoseStack.() -> T): T {
        this.pushPose()
        val result = block(this)
        this.popPose()
        return result
    }
}