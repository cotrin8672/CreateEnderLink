package io.github.cotrin8672.cel.compat

import com.simibubi.create.compat.jei.GhostIngredientHandler
import io.github.cotrin8672.cel.content.item.ScopeFilterMenu
import io.github.cotrin8672.cel.content.item.ScopeFilterScreen
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.gui.handlers.IGhostIngredientHandler
import mezz.jei.api.registration.IGuiHandlerRegistration
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class CreateEnderLinkJeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("createenderlink", "jei_plugin")
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addGhostIngredientHandler(
            ScopeFilterScreen::class.java,
            GhostIngredientHandler<ScopeFilterMenu>() as IGhostIngredientHandler<ScopeFilterScreen>
        )
    }
}
