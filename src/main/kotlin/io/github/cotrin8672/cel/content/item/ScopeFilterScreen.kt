package io.github.cotrin8672.cel.content.item

import com.simibubi.create.foundation.gui.AllGuiTextures
import com.simibubi.create.foundation.gui.AllIcons
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen
import com.simibubi.create.foundation.gui.widget.IconButton
import io.github.cotrin8672.cel.registry.CelGuiTextures
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle
import net.createmod.catnip.gui.element.GuiGameElement
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import kotlin.math.max

class ScopeFilterScreen(
    container: ScopeFilterMenu,
    inv: Inventory,
    title: Component,
) : AbstractSimiContainerScreen<ScopeFilterMenu>(container, inv, title) {
    private val background = CelGuiTextures.SCOPE_FILTER

    override fun init() {
        setWindowSize(
            max(background.width, AllGuiTextures.PLAYER_INVENTORY.width),
            background.height + 4 + AllGuiTextures.PLAYER_INVENTORY.height
        )

        super.init()

        IconButton(leftPos + background.width - 33, topPos + background.height - 24, AllIcons.I_CONFIRM).apply {
            withCallback<IconButton>(Runnable {
                minecraft?.player?.closeContainer()
            })
            addRenderableWidgets(this)
        }
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.width)
        val invY = topPos + background.height + 4
        renderPlayerInventory(graphics, invX, invY)
        background.render(graphics, leftPos, topPos)

        graphics.drawString(
            font, title, leftPos + (background.width - 8) / 2 - font.width(title) / 2, topPos + 4,
            0x41216D,
            false
        )

        graphics.drawString(
            font,
            minecraft?.player?.displayName ?: Component.literal(""),
            leftPos + (background.width - 40) / 2 - font.width(title) / 2, topPos + 31,
            0xF3EBDE,
            true
        )

        GuiGameElement.of(menu.contentHolder).at<GuiGameElement.GuiRenderBuilder>(
            (leftPos + background.width + 8f),
            (topPos + background.height - 52f),
            -200f
        )
            .scale(4.0)
            .render(graphics)
    }
}