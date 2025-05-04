package io.github.cotrin8672.cel.datagen

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllItems
import io.github.cotrin8672.cel.CreateEnderLink
import io.github.cotrin8672.cel.registry.CelBlocks
import io.github.cotrin8672.cel.registry.CelItems
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import java.util.function.Consumer

class CelRecipeProvider(
    output: PackOutput,
) : RecipeProvider(output) {
    override fun buildRecipes(recipeOutput: Consumer<FinishedRecipe>) {
        with(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CelBlocks.ENDER_VAULT)) {
            pattern(" B ")
            pattern("EVE")
            pattern(" B ")
            define('B', AllItems.BRASS_SHEET)
            define('E', Items.ENDER_EYE)
            define('V', AllBlocks.ITEM_VAULT)
            unlockedBy("has_item_vault", has(AllBlocks.ITEM_VAULT))
            save(recipeOutput)
        }

        with(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CelBlocks.ENDER_VAULT)) {
            requires(CelBlocks.ENDER_VAULT)
            unlockedBy("has_ender_vault", has(CelBlocks.ENDER_VAULT))
            save(recipeOutput, CreateEnderLink.asResource("ender_vault_clear"))
        }

        with(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CelBlocks.ENDER_TANK)) {
            pattern(" B ")
            pattern("ETE")
            pattern(" B ")
            define('B', AllItems.BRASS_SHEET)
            define('E', Items.ENDER_EYE)
            define('T', AllBlocks.FLUID_TANK)
            unlockedBy("has_item_vault", has(AllBlocks.FLUID_TANK))
            save(recipeOutput)
        }

        with(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CelBlocks.ENDER_TANK)) {
            requires(CelBlocks.ENDER_TANK)
            unlockedBy("has_ender_tank", has(CelBlocks.ENDER_TANK))
            save(recipeOutput, CreateEnderLink.asResource("ender_tank_clear"))
        }

        with(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CelItems.SCOPE_FILTER)) {
            pattern("AWA")
            define('A', Items.AMETHYST_SHARD)
            define('W', ItemTags.WOOL)
            unlockedBy("has_amethyst", has(Items.AMETHYST_SHARD))
            save(recipeOutput)
        }

        with(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CelItems.SCOPE_FILTER)) {
            requires(CelItems.SCOPE_FILTER)
            unlockedBy("has_scope_filter", has(CelItems.SCOPE_FILTER))
            save(recipeOutput, CreateEnderLink.asResource("scope_filter_clear"))
        }
    }
}
