package io.github.cotrin8672.cel.datagen

import net.minecraftforge.data.event.GatherDataEvent

object CelDatagen {
    @JvmStatic
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        generator.addProvider(true, CelRecipeProvider(packOutput))
    }
}
