package io.github.cotrin8672.cel.registry

import com.tterrag.registrate.builders.MenuBuilder.ForgeMenuFactory
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory
import com.tterrag.registrate.util.entry.MenuEntry
import com.tterrag.registrate.util.nullness.NonNullSupplier
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import io.github.cotrin8672.cel.content.item.ScopeFilterMenu
import io.github.cotrin8672.cel.content.item.ScopeFilterScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.world.inventory.AbstractContainerMenu

object CelMenuTypes {
    val SCOPE_FILTER: MenuEntry<ScopeFilterMenu> =
        register("scope_filter", ::ScopeFilterMenu) { ScreenFactory(::ScopeFilterScreen) }

    fun <C : AbstractContainerMenu?, S> register(
        name: String, factory: ForgeMenuFactory<C>, screenFactory: NonNullSupplier<ScreenFactory<C, S>?>,
    ): MenuEntry<C> where S : Screen?, S : MenuAccess<C> {
        return REGISTRATE.menu(name, factory, screenFactory).register()
    }

    fun register() {}
}
