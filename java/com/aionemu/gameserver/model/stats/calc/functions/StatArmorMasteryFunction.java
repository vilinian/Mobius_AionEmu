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
import com.aionemu.gameserver.model.templates.item.ArmorType;

/**
 * This class calculates the armor mastery modifier for a character.<br>
 * It determines how much additional defense is granted based on the {@link ArmorType} of the equipped gear.
 * @author ATracer (based on Mr.Poke ArmorMasteryModifier)
 */
public class StatArmorMasteryFunction extends StatRateFunction
{
	private final ArmorType armorType;
	
	/**
	 * Creates a new {@link StatArmorMasteryFunction} to calculate stats based on armor mastery.<br>
	 * This function applies specific modifiers to the player's statistics.
	 * @param armorType The type of armor used for this calculation.
	 * @param name The name of the statistic being modified.
	 * @param value The numerical amount to apply.
	 * @param bonus Whether this value should be treated as a bonus.
	 */
	public StatArmorMasteryFunction(ArmorType armorType, StatEnum name, int value, boolean bonus)
	{
		super(name, value, bonus);
		this.armorType = armorType;
	}
	
	/**
	 * Updates the {@link Stat2} object based on armor mastery.<br>
	 * This method checks if the player is wearing the required {@code armorType}.<br>
	 * If the equipment is equipped, it applies the calculation.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final Player player = (Player) stat.getOwner();
		if (player.getEquipment().isArmorEquipped(armorType))
		{
			super.apply(stat);
		}
	}
}
