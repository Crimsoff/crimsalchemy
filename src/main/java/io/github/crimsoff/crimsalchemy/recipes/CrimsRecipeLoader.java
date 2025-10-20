package io.github.crimsoff.crimsalchemy.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.crimsoff.crimsalchemy.CrimsAlchemy;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class CrimsRecipeLoader extends SimpleJsonResourceReloadListener {
    public Map<Item, AlchemicalCauldronRecipe> ALCHEMY_RECIPES = new HashMap<>();

    public CrimsRecipeLoader() {
        super(new Gson(), "alchemy_recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        jsonMap.forEach((loc, jsonElement) -> {
            // Parse into your custom class here
            if (jsonElement.isJsonObject()) {
                JsonObject json = jsonElement.getAsJsonObject();
                if (json.has("type") && json.has("input")) {
                    String type = json.get("type").getAsString();

                    Item input = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(json.get("input").getAsString()));
                    if (input != Items.AIR) {
                        if (type.equals("potion")) {
                            ResourceLocation effect = ResourceLocation.parse(json.get("effect").getAsString());
                            int duration = json.has("duration") ? json.get("duration").getAsInt() : 1800;
                            int amplifier = json.has("amplifier") ? json.get("amplifier").getAsInt() : 0;
                            int progress_required = json.has("progress_required") ? json.get("progress_required").getAsInt() : 20;
                            int capacity_requirement = json.has("capacity_requirement") ? json.get("capacity_requirement").getAsInt() : 1;
                            ALCHEMY_RECIPES.put(input, new AlchemyRecipe(input, effect, duration, amplifier, progress_required, capacity_requirement));
                        } else if (type.equals("dye")) {

                            int color = json.has("color") ? Integer.parseInt(json.get("color").getAsString(), 16) : 0xFFFFFF;
                            int progress_required = json.has("progress_required") ? json.get("progress_required").getAsInt() : 5;
                            ALCHEMY_RECIPES.put(input, new AlchemyDyeRecipe(input, color, progress_required));
                        } else if (type.equals("capacity")) {
                            int capacity = json.has("capacity") ? json.get("capacity").getAsInt() : 1;
                            int progress_required = json.has("progress_required") ? json.get("progress_required").getAsInt() : 1;
                            int capacity_requirement = json.has("capacity_requirement") ? json.get("capacity_requirement").getAsInt() : 0;
                            ALCHEMY_RECIPES.put(input, new AlchemyCapacityRecipe(input, capacity, progress_required, capacity_requirement));
                        } else if (type.equals("duration")) {
                            float multiplier = json.has("multiplier") ? json.get("multiplier").getAsFloat() : 2.0f;
                            boolean apply_to_all = json.has("apply_to_all") && json.get("apply_to_all").getAsBoolean();
                            int progress_required = json.has("progress_required") ? json.get("progress_required").getAsInt() : 1;
                            int capacity_requirement = json.has("capacity_requirement") ? json.get("capacity_requirement").getAsInt() : 1;
                            ALCHEMY_RECIPES.put(input, new AlchemyDurationRecipe(input, multiplier, progress_required, apply_to_all, capacity_requirement));
                        } else if (type.equals("amplifier")) {
                            int amplifier = json.has("amplifier") ? json.get("amplifier").getAsInt() : 1;
                            boolean apply_to_all = json.has("apply_to_all") && json.get("apply_to_all").getAsBoolean();
                            int progress_required = json.has("progress_required") ? json.get("progress_required").getAsInt() : 1;
                            int capacity_requirement = json.has("capacity_requirement") ? json.get("capacity_requirement").getAsInt() : 1;
                            if (amplifier >= 1) {
                                ALCHEMY_RECIPES.put(input, new AlchemyAmplifierRecipe(input, amplifier, progress_required, apply_to_all, capacity_requirement));
                            }
                        }  else if (type.equals("change")) {
                            Item output = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(json.get("output").getAsString()));
                            if (output != null) {
                                int progress_required = json.has("progress_required") ? json.get("progress_required").getAsInt() : 1;
                                int capacity_requirement = json.has("capacity_requirement") ? json.get("capacity_requirement").getAsInt() : 0;
                                ALCHEMY_RECIPES.put(input, new ChangePotionRecipe(input, output, progress_required, capacity_requirement));
                            }
                        } else {
                            CrimsAlchemy.LOGGER.error("[Crim's Alchemy] No recognized type in recipe " + loc);
                        }
                    } else {
                        CrimsAlchemy.LOGGER.error("[Crim's Alchemy] No item found in recipe " + loc);
                    }

                } else {
                    CrimsAlchemy.LOGGER.error("[Crim's Alchemy] Missing type or input in recipe " + loc);
                }
            } else {
                CrimsAlchemy.LOGGER.error("[Crim's Alchemy] Not a JSON Object in recipe " + loc);
            }


        });
    }

}