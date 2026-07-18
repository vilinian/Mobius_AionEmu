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
package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class calculates the shield mastery effect for a character.<br>
 * It determines how {@link Stat2} values influence the player's shield capabilities.
 * @author VladimirZ
 */
public class StatShieldMasteryFunction extends StatRateFunction
{
	/**
	 * Creates a new {@link StatShieldMasteryFunction} instance.<br>
	 * This constructor initializes the shield mastery calculation logic.
	 * @param name The {@code StatEnum} type of the stat.
	 * @param value The numerical amount to apply.
	 * @param bonus Whether this value should be treated as a bonus.
	 */
	public StatShieldMasteryFunction(StatEnum name, int value, boolean bonus)
	{
		super(name, value, bonus);
	}
	
	/**
	 * Applies the shield mastery bonus to a {@link Stat2} object.<br>
	 * This method checks if the owner has a shield equipped.<br>
	 * If a shield is equipped, it calls the superclass {@code apply} method.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final Player player = (Player) stat.getOwner();
		if (player.getEquipment().isShieldEquipped())
		{
			super.apply(stat);
		}
	}
}
