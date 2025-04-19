package io.github.cotrin8672.cel.content.ponder

import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.registry.CelPonderScenes
import net.createmod.ponder.api.registration.PonderPlugin
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.resources.ResourceLocation

object CelPonderPlugin : PonderPlugin {
    override fun getModId(): String {
        return CreateEnderLink.MOD_ID
    }

    override fun registerScenes(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        CelPonderScenes.register(helper)
    }
}
