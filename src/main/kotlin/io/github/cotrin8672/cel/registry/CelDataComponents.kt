package io.github.cotrin8672.cel.registry

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.util.StorageFrequency
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier
import java.util.function.UnaryOperator

object CelDataComponents {
    private val DATA_COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateEnderLink.MOD_ID)

    val STORAGE_FREQUENCY = register("storage_frequency") {
        it.persistent(StorageFrequency.CODEC).networkSynchronized(StorageFrequency.STREAM_CODEC)
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
