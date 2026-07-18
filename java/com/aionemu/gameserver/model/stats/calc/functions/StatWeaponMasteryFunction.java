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
import com.aionemu.gameserver.model.templates.item.WeaponType;

/**
 * This class calculates the weapon mastery bonus for a {@link Player}.<br>
 * It determines the appropriate rate based on the {@code WeaponType} of the equipped item.
 * @author ATracer (based on Mr.Poke WeaponMasteryModifier)
 */
public class StatWeaponMasteryFunction extends StatRateFunction
{
	private final WeaponType weaponType;
	
	/**
	 * Creates a new function to calculate weapon mastery stats.<br>
	 * This class handles specific modifiers based on the {@code WeaponType}.<br>
	 * It extends the base functionality of {@link StatRateFunction}.
	 * @param weaponType The type of weapon this modifier applies to.
	 * @param name The name of the stat being modified.
	 * @param value The numerical amount to apply to the stat.
	 * @param bonus Whether this value should be treated as a bonus.
	 */
	public StatWeaponMasteryFunction(WeaponType weaponType, StatEnum name, int value, boolean bonus)
	{
		super(name, value, bonus);
		this.weaponType = weaponType;
	}
	
	/**
	 * Updates the base value of a {@link Stat2} object.<br>
	 * This method calculates a new value based on the owner's agility.<br>
	 * It uses the internal modifier to adjust the result.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final Player player = (Player) stat.getOwner();
		switch (this.stat)
		{
			case MAIN_HAND_POWER:
				if (player.getEquipment().getMainHandWeaponType() == weaponType)
				{
					super.apply(stat);
				}
				break;
			case OFF_HAND_POWER:
				if (player.getEquipment().getOffHandWeaponType() == weaponType)
				{
					super.apply(stat);
				}
				break;
			default:
				if (player.getEquipment().getMainHandWeaponType() == weaponType)
				{
					super.apply(stat);
				}
				break;
		}
	}
}
