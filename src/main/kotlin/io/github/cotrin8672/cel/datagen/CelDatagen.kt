package io.github.cotrin8672.cel.datagen

import com.tterrag.registrate.providers.RegistrateDataProvider
import io.github.cotrin8672.cel.CreateEnderLink.Companion.MOD_ID
import io.github.cotrin8672.cel.CreateEnderLink.Companion.REGISTRATE
import net.neoforged.neoforge.data.event.GatherDataEvent

object CelDatagen {
    @JvmStatic
    fun gatherData(event: GatherDataEvent) {
        event.generator.addProvider(
            true,
            REGISTRATE.setDataProvider(RegistrateDataProvider(REGISTRATE, MOD_ID, event))
        )
    }
}
