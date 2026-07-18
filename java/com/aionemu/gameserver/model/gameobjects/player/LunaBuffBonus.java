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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.templates.luna.LunaBonusAttr;
import com.aionemu.gameserver.model.templates.luna.LunaBonusTemplate;
import com.aionemu.gameserver.skillengine.change.Func;

/**
 * Represents a specific bonus granted by the {@link LunaBonusTemplate} to a player.<br>
 * This class handles the calculation of various statistics based on {@link LunaBonusAttr} data.
 */
public class LunaBuffBonus implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	private final LunaBonusTemplate lunaBonusTemplate;
	
	/**
	 * Creates a new {@code LunaBuffBonus} instance.<br>
	 * This constructor loads the template data using the provided ID.
	 * @param bonusId The unique identifier for the luna buff.
	 */
	public LunaBuffBonus(int bonusId)
	{
		lunaBonusTemplate = DataManager.LUNA_BUFF_DATA.getLunaBuffId(bonusId);
	}
	
	/**
	 * Applies the bonus effects to a specific {@link Player}.<br>
	 * This method checks if the {@code lunaBonusTemplate} is valid.<br>
	 * It adds new stat functions based on the template attributes.<br>
	 * Finally, it registers these effects to the player's game stats.
	 * @param player The {@code Player} who will receive the effect.
	 */
	public void applyEffect(Player player)
	{
		if (lunaBonusTemplate == null)
		{
			return;
		}
		
		for (LunaBonusAttr lunaBonusAttr : lunaBonusTemplate.getPenaltyAttr())
		{
			if (lunaBonusAttr.getFunc().equals(Func.PERCENT))
			{
				functions.add(new StatRateFunction(lunaBonusAttr.getStat(), lunaBonusAttr.getValue(), true));
			}
			else
			{
				functions.add(new StatAddFunction(lunaBonusAttr.getStat(), lunaBonusAttr.getValue(), true));
			}
		}
		
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Removes the effect from the specified {@link Player}.<br>
	 * This method clears all internal functions.<br>
	 * It also notifies the player's game stats to stop the effect.
	 * @param player The {@code Player} who currently has this effect applied.
	 */
	public void endEffect(Player player)
	{
		functions.clear();
		player.getGameStats().endEffect(this);
	}
}
