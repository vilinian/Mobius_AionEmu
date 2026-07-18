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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerRecipesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEARN_RECIPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECIPE_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the collection of recipes learned by a specific player.<br>
 * It handles loading, saving, and updating recipe data for {@link com.aionemu.gameserver.model.gameobjects.player.Player}.<br>
 * This class provides helper methods to synchronize the player's known recipes with the database.
 * @author MrPoke
 */
public class RecipeList
{
	private Set<Integer> recipeList = new HashSet<>();
	
	/**
	 * Creates a new {@link RecipeList} using a provided set of IDs.<br>
	 * This constructor initializes the internal collection with the given data.
	 * @param recipeList A {@code HashSet<Integer>} containing the unique identifiers for each recipe.
	 */
	public RecipeList(HashSet<Integer> recipeList)
	{
		this.recipeList = recipeList;
	}
	
	/**
	 * Creates a new instance of the {@code RecipeList} class.<br>
	 * This initializes an empty set to store recipes.
	 */
	public RecipeList()
	{
	}
	
	/**
	 * Retrieves the collection of recipes for this list.<br>
	 * This method returns all stored {@code Integer} IDs.
	 * @return a {@link Set} containing the recipe IDs.
	 */
	public Set<Integer> getRecipeList()
	{
		return recipeList;
	}
	
	/**
	 * Adds a new recipe to the {@link Player} object.<br>
	 * This method checks if the {@code RecipeTemplate} is already known.<br>
	 * It saves the recipe to the database and sends a success packet.
	 * @param player The {@link Player} who will learn the recipe.
	 * @param recipeTemplate The {@link RecipeTemplate} containing the recipe data.
	 */
	public void addRecipe(Player player, RecipeTemplate recipeTemplate)
	{
		final int recipeId = recipeTemplate.getId();
		if (!player.getRecipeList().isRecipePresent(recipeId))
		{
			if (DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(player.getObjectId(), recipeId))
			{
				recipeList.add(recipeId);
				PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(recipeId));
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_LEARN(recipeId, player.getName()));
			}
		}
	}
	
	/**
	 * Adds a new recipe to the player's collection.<br>
	 * This method updates the database and the local {@code recipeList}.
	 * @param playerId The unique identifier of the player.
	 * @param recipeId The unique identifier of the recipe to add.
	 */
	public void addRecipe(int playerId, int recipeId)
	{
		if (DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(playerId, recipeId))
		{
			recipeList.add(recipeId);
		}
	}
	
	/**
	 * Removes a specific recipe from the {@code Player} object.<br>
	 * This method updates the database and notifies the player via a packet.<br>
	 * It only proceeds if the {@code recipeId} exists in the current list.
	 * @param player The {@link Player} who owns the recipe.
	 * @param recipeId The unique identifier of the recipe to remove.
	 */
	public void deleteRecipe(Player player, int recipeId)
	{
		if (recipeList.contains(recipeId))
		{
			if (DAOManager.getDAO(PlayerRecipesDAO.class).delRecipe(player.getObjectId(), recipeId))
			{
				recipeList.remove(recipeId);
				PacketSendUtility.sendPacket(player, new SM_RECIPE_DELETE(recipeId));
			}
		}
	}
	
	/**
	 * Automatically grants recipes to a player based on their race and skill level.<br>
	 * This method checks the {@code DataManager} for matching recipes.<br>
	 * It adds every found {@link RecipeTemplate} to the player's recipe list.
	 * @param player The {@code Player} who will receive the new recipes.
	 * @param skillId The unique identifier for the skill being checked.
	 * @param skillLvl The current level of the skill.
	 */
	public void autoLearnRecipe(Player player, int skillId, int skillLvl)
	{
		for (RecipeTemplate recipe : DataManager.RECIPE_DATA.getAutolearnRecipes(player.getRace(), skillId, skillLvl))
		{
			player.getRecipeList().addRecipe(player, recipe);
		}
	}
	
	/**
	 * Checks if a specific recipe exists in the current list.<br>
	 * This method looks for the provided {@code recipeId}.
	 * @param recipeId The unique identifier of the recipe to check.
	 * @return {@code true} if the recipe is found, otherwise {@code false}.
	 */
	public boolean isRecipePresent(int recipeId)
	{
		return recipeList.contains(recipeId);
	}
	
	/**
	 * Returns the total number of recipes in this list.<br>
	 * This count represents all items currently stored in the {@code recipeList}.
	 * @return The size of the collection as an {@code int}.
	 */
	public int size()
	{
		return recipeList.size();
	}
}
