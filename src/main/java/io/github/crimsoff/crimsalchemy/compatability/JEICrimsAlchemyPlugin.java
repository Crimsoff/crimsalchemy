package io.github.crimsoff.crimsalchemy.compatability;

import io.github.crimsoff.crimsalchemy.CrimsAlchemy;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import io.github.crimsoff.crimsalchemy.recipes.AlchemicalCauldronRecipe;
import io.github.crimsoff.crimsalchemy.recipes.CrimsData;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

@JeiPlugin
public class JEICrimsAlchemyPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CrimsAlchemy.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlchemicalCauldronCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlchemicalCauldronSpecialCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        ArrayList<AlchemicalCauldronRecipe> alchemical_cauldron_recipes = new ArrayList<>(CrimsData.RECIPE_LOADER.ALCHEMY_RECIPES.values());
        registration.addRecipes(AlchemicalCauldronCategory.ALCHEMICAL_CAULDRON_RECIPE, alchemical_cauldron_recipes);

        ArrayList<SpecialAlchemicalRecipe> meltingRecipes = new ArrayList<>();
        meltingRecipes.add(new SpecialAlchemicalRecipe(0));
        meltingRecipes.add(new SpecialAlchemicalRecipe(1));
        registration.addRecipes(AlchemicalCauldronSpecialCategory.MELTING_RECIPE, meltingRecipes);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get(), AlchemicalCauldronCategory.ALCHEMICAL_CAULDRON_RECIPE);
        registration.addRecipeCatalyst(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get(), AlchemicalCauldronSpecialCategory.MELTING_RECIPE);
    }
}
