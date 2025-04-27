package io.github.cotrin8672.cel.content.item

import com.mojang.blaze3d.platform.Lighting
import io.github.cotrin8672.cel.registry.CelDataComponents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.IItemDecorator
import thedarkcolour.kotlinforforge.neoforge.forge.use

object ScopeFilterItemDecorator : IItemDecorator {
    override fun render(guiGraphics: GuiGraphics, font: Font, stack: ItemStack, xOffset: Int, yOffset: Int): Boolean {
        val frequencyItemContainer = stack.get(CelDataComponents.FREQUENCY_ITEM) ?: return false
        if (frequencyItemContainer.slots == 0) return false
        val frequencyItem = frequencyItemContainer.getStackInSlot(0) ?: return false
        if (frequencyItem.isEmpty) return false
        guiGraphics.renderItemModel(xOffset + 8f, yOffset + 8f, 9f, frequencyItem)
        return true
    }

    private fun GuiGraphics.renderItemModel(
        x: Float,
        y: Float,
        scale: Float,
        stack: ItemStack,
    ) {
        val minecraft = Minecraft.getInstance()
        val model = minecraft.itemRenderer.getModel(stack, minecraft.level, minecraft.player, 16777216)
        pose().use {
            pose().translate(x, y, 160f)
            pose().scale(scale, -scale, scale)
            val flag = !model.usesBlockLight()
            if (flag) Lighting.setupForFlatItems()

            minecraft.itemRenderer.render(
                stack,
                ItemDisplayContext.GUI,
                false,
                pose(),
                bufferSource(),
                15728880,
                OverlayTexture.NO_OVERLAY,
                model
            )
            flush()
            if (flag) Lighting.setupFor3DItems()
        }
    }
}
