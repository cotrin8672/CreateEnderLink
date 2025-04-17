package io.github.cotrin8672.cel.datagen

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllItems
import io.github.cotrin8672.cel.registry.CelBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

class CelRecipeProvider(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>,
) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        with(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CelBlocks.ENDER_VAULT.asItem())) {
            pattern(" B ")
            pattern("EVE")
            pattern(" B ")
            define('B', AllItems.BRASS_SHEET)
            define('E', Items.ENDER_EYE)
            define('V', AllBlocks.ITEM_VAULT)
            unlockedBy("has_item_vault", has(AllBlocks.ITEM_VAULT))
            save(recipeOutput)
        }

        with(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CelBlocks.ENDER_TANK.asItem())) {
            pattern(" B ")
            pattern("ETE")
            pattern(" B ")
            define('B', AllItems.BRASS_SHEET)
            define('E', Items.ENDER_EYE)
            define('T', AllBlocks.FLUID_TANK)
            unlockedBy("has_item_vault", has(AllBlocks.FLUID_TANK))
            save(recipeOutput)
        }
    }
}
