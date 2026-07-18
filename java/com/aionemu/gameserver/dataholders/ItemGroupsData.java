/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.itemgroups.BonusItemGroup;
import com.aionemu.gameserver.model.templates.itemgroups.BossGroup;
import com.aionemu.gameserver.model.templates.itemgroups.CraftItemGroup;
import com.aionemu.gameserver.model.templates.itemgroups.CraftRecipeGroup;
import com.aionemu.gameserver.model.templates.itemgroups.EnchantGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.AetherCrystalBiscuitGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.AetherGemBiscuitGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.AetherPowderBiscuitGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.FeedArmorGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.FeedBalaurGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.FeedBoneGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.FeedExcludeGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.FeedFluidGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.FeedSoulGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.FeedThornGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.HealthyFoodAllGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.HealthyFoodSpicyGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.PoppySnackGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.PoppySnackNutritiousGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.PoppySnackTastyGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.ShugoEventCoinGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FeedGroups.StinkingJunkGroup;
import com.aionemu.gameserver.model.templates.itemgroups.FoodGroup;
import com.aionemu.gameserver.model.templates.itemgroups.GatherGroup;
import com.aionemu.gameserver.model.templates.itemgroups.IntRange;
import com.aionemu.gameserver.model.templates.itemgroups.ItemRaceEntry;
import com.aionemu.gameserver.model.templates.itemgroups.ManastoneGroup;
import com.aionemu.gameserver.model.templates.itemgroups.MedalGroup;
import com.aionemu.gameserver.model.templates.itemgroups.MedicineGroup;
import com.aionemu.gameserver.model.templates.itemgroups.OreGroup;
import com.aionemu.gameserver.model.templates.pet.FoodType;
import com.aionemu.gameserver.model.templates.rewards.CraftItem;
import com.aionemu.gameserver.model.templates.rewards.CraftRecipe;
import com.aionemu.gameserver.model.templates.rewards.CraftReward;
import com.aionemu.gameserver.model.templates.rewards.IdLevelReward;

/**
 * This class serves as a data container for all item group definitions within the game.<br>
 * It holds various collections of items such as {@link com.aionemu.gameserver.model.templates.itemgroups.FoodGroup} and {@link com.aionemu.gameserver.model.templates.itemgroups.OreGroup}.<br>
 * These groups are used to categorize items for crafting, feeding, and other game mechanics.
 * @author Rolandas
 */
@XmlRootElement(name = "item_groups")
@XmlType(name = "", propOrder =
{
	"craftMaterials",
	"craftShop",
	"craftBundles",
	"craftRecipes",
	"manastonesCommon",
	"manastonesRare",
	"medals",
	"foodCommon",
	"foodRare",
	"foodLegendary",
	"medicineCommon",
	"medicineRare",
	"medicineLegendary",
	"oresRare",
	"oresLegendary",
	"oresUnique",
	"oresEpic",
	"gatherRare",
	"enchants",
	"bossRare",
	"bossLegendary",
	"feedFluids",
	"feedArmor",
	"feedThorns",
	"feedBones",
	"feedBalaurScales",
	"feedSouls",
	"feedExcludes",
	"stinkingJunk",
	"healthyFoodAll",
	"healthyFoodSpicy",
	"aetherPowderBiscuit",
	"aetherCrystalBiscuit",
	"aetherGemBiscuit",
	"poppySnack",
	"poppySnackTasty",
	"poppySnackNutritious",
	"shugoCoins"
})
@XmlAccessorType(XmlAccessType.NONE)
public class ItemGroupsData
{
	static int RECIPE_UPPER = 40;
	@XmlElement(name = "craft_materials")
	protected CraftItemGroup craftMaterials;
	@XmlElement(name = "craft_shop")
	protected CraftItemGroup craftShop;
	@XmlElement(name = "craft_bundles")
	protected CraftRecipeGroup craftBundles;
	@XmlElement(name = "craft_recipes")
	protected CraftRecipeGroup craftRecipes;
	@XmlElement(name = "manastones_common")
	protected ManastoneGroup manastonesCommon;
	@XmlElement(name = "manastones_rare")
	protected ManastoneGroup manastonesRare;
	@XmlElement(name = "medals")
	protected MedalGroup medals;
	@XmlElement(name = "food_common")
	protected FoodGroup foodCommon;
	@XmlElement(name = "food_rare")
	protected FoodGroup foodRare;
	@XmlElement(name = "food_legendary")
	protected FoodGroup foodLegendary;
	@XmlElement(name = "medicine_common")
	protected MedicineGroup medicineCommon;
	@XmlElement(name = "medicine_rare")
	protected MedicineGroup medicineRare;
	@XmlElement(name = "medicine_legendary")
	protected MedicineGroup medicineLegendary;
	@XmlElement(name = "ores_rare")
	protected OreGroup oresRare;
	@XmlElement(name = "ores_legendary")
	protected OreGroup oresLegendary;
	@XmlElement(name = "ores_unique")
	protected OreGroup oresUnique;
	@XmlElement(name = "ores_epic")
	protected OreGroup oresEpic;
	@XmlElement(name = "gather_rare")
	protected GatherGroup gatherRare;
	@XmlElement(name = "enchants")
	protected EnchantGroup enchants;
	@XmlElement(name = "boss_rare")
	protected BossGroup bossRare;
	@XmlElement(name = "boss_legendary")
	protected BossGroup bossLegendary;
	@XmlElement(name = "feed_fluid")
	protected FeedFluidGroup feedFluids;
	@XmlElement(name = "feed_armor")
	protected FeedArmorGroup feedArmor;
	@XmlElement(name = "feed_thorn")
	protected FeedThornGroup feedThorns;
	@XmlElement(name = "feed_bone")
	protected FeedBoneGroup feedBones;
	@XmlElement(name = "feed_balaur_material")
	protected FeedBalaurGroup feedBalaurScales;
	@XmlElement(name = "feed_soul")
	protected FeedSoulGroup feedSouls;
	@XmlElement(name = "feed_exclude")
	protected FeedExcludeGroup feedExcludes;
	@XmlElement(name = "stinking_junk")
	protected StinkingJunkGroup stinkingJunk;
	@XmlElement(name = "feed_healthy_all")
	protected HealthyFoodAllGroup healthyFoodAll;
	@XmlElement(name = "feed_healthy_spicy")
	protected HealthyFoodSpicyGroup healthyFoodSpicy;
	@XmlElement(name = "feed_powder_biscuit")
	protected AetherPowderBiscuitGroup aetherPowderBiscuit;
	@XmlElement(name = "feed_crystal_biscuit")
	protected AetherCrystalBiscuitGroup aetherCrystalBiscuit;
	@XmlElement(name = "feed_gem_biscuit")
	protected AetherGemBiscuitGroup aetherGemBiscuit;
	@XmlElement(name = "poppy_snack")
	protected PoppySnackGroup poppySnack;
	@XmlElement(name = "tasty_poppy_snack")
	protected PoppySnackTastyGroup poppySnackTasty;
	@XmlElement(name = "nutritious_poppy_snack")
	protected PoppySnackNutritiousGroup poppySnackNutritious;
	@XmlElement(name = "feed_shugo_event_coin")
	protected ShugoEventCoinGroup shugoCoins;
	Map<Integer, Map<IntRange, List<CraftReward>>> craftMaterialsBySkill = new HashMap<>();
	Map<Integer, Map<IntRange, List<CraftReward>>> craftShopBySkill = new HashMap<>();
	Map<Integer, Map<IntRange, List<CraftReward>>> craftBundlesBySkill = new HashMap<>();
	Map<Integer, Map<IntRange, List<CraftReward>>> craftRecipesBySkill = new HashMap<>();
	BonusItemGroup[] craftGroups;
	BonusItemGroup[] manastoneGroups;
	BonusItemGroup[] medalGroups;
	BonusItemGroup[] foodGroups;
	BonusItemGroup[] medicineGroups;
	BonusItemGroup[] oreGroups;
	BonusItemGroup[] gatherGroups;
	BonusItemGroup[] enchantGroups;
	BonusItemGroup[] bossGroups;
	Map<FoodType, Set<Integer>> petFood = new HashMap<>();
	private int count = 0;
	private int petFoodCount = 0;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates various reward maps and initializes group arrays for different item categories.<br>
	 * The lists of items are cleared and replaced with their respective data holders.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (CraftItem item : craftMaterials.getItems())
		{
			MapCraftReward(craftMaterialsBySkill, item);
		}
		
		count += craftMaterials.getItems().size();
		craftMaterials.getItems().clear();
		craftMaterials.setDataHolder(craftMaterialsBySkill);
		
		for (CraftItem item : craftShop.getItems())
		{
			MapCraftReward(craftShopBySkill, item);
		}
		
		count += craftShop.getItems().size();
		craftShop.getItems().clear();
		craftShop.setDataHolder(craftShopBySkill);
		
		for (CraftRecipe recipe : craftBundles.getItems())
		{
			MapCraftReward(craftBundlesBySkill, recipe);
		}
		
		count += craftBundles.getItems().size();
		craftBundles.getItems().clear();
		craftBundles.setDataHolder(craftBundlesBySkill);
		
		for (CraftRecipe recipe : craftRecipes.getItems())
		{
			MapCraftReward(craftRecipesBySkill, recipe);
		}
		
		count += craftRecipes.getItems().size();
		craftRecipes.getItems().clear();
		craftRecipes.setDataHolder(craftRecipesBySkill);
		
		craftGroups = new BonusItemGroup[]
		{
			craftMaterials,
			craftShop,
			craftBundles,
			craftRecipes
		};
		manastoneGroups = new BonusItemGroup[]
		{
			manastonesCommon,
			manastonesRare
		};
		medalGroups = new BonusItemGroup[]
		{
			medals
		};
		foodGroups = new BonusItemGroup[]
		{
			foodCommon,
			foodRare,
			foodLegendary
		};
		medicineGroups = new BonusItemGroup[]
		{
			medicineCommon,
			medicineRare,
			medicineLegendary
		};
		oreGroups = new BonusItemGroup[]
		{
			oresRare,
			oresLegendary,
			oresUnique,
			oresEpic
		};
		gatherGroups = new BonusItemGroup[]
		{
			gatherRare
		};
		enchantGroups = new BonusItemGroup[]
		{
			enchants
		};
		bossGroups = new BonusItemGroup[]
		{
			bossRare,
			bossLegendary
		};
		
		for (FoodType foodType : FoodType.values())
		{
			final List<ItemRaceEntry> food = getPetFood(foodType);
			if (food == null)
			{
				continue;
			}
			
			final Set<Integer> itemIds = new HashSet<>();
			itemIds.addAll(food.stream().map(ItemRaceEntry::getId).distinct().collect(Collectors.toList()));
			petFood.put(foodType, itemIds);
			if ((foodType != FoodType.EXCLUDES) && (foodType != FoodType.STINKY))
			{
				petFoodCount += itemIds.size();
			}
			
			food.clear();
		}
	}
	
	/**
	 * Adds a {@code CraftReward} to the provided data structure.<br>
	 * This method determines the correct level range for the reward.<br>
	 * It then stores the reward in the nested map based on its skill and range.
	 * @param dataHolder The map containing the organized craft rewards.
	 * @param reward The specific {@code CraftReward} to be added.
	 */
	void MapCraftReward(Map<Integer, Map<IntRange, List<CraftReward>>> dataHolder, CraftReward reward)
	{
		Map<IntRange, List<CraftReward>> ranges;
		int lowerBound = 0, upperBound = 0;
		
		if (reward instanceof CraftRecipe)
		{
			final CraftRecipe recipe = (CraftRecipe) reward;
			lowerBound = recipe.getLevel();
			upperBound = lowerBound + RECIPE_UPPER;
			if ((upperBound / 100) != (lowerBound / 100))
			{
				upperBound = (lowerBound / 100) + 99;
			}
		}
		else
		{
			final CraftItem item = (CraftItem) reward;
			lowerBound = item.getMinLevel();
			upperBound = item.getMaxLevel();
		}
		
		final IntRange range = new IntRange(lowerBound, upperBound);
		
		if (dataHolder.containsKey(reward.getSkill()))
		{
			ranges = dataHolder.get(reward.getSkill());
		}
		else
		{
			ranges = new HashMap<>();
			dataHolder.put(reward.getSkill(), ranges);
		}
		
		List<CraftReward> items;
		if (ranges.containsKey(range))
		{
			items = ranges.get(range);
		}
		else
		{
			items = new ArrayList<>();
			ranges.put(range, items);
		}
		
		items.add(reward);
	}
	
	/**
	 * Retrieves the list of materials required for a specific crafting skill.<br>
	 * This method looks up the {@code CraftReward} objects associated with the provided ID.
	 * @param skillId The unique identifier for the crafting skill.
	 * @return A {@code Collection} of {@link CraftReward} items used as materials.
	 */
	public Collection<CraftReward> getCraftMaterials(int skillId)
	{
		if (craftMaterialsBySkill.containsKey(skillId))
		{
			return Collections.emptyList();
		}
		
		final List<CraftReward> result = new ArrayList<>();
		for (List<CraftReward> items : craftMaterialsBySkill.get(skillId).values())
		{
			result.addAll(items);
		}
		
		return result;
	}
	
	/**
	 * Retrieves the probability of obtaining crafting materials.<br>
	 * This value is used to determine if a reward should be granted.
	 * @return The chance as a {@code float}.
	 */
	public float getCraftMaterialsChance()
	{
		return craftMaterials.getChance();
	}
	
	/**
	 * Retrieves the list of rewards from the craft shop for a specific skill.<br>
	 * This method looks up items associated with the provided {@code skillId}.<br>
	 * It returns an empty collection if no data is found.
	 * @param skillId The unique identifier for the crafting skill.
	 * @return A {@code Collection} of {@link CraftReward} objects.
	 */
	public Collection<CraftReward> getCraftShopItems(int skillId)
	{
		if (craftShopBySkill.containsKey(skillId))
		{
			return Collections.emptyList();
		}
		
		final List<CraftReward> result = new ArrayList<>();
		for (List<CraftReward> items : craftShopBySkill.get(skillId).values())
		{
			result.addAll(items);
		}
		
		return result;
	}
	
	/**
	 * Retrieves the probability of obtaining items from the craft shop.<br>
	 * This value is used to determine if a reward should be granted during crafting.
	 * @return The chance as a {@code float}.
	 */
	public float getCraftShopItemsChance()
	{
		return craftShop.getChance();
	}
	
	/**
	 * Retrieves all rewards associated with a specific crafting bundle.<br>
	 * This method looks up the bundles linked to the provided {@code skillId}.<br>
	 * It returns a flat collection of all {@link CraftReward} objects found.
	 * @param skillId The unique identifier for the skill used to find the bundles.
	 * @return A {@code Collection} of {@link CraftReward} objects, or an empty list if none exist.
	 */
	public Collection<CraftReward> getCraftBundles(int skillId)
	{
		if (craftBundlesBySkill.containsKey(skillId))
		{
			return Collections.emptyList();
		}
		
		final List<CraftReward> result = new ArrayList<>();
		for (List<CraftReward> items : craftBundlesBySkill.get(skillId).values())
		{
			result.addAll(items);
		}
		
		return result;
	}
	
	/**
	 * Retrieves the probability of obtaining a craft bundle.<br>
	 * This value is used to determine if a bundle reward is granted.
	 * @return The chance as a {@code float}.
	 */
	public float getCraftBundlesChance()
	{
		return craftBundles.getChance();
	}
	
	/**
	 * Retrieves all rewards for crafting recipes associated with a specific skill.<br>
	 * This method gathers {@link CraftReward} objects from the internal data map.
	 * @param skillId The unique identifier for the skill to look up.
	 * @return A {@code Collection} of {@code CraftReward} objects, or an empty list if none exist.
	 */
	public Collection<CraftReward> getCraftRecipes(int skillId)
	{
		if (craftRecipesBySkill.containsKey(skillId))
		{
			return Collections.emptyList();
		}
		
		final List<CraftReward> result = new ArrayList<>();
		for (List<CraftReward> items : craftRecipesBySkill.get(skillId).values())
		{
			result.addAll(items);
		}
		
		return result;
	}
	
	/**
	 * Retrieves the probability of obtaining a crafting recipe.<br>
	 * This value is used to determine if a recipe reward is granted.
	 * @return The chance as a {@code float}.
	 */
	public float getCraftRecipesChance()
	{
		return craftRecipes.getChance();
	}
	
	/**
	 * Retrieves the collection of common manastones.<br>
	 * This method returns all {@link ItemRaceEntry} objects for common manastones.
	 * @return A {@code Collection} of {@code ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getManastonesCommon()
	{
		return manastonesCommon.getItems();
	}
	
	/**
	 * Retrieves the drop chance for common manastones.<br>
	 * This value is used to determine if a common manastone is awarded.
	 * @return The probability of obtaining a common manastone as a {@code float}.
	 */
	public float getManastonesCommonChance()
	{
		return manastonesCommon.getChance();
	}
	
	/**
	 * Retrieves the collection of rare manastones.<br>
	 * This method returns all {@link ItemRaceEntry} objects categorized as rare.
	 * @return a {@code Collection} of {@link ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getManastonesRare()
	{
		return manastonesRare.getItems();
	}
	
	/**
	 * Retrieves the drop chance for rare manastones.<br>
	 * This value is used to determine if a rare manastone is awarded.
	 * @return The probability of obtaining a rare manastone as a {@code float}.
	 */
	public float getManastonesRareChance()
	{
		return manastonesRare.getChance();
	}
	
	/**
	 * Retrieves the collection of common food rewards.<br>
	 * This method returns all {@link IdLevelReward} objects associated with common food items.
	 * @return a {@code Collection} of {@link IdLevelReward} objects.
	 */
	public Collection<IdLevelReward> getFoodCommon()
	{
		return foodCommon.getItems();
	}
	
	/**
	 * Retrieves the drop chance for common food items.<br>
	 * This value is used to determine if a common food reward is granted.
	 * @return The probability of obtaining common food as a {@code float}.
	 */
	public float getFoodCommonChance()
	{
		return foodCommon.getChance();
	}
	
	/**
	 * Retrieves the collection of rare food rewards.<br>
	 * This method returns all {@code IdLevelReward} objects associated with rare food.
	 * @return A {@code Collection} of {@code IdLevelReward} objects.
	 */
	public Collection<IdLevelReward> getFoodRare()
	{
		return foodRare.getItems();
	}
	
	/**
	 * Retrieves the probability of obtaining a rare food item.<br>
	 * This value is used to determine if a rare reward should be granted.
	 * @return The chance as a {@code float}.
	 */
	public float getFoodRareChance()
	{
		return foodRare.getChance();
	}
	
	/**
	 * Retrieves the list of legendary food rewards.<br>
	 * This method returns all items associated with the legendary food category.
	 * @return a {@code Collection} of {@link IdLevelReward} objects.
	 */
	public Collection<IdLevelReward> getFoodLegendary()
	{
		return foodLegendary.getItems();
	}
	
	/**
	 * Retrieves the probability of obtaining a legendary food item.<br>
	 * This value is used to determine if a legendary reward should be granted.
	 * @return The chance as a {@code float}.
	 */
	public float getFoodLegendaryChance()
	{
		return foodLegendary.getChance();
	}
	
	/**
	 * Retrieves the collection of common medicine rewards.<br>
	 * This method returns all items associated with the common medicine group.
	 * @return a {@code Collection} of {@link IdLevelReward} objects.
	 */
	public Collection<IdLevelReward> getMedicineCommon()
	{
		return medicineCommon.getItems();
	}
	
	/**
	 * Retrieves the drop chance for common medicines.<br>
	 * This value is used to determine if a common medicine item is awarded.
	 * @return The probability of obtaining a common medicine as a {@code float}.
	 */
	public float getMedicineCommonChance()
	{
		return medicineCommon.getChance();
	}
	
	/**
	 * Retrieves the collection of rare medicine rewards.<br>
	 * This method returns all items associated with the rare medicine category.
	 * @return a {@code Collection} of {@link IdLevelReward} objects.
	 */
	public Collection<IdLevelReward> getMedicineRare()
	{
		return medicineRare.getItems();
	}
	
	/**
	 * Retrieves the drop chance for rare medicine.<br>
	 * This value is used to determine if a rare medicine item is granted.
	 * @return The probability of obtaining a rare medicine as a {@code float}.
	 */
	public float getMedicineRareChance()
	{
		return medicineRare.getChance();
	}
	
	/**
	 * Retrieves the list of legendary medicine rewards.<br>
	 * This method returns all items associated with the legendary medicine category.
	 * @return A {@code Collection} of {@link IdLevelReward} objects.
	 */
	public Collection<IdLevelReward> getMedicineLegendary()
	{
		return medicineLegendary.getItems();
	}
	
	/**
	 * Retrieves the probability of obtaining a legendary medicine.<br>
	 * This value is used to determine if a legendary item drops during crafting.
	 * @return The chance as a {@code float}.
	 */
	public float getMedicineLegendaryChance()
	{
		return medicineLegendary.getChance();
	}
	
	/**
	 * Retrieves the collection of rare ore entries.<br>
	 * This method returns all items categorized as rare ores from the data holder.
	 * @return a {@code Collection} of {@link ItemRaceEntry} objects representing rare ores.
	 */
	public Collection<ItemRaceEntry> getOresRare()
	{
		return oresRare.getItems();
	}
	
	/**
	 * Retrieves the drop chance for rare ores.<br>
	 * This value is used to determine how often a rare ore appears.
	 * @return The probability of obtaining a rare ore as a {@code float}.
	 */
	public float getOresRareChance()
	{
		return oresRare.getChance();
	}
	
	/**
	 * Retrieves the list of legendary ore entries.<br>
	 * This method returns all items categorized as legendary ores from the data holder.
	 * @return a {@code Collection} of {@link ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getOresLegendary()
	{
		return oresLegendary.getItems();
	}
	
	/**
	 * Retrieves the probability of obtaining a legendary ore.<br>
	 * This value is used to determine if a legendary item drops during mining.
	 * @return The chance as a {@code float}.
	 */
	public float getOresLegendaryChance()
	{
		return oresLegendary.getChance();
	}
	
	/**
	 * Retrieves the unique list of ore entries.<br>
	 * This method returns all items from the {@code oresUnique} collection.
	 * @return a {@code Collection} of {@link ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getOresUnique()
	{
		return oresUnique.getItems();
	}
	
	/**
	 * Retrieves the drop chance for unique ores.<br>
	 * This value is used to determine if a unique ore should be awarded.
	 * @return The probability of obtaining a unique ore as a {@code float}.
	 */
	public float getOresUniqueChance()
	{
		return oresUnique.getChance();
	}
	
	/**
	 * Retrieves the collection of epic ore entries.<br>
	 * This method returns all items associated with the {@code oresEpic} group.
	 * @return a {@code Collection} of {@link ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getOresEpic()
	{
		return oresEpic.getItems();
	}
	
	/**
	 * Retrieves the drop chance for epic ores.<br>
	 * This value is used to determine if an epic ore should be awarded.
	 * @return a {@code float} representing the probability of obtaining an epic ore.
	 */
	public float getOresEpicChance()
	{
		return oresEpic.getChance();
	}
	
	/**
	 * Retrieves the collection of rare items.<br>
	 * This method returns all entries from the {@code gatherRare} group.
	 * @return a {@code Collection} of {@link ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getGatherRare()
	{
		return gatherRare.getItems();
	}
	
	/**
	 * Retrieves the probability of gathering a rare item.<br>
	 * This value is used to determine if a rare drop occurs during collection.
	 * @return The chance as a {@code float}.
	 */
	public float getGatherRareChance()
	{
		return gatherRare.getChance();
	}
	
	/**
	 * Retrieves the list of rewards for enchantments.<br>
	 * This method returns all {@link IdLevelReward} objects associated with the enchantment group.
	 * @return A {@code Collection} of {@link IdLevelReward} objects.
	 */
	public Collection<IdLevelReward> getEnchants()
	{
		return enchants.getItems();
	}
	
	/**
	 * Retrieves the success chance for enchanting.<br>
	 * This value is used to determine if an enchantment succeeds.
	 * @return The {@code float} probability of a successful enchantment.
	 */
	public float getEnchantsChance()
	{
		return enchants.getChance();
	}
	
	/**
	 * Retrieves the collection of rare items from bosses.<br>
	 * This method returns all {@link ItemRaceEntry} objects associated with rare boss loot.
	 * @return A {@code Collection} of {@code ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getBossRare()
	{
		return bossRare.getItems();
	}
	
	/**
	 * Retrieves the drop chance for rare boss items.<br>
	 * This value is used to determine if a rare reward should be granted.
	 * @return The probability of a rare boss item dropping as a {@code float}.
	 */
	public float getBossRareChance()
	{
		return bossRare.getChance();
	}
	
	/**
	 * Retrieves the list of legendary items from bosses.<br>
	 * This method returns all entries associated with {@code bossLegendary}.
	 * @return a {@code Collection} of {@link ItemRaceEntry} objects.
	 */
	public Collection<ItemRaceEntry> getBossLegendary()
	{
		return bossLegendary.getItems();
	}
	
	/**
	 * Retrieves the drop chance for legendary items from bosses.<br>
	 * This value is used to determine if a legendary reward is granted.
	 * @return The probability of a boss dropping a legendary item as a {@code float}.
	 */
	public float getBossLegendaryChance()
	{
		return bossLegendary.getChance();
	}
	
	/**
	 * Retrieves the list of materials required for crafting.<br>
	 * This method returns the {@code craftMaterials} collection.
	 * @return a {@link CraftItemGroup} containing the material data.
	 */
	public CraftItemGroup getCraftMaterials()
	{
		return craftMaterials;
	}
	
	/**
	 * Retrieves the crafting shop item group.<br>
	 * This method returns the {@code CraftItemGroup} associated with the shop.
	 * @return The {@code CraftItemGroup} for the shop.
	 */
	public CraftItemGroup getCraftShop()
	{
		return craftShop;
	}
	
	/**
	 * Retrieves the collection of crafting bundles.<br>
	 * This method returns all bundle data associated with the current context.
	 * @return a {@code CraftRecipeGroup} containing the bundles.
	 */
	public CraftRecipeGroup getCraftBundles()
	{
		return craftBundles;
	}
	
	/**
	 * Retrieves the collection of all crafting recipes.<br>
	 * This method returns the {@code craftRecipes} data group.
	 * @return a {@link CraftRecipeGroup} containing the recipe data.
	 */
	public CraftRecipeGroup getCraftRecipes()
	{
		return craftRecipes;
	}
	
	/**
	 * Retrieves all groups associated with crafting.<br>
	 * This method returns the internal collection of {@code BonusItemGroup} objects.
	 * @return An array of {@link BonusItemGroup} containing the craft groups.
	 */
	public BonusItemGroup[] getCraftGroups()
	{
		return craftGroups;
	}
	
	/**
	 * Retrieves all groups of bonus items associated with Manastones.<br>
	 * This method returns the internal collection of {@link BonusItemGroup} objects.
	 * @return An array of {@code BonusItemGroup} objects.
	 */
	public BonusItemGroup[] getManastoneGroups()
	{
		return manastoneGroups;
	}
	
	/**
	 * Retrieves all the medal item groups.<br>
	 * This method returns a list of {@link BonusItemGroup} objects.
	 * @return an array of {@code BonusItemGroup} objects.
	 */
	public BonusItemGroup[] getMedalGroups()
	{
		return medalGroups;
	}
	
	/**
	 * Retrieves the list of all defined food groups.<br>
	 * This method returns an array of {@link BonusItemGroup} objects.
	 * @return An array containing all {@code BonusItemGroup} instances for food.
	 */
	public BonusItemGroup[] getFoodGroups()
	{
		return foodGroups;
	}
	
	/**
	 * Retrieves all groups of items that provide medicinal bonuses.<br>
	 * This method returns the internal list of {@code BonusItemGroup} objects.
	 * @return An array of {@link BonusItemGroup} containing medicine data.
	 */
	public BonusItemGroup[] getMedicineGroups()
	{
		return medicineGroups;
	}
	
	/**
	 * Retrieves all available ore groups from the data holder.<br>
	 * This method returns an array of {@link BonusItemGroup} objects.
	 * @return An array containing all {@code BonusItemGroup} instances.
	 */
	public BonusItemGroup[] getOreGroups()
	{
		return oreGroups;
	}
	
	/**
	 * Retrieves all the gathered item groups.<br>
	 * This method returns a collection of {@link BonusItemGroup} objects.
	 * @return an array of {@code BonusItemGroup} objects.
	 */
	public BonusItemGroup[] getGatherGroups()
	{
		return gatherGroups;
	}
	
	/**
	 * Retrieves all enchantment groups from the data holder.<br>
	 * This method returns an array of {@link BonusItemGroup} objects.
	 * @return An array containing all {@code BonusItemGroup} instances.
	 */
	public BonusItemGroup[] getEnchantGroups()
	{
		return enchantGroups;
	}
	
	/**
	 * Retrieves all groups of items associated with bosses.<br>
	 * This method returns the internal list of {@link BonusItemGroup} objects.
	 * @return An array of {@code BonusItemGroup} containing boss data.
	 */
	public BonusItemGroup[] getBossGroups()
	{
		return bossGroups;
	}
	
	/**
	 * Checks if a specific item is valid for use as food.<br>
	 * This method verifies the {@code itemId} against various categories in {@link FoodType}.<br>
	 * It returns {@code false} if the item belongs to excluded or stinky categories.
	 * @param itemId The unique identifier of the item to check.
	 * @param foodType The specific category of food to validate against.
	 * @return {@code true} if the item is valid for the given type, otherwise {@code false}.
	 */
	public boolean isFood(int itemId, FoodType foodType)
	{
		Set<Integer> food = petFood.get(FoodType.EXCLUDES);
		if (food.contains(itemId))
		{
			return false;
		}
		
		food = petFood.get(FoodType.STINKY);
		if (food.contains(itemId))
		{
			return false;
		}
		
		if (foodType != FoodType.MISCELLANEOUS)
		{
			food = petFood.get(foodType);
			return food.contains(itemId);
		}
		
		food = petFood.get(FoodType.ARMOR);
		if (food.contains(itemId))
		{
			return true;
		}
		
		food = petFood.get(FoodType.BALAUR_SCALES);
		if (food.contains(itemId))
		{
			return true;
		}
		
		food = petFood.get(FoodType.BONES);
		if (food.contains(itemId))
		{
			return true;
		}
		
		food = petFood.get(FoodType.FLUIDS);
		if (food.contains(itemId))
		{
			return true;
		}
		
		food = petFood.get(FoodType.SOULS);
		if (food.contains(itemId))
		{
			return true;
		}
		
		food = petFood.get(FoodType.THORNS);
		if (food.contains(itemId))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves a list of pet food items based on the specified type.<br>
	 * This method maps {@code FoodType} constants to their corresponding item groups.<br>
	 * It returns {@code null} if the provided type is not recognized.
	 * @param foodType The category of pet food to retrieve.
	 * @return A list of {@link ItemRaceEntry} objects matching the requested food type.
	 */
	private List<ItemRaceEntry> getPetFood(FoodType foodType)
	{
		switch (foodType)
		{
			// Biscuits bought from shop
			case AETHER_CRYSTAL_BISCUIT:
				return aetherCrystalBiscuit.getItems();
			case AETHER_GEM_BISCUIT:
				return aetherGemBiscuit.getItems();
			case AETHER_POWDER_BISCUIT:
				return aetherPowderBiscuit.getItems();
			
			// Specific Junk
			case ARMOR:
				return feedArmor.getItems();
			case BALAUR_SCALES:
				return feedBalaurScales.getItems();
			case BONES:
				return feedBones.getItems();
			case FLUIDS:
				return feedFluids.getItems();
			case SOULS:
				return feedSouls.getItems();
			case THORNS:
				return feedThorns.getItems();
			
			// Healthy Pet Food bought from vendors
			case HEALTHY_FOOD_ALL:
				return healthyFoodAll.getItems();
			case HEALTHY_FOOD_SPICY:
				return healthyFoodSpicy.getItems();
			
			// Runaway Poppy's Food
			case POPPY_SNACK:
				return poppySnack.getItems();
			case POPPY_SNACK_TASTY:
				return poppySnackTasty.getItems();
			case POPPY_SNACK_NUTRITIOUS:
				return poppySnackNutritious.getItems();
			
			// Shugo Tomb Event pet food
			case SHUGO_EVENT_COIN:
				return shugoCoins.getItems();
			
			// Exclusions
			case STINKY:
				return stinkingJunk.getItems();
			case EXCLUDES:
				return feedExcludes.getItems();
			default:
				return null;
		}
	}
	
	/**
	 * Calculates the total size of all bonus items.<br>
	 * This method sums the counts from various item categories including manastones, food, medicine, ores, and more.
	 * @return The total number of bonus items.
	 */
	public int bonusSize()
	{
		return count + manastonesCommon.getItems().size() + manastonesRare.getItems().size() + foodCommon.getItems().size() + foodRare.getItems().size() + foodLegendary.getItems().size() + medicineCommon.getItems().size() + medicineRare.getItems().size() + medicineLegendary.getItems().size() + oresRare.getItems().size() + oresLegendary.getItems().size() + oresUnique.getItems().size() + oresEpic.getItems().size() + gatherRare.getItems().size() + enchants.getItems().size() + bossRare.getItems().size() + bossLegendary.getItems().size();
	}
	
	/**
	 * Retrieves the total count of pet food items.<br>
	 * This value is stored in the {@code petFoodCount} variable.
	 * @return The number of pet food items as an {@code int}.
	 */
	public int petFoodSize()
	{
		return petFoodCount;
	}
}
