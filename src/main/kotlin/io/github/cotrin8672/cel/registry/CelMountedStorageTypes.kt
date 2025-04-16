package io.github.cotrin8672.cel.registry

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType
import com.tterrag.registrate.util.entry.RegistryEntry
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import io.github.cotrin8672.cel.content.storage.EnderVaultMountedStorageType

object CelMountedStorageTypes {
    val SHARED_ITEM: RegistryEntry<MountedItemStorageType<*>, EnderVaultMountedStorageType> = REGISTRATE
        .mountedItemStorage("shared_item", ::EnderVaultMountedStorageType)
        .register()
}
