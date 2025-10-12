package io.github.crimsoff.crimsalchemy.compatability;


import io.github.crimsoff.crimsalchemy.CrimsAlchemy;
import io.github.crimsoff.crimsalchemy.blocks.CrimsBlockRegister;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import mezz.jei.api.helpers.IGuiHelper;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalCauldronSpecialCategory implements IRecipeCategory<SpecialAlchemicalRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CrimsAlchemy.MOD_ID, "alchemical_special");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrimsAlchemy.MOD_ID,
            "textures/gui/alchemical_cauldron.png");

    private final IDrawable background;
    private final IDrawable icon;

    public static RecipeType<SpecialAlchemicalRecipe> MELTING_RECIPE = new RecipeType<>(UID, SpecialAlchemicalRecipe.class);

    public AlchemicalCauldronSpecialCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 102);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CrimsBlockRegister.ALCHEMICAL_CAULDRON_BLOCK.get()));
    }

    @Override
    public RecipeType getRecipeType() {
        return MELTING_RECIPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, SpecialAlchemicalRecipe recipe, IFocusGroup focuses) {
        if (recipe.type == 0) {
            TagKey<Item> FILLS_HEATED_CAULDRON = ItemTags.create(ResourceLocation.fromNamespaceAndPath("crimsalchemy", "fills_heated_cauldron"));

            List<ItemStack> taggedItems = new ArrayList<>();
            for (Item item : ForgeRegistries.ITEMS) {
                ItemStack stack = new ItemStack(item);
                if (stack.getTags().anyMatch((tag) -> tag == FILLS_HEATED_CAULDRON)) {
                    taggedItems.add(stack);
                }
            }

            builder.addSlot(RecipeIngredientRole.INPUT, 80, 11)
                    .addItemStacks(taggedItems);

            builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 75).addFluidStack(Fluids.WATER);
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 80, 11).addItemLike(Items.GLASS_BOTTLE);

            ItemStack item = new ItemStack(Items.POTION, 1);
            item.setHoverName(Component.translatable("item.crimsalchemy.potion").withStyle(style -> style.withItalic(false)));

            builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 75).addItemStack(item);
        }

    }
}
