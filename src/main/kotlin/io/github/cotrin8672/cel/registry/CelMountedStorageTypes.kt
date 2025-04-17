package io.github.cotrin8672.cel.registry

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType
import com.tterrag.registrate.util.entry.RegistryEntry
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import io.github.cotrin8672.cel.content.storage.SharedMountedFluidStorageType
import io.github.cotrin8672.cel.content.storage.SharedMountedItemStorageType

object CelMountedStorageTypes {
    val SHARED_ITEM: RegistryEntry<MountedItemStorageType<*>, SharedMountedItemStorageType> = REGISTRATE
        .mountedItemStorage("shared_item", ::SharedMountedItemStorageType)
        .register()

    val SHARED_FLUID: RegistryEntry<MountedFluidStorageType<*>, SharedMountedFluidStorageType> = REGISTRATE
        .mountedFluidStorage("shared_fluid", ::SharedMountedFluidStorageType)
        .register()
}
