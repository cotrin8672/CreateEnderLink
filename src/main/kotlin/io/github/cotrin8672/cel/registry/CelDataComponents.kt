package io.github.cotrin8672.cel.registry

import com.mojang.serialization.Codec
import io.github.cotrin8672.cel.CreateEnderLink
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier
import java.util.function.UnaryOperator

object CelDataComponents {
    private val DATA_COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateEnderLink.MOD_ID)

    val FREQUENCY_ITEM = register("frequency_item") {
        it.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC)
    }

    val FREQUENCY_OWNER_UUID = register("owner_uuid") {
        it.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8)
    }

    val FREQUENCY_OWNER_NAME = register("owner_name") {
        it.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8)
    }

    private fun <T> register(name: String, builder: UnaryOperator<DataComponentType.Builder<T>>): DataComponentType<T> {
        val type = builder.apply(DataComponentType.builder()).build()
        DATA_COMPONENTS.register(name, Supplier { type })
        return type
    }

    fun register(bus: IEventBus) {
        DATA_COMPONENTS.register(bus)
    }
}
