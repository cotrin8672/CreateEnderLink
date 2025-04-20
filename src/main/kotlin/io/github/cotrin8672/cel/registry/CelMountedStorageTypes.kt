package io.github.cotrin8672.cel.registry

import com.tterrag.registrate.util.entry.RegistryEntry
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import io.github.cotrin8672.cel.content.storage.SharedMountedFluidStorageType
import io.github.cotrin8672.cel.content.storage.SharedMountedItemStorageType

object CelMountedStorageTypes {
    val SHARED_ITEM: RegistryEntry<SharedMountedItemStorageType> = REGISTRATE
        .mountedItemStorage("shared_item", ::SharedMountedItemStorageType)
        .register()

    val SHARED_FLUID: RegistryEntry<SharedMountedFluidStorageType> = REGISTRATE
        .mountedFluidStorage("shared_fluid", ::SharedMountedFluidStorageType)
        .register()
}
