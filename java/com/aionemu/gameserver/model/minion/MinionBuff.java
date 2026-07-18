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
package com.aionemu.gameserver.model.minion;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.templates.minion.MinionAttr;
import com.aionemu.gameserver.model.templates.minion.MinionTemplate;
import com.aionemu.gameserver.skillengine.change.Func;

/**
 * Represents a buff applied to a {@link MinionTemplate}.<br>
 * It manages the statistics and effects granted to a minion.<br>
 * This class implements {@link StatOwner} to handle stat calculations.
 */
public class MinionBuff implements StatOwner
{
	private MinionTemplate minionTemplate;
	private final List<IStatFunction> functions = new ArrayList<>();
	
	/**
	 * Applies a specific minion buff to a player.<br>
	 * This method identifies the correct attributes based on the player's class type.<br>
	 * It then adds the corresponding stat functions to the player's game stats.
	 * @param player The {@link Player} who will receive the buff.
	 * @param minionId The unique identifier for the minion template to use.
	 */
	public void apply(Player player, int minionId)
	{
		if (minionId == 0)
		{
			return;
		}
		
		minionTemplate = DataManager.MINION_DATA.getMinionTemplate(minionId);
		List<MinionAttr> attribute = null;
		if (player.isMagicalTypeClass())
		{
			attribute = minionTemplate.getMagicalAttr();
		}
		else
		{
			attribute = minionTemplate.getPhysicalAttr();
		}
		
		for (MinionAttr minionAttribute : attribute)
		{
			if (minionAttribute.getFunc().equals(Func.PERCENT))
			{
				functions.add(new StatRateFunction(minionAttribute.getStat(), minionAttribute.getValue(), true));
			}
			else
			{
				functions.add(new StatAddFunction(minionAttribute.getStat(), minionAttribute.getValue(), true));
			}
		}
		
		player.setBonus(true);
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Removes the buff from the specified {@link Player}.<br>
	 * Clears all associated functions and sets the bonus to {@code false}.<br>
	 * Notifies the game stats to end the effect.
	 * @param player The {@code Player} instance to update.
	 */
	public void end(Player player)
	{
		functions.clear();
		player.setBonus(false);
		player.getGameStats().endEffect(this);
	}
}
