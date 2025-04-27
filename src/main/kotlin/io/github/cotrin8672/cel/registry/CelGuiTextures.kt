package io.github.cotrin8672.cel.registry

import io.github.cotrin8672.cel.CreateEnderLink
import net.createmod.catnip.gui.TextureSheetSegment
import net.createmod.catnip.gui.element.ScreenElement
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

enum class CelGuiTextures(
    private val path: String,
    private val width: Int,
    private val height: Int,
    private val startX: Int = 0,
    private val startY: Int = 0,
) : ScreenElement, TextureSheetSegment {
    SCOPE_FILTER("scope_filter", 214, 85);

    override fun getLocation(): ResourceLocation {
        return CreateEnderLink.asResource("textures/gui/${this.path}.png")
    }

    @OnlyIn(Dist.CLIENT)
    override fun render(graphics: GuiGraphics, x: Int, y: Int) {
        graphics.blit(location, x, y, 0, 0, width, height)
    }

    override fun getStartX(): Int {
        return startX
    }

    override fun getStartY(): Int {
        return startY
    }

    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
    }
}
