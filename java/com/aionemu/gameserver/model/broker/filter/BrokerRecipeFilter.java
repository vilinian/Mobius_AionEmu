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
package com.aionemu.gameserver.model.broker.filter;

import java.util.Arrays;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.CraftLearnAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;

/**
 * This class filters {@link RecipeTemplate} objects within the broker system.<br>
 * It ensures that only valid recipes are displayed to players based on specific criteria.
 * @author xTz
 */
public class BrokerRecipeFilter extends BrokerFilter
{
	private final int craftSkillId;
	private final int[] masks;
	
	/**
	 * Creates a new filter for broker recipes.<br>
	 * This filter uses a specific skill and bitmask to identify valid items.<br>
	 * It helps the system decide which items should be shown based on crafting rules.
	 * @param craftSkillId The unique identifier for the required crafting skill.
	 * @param masks A variable number of integer bitmasks used to further refine the filter.
	 */
	public BrokerRecipeFilter(int craftSkillId, int... masks)
	{
		this.craftSkillId = craftSkillId;
		this.masks = masks;
	}
	
	/**
	 * Checks if the item matches the required craft skill and mask.<br>
	 * It validates the {@link ItemTemplate} against specific recipe criteria.
	 * @param template The {@code ItemTemplate} to check.
	 * @return {@code true} if the item matches the filter, otherwise {@code false}.
	 */
	@Override
	public boolean accept(ItemTemplate template)
	{
		final ItemActions actions = template.getActions();
		if (actions != null)
		{
			final CraftLearnAction craftAction = actions.getCraftLearnAction();
			if (craftAction != null)
			{
				final int id = craftAction.getRecipeId();
				final RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(id);
				if ((recipeTemplate != null) && (recipeTemplate.getSkillid() == craftSkillId))
				{
					final int value = template.getTemplateId() / 100000;
					return Arrays.stream(masks).anyMatch(e -> e == value);
				}
			}
		}
		
		return false;
	}
}
