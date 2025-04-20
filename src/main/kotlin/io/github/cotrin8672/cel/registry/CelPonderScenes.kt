package io.github.cotrin8672.cel.registry

import com.tterrag.registrate.util.entry.ItemProviderEntry
import io.github.cotrin8672.cel.content.ponder.EnderTankPonderScene
import io.github.cotrin8672.cel.content.ponder.EnderVaultPonderScene
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.resources.ResourceLocation

object CelPonderScenes {
    fun register(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        val registry = helper.withKeyFunction { obj: ItemProviderEntry<*> -> obj.id }

        registry.forComponents(CelBlocks.ENDER_VAULT)
            .addStoryBoard("ender_vault/sharing", EnderVaultPonderScene::sharing)
            .addStoryBoard("ender_vault/contraption", EnderVaultPonderScene::contraption)

        registry.forComponents(CelBlocks.ENDER_TANK)
            .addStoryBoard("ender_tank/sharing", EnderTankPonderScene::sharing)
    }
}
