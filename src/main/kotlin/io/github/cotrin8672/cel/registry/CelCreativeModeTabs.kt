package io.github.cotrin8672.cel.registry

import io.github.cotrin8672.cel.CreateEnderLink
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import java.util.function.Supplier

object CelCreativeModeTabs {
    private val REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateEnderLink.MOD_ID)

    val CEL_CREATIVE_TAB: RegistryObject<CreativeModeTab> = REGISTER.register(
        "cel_default",
        Supplier {
            CreativeModeTab.builder().apply {
                title(Component.literal("Create: Ender Link"))
                icon { CelBlocks.ENDER_VAULT.asStack() }
            }.build()
        }
    )

    fun register(bus: IEventBus) {
        REGISTER.register(bus)
    }
}
