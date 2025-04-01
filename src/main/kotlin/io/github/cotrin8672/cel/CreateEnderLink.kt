package io.github.cotrin8672.cel

import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(CreateEnderLink.MOD_ID)
class CreateEnderLink(container: ModContainer) {
    companion object {
        const val MOD_ID = "createenderlink"

        fun asResource(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
        }
    }
}
