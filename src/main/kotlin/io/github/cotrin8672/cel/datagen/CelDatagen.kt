package io.github.cotrin8672.cel.datagen

import com.tterrag.registrate.providers.RegistrateDataProvider
import io.github.cotrin8672.cel.CreateEnderLink.MOD_ID
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import net.neoforged.neoforge.data.event.GatherDataEvent

object CelDatagen {
    @JvmStatic
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        generator.addProvider(
            true,
            REGISTRATE.setDataProvider(RegistrateDataProvider(REGISTRATE, MOD_ID, event))
        )
        generator.addProvider(true, CelRecipeProvider(packOutput, event.lookupProvider))
    }
}
