package io.github.crimsoff.crimsalchemy.compatability;


import io.github.crimsoff.crimsalchemy.CrimsAlchemy;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import io.github.crimsoff.crimsalchemy.recipes.AlchemicalCauldronRecipe;
import io.github.crimsoff.crimsalchemy.recipes.AlchemyRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import mezz.jei.api.helpers.IGuiHelper;

public class AlchemicalCauldronCategory implements IRecipeCategory<AlchemicalCauldronRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CrimsAlchemy.MOD_ID, "alchemical_brewing");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrimsAlchemy.MOD_ID,
            "textures/gui/alchemical_cauldron.png");

    private final IDrawable background;
    private final IDrawable icon;

    public static RecipeType<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON_RECIPE = new RecipeType<>(UID, AlchemicalCauldronRecipe.class);

    public AlchemicalCauldronCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 102);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get()));
    }

    @Override
    public RecipeType getRecipeType() {
        return ALCHEMICAL_CAULDRON_RECIPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.crimsalchemy.alchemical_cauldron");
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, AlchemicalCauldronRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 80, 11).addItemLike(recipe.getInput());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 80, 75).addItemStack(recipe.exampleOutput());

    }
}
