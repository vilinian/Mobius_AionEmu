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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the results of a magic crafting attempt.<br>
 * It informs the client about whether the craft succeeded or failed based on the {@code RecipeTemplate}.
 * @author Falke_34, FrozenKiller
 */
public class SM_MAGIC_CRAFT extends AionServerPacket
{
	private final int action;
	private final int recipe;
	private final int timerPeriod = 4000;
	
	/**
	 * This packet handles the magic crafting action.<br>
	 * It uses a {@link RecipeTemplate} to identify the item being crafted.<br>
	 * The {@code action} value determines the specific type of craft.
	 * @param action The unique identifier for the crafting action.
	 * @param recipeTemplate The template containing the recipe data.
	 */
	public SM_MAGIC_CRAFT(int action, RecipeTemplate recipeTemplate)
	{
		this.action = action;
		recipe = recipeTemplate.getId();
	}
	
	/**
	 * This packet handles the magic craft action.<br>
	 * It sends the specific action and recipe ID to the client.
	 * @param action The type of action being performed.
	 * @param recipe The unique identifier for the crafting recipe.
	 */
	public SM_MAGIC_CRAFT(int action, int recipe)
	{
		this.action = action;
		this.recipe = recipe;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action);
		switch (action)
		{
			case 0:
				writeD(recipe);
				writeD(timerPeriod); // Time
				break;
			case 1:
				writeD(0);
				writeD(0);
				break;
			case 2:
				writeD(recipe);
				writeD(0);
				break;
			case 3: // Proc
				writeD(recipe);
				writeD(0);
				break;
		}
	}
}
