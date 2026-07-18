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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles all logic related to crafting recipes in the game.<br>
 * It manages how {@link RecipeTemplate} objects are processed and used by players.
 * @author KID
 */
public class RecipeService
{
	/**
	 * Checks if a {@link Player} can learn a specific recipe.<br>
	 * It verifies the player's capacity, race, and required skills.<br>
	 * Returns the {@code RecipeTemplate} if all conditions are met.
	 * @param player The {@code Player} attempting to learn the recipe.
	 * @param recipeId The unique identifier for the recipe.
	 * @return The valid {@code RecipeTemplate} or {@code null} if validation fails.
	 */
	public static RecipeTemplate validateNewRecipe(Player player, int recipeId)
	{
		if (player.getRecipeList().size() >= 1600)
		{
			PacketSendUtility.sendMessage(player, "You are unable to have more than 1600 recipes at the same time.");
			return null;
		}
		
		final RecipeTemplate template = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		if (template == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_RECIPEITEM_CANT_USE_NO_RECIPE);
			return null;
		}
		
		if (template.getRace() != Race.PC_ALL)
		{
			if (template.getRace() != player.getRace())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFTRECIPE_RACE_CHECK);
				return null;
			}
		}
		
		if (player.getRecipeList().isRecipePresent(recipeId))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_LEARNED_ALREADY);
			return null;
		}
		
		if (!player.getSkillList().isSkillPresent(template.getSkillid()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_CANT_LEARN_SKILL(DataManager.SKILL_DATA.getSkillTemplate(template.getSkillid()).getNameId()));
			return null;
		}
		
		if (template.getSkillpoint() > player.getSkillList().getSkillLevel(template.getSkillid()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_CANT_LEARN_SKILLPOINT);
			return null;
		}
		
		return template;
	}
	
	/**
	 * Adds a new recipe to a {@link Player}.<br>
	 * This method checks if the recipe exists before adding it.<br>
	 * It can optionally use {@code int)} for extra checks.
	 * @param player The {@code Player} who will receive the recipe.
	 * @param recipeId The unique ID of the recipe to add.
	 * @param useValidation Set to {@code true} to run validation logic.
	 * @return {@code true} if the recipe was added successfully, otherwise {@code false}.
	 */
	public static boolean addRecipe(Player player, int recipeId, boolean useValidation)
	{
		RecipeTemplate template = null;
		if (useValidation)
		{
			template = validateNewRecipe(player, recipeId);
		}
		else
		{
			template = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		}
		
		if (template == null)
		{
			return false;
		}
		
		player.getRecipeList().addRecipe(player, template);
		return true;
	}
}
