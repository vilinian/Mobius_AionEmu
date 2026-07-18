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
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class calculates the {@code Dual Weapon Mastery} stat for a player.<br>
 * It handles the specific logic required to determine this value based on character attributes and equipment.
 * @author ATracer
 */
public class StatDualWeaponMasteryFunction extends StatFunctionProxy
{
	/**
	 * Creates a new {@code StatDualWeaponMasteryFunction} instance.<br>
	 * This constructor links an {@link Effect} with a specific {@code IStatFunction}.<br>
	 * It allows for the calculation of dual weapon mastery stats based on an effect.
	 * @param effect The {@link Effect} that triggers this stat calculation.
	 * @param statFunction The underlying {@code IStatFunction} to be used.
	 */
	public StatDualWeaponMasteryFunction(Effect effect, IStatFunction statFunction)
	{
		super(effect, statFunction);
	}
	
	/**
	 * Applies the dual weapon mastery effect to a {@link Stat2} object.<br>
	 * This method checks if the player has a dual weapon equipped in the sub-hand slot.<br>
	 * If the condition is met, it calls the superclass {@code apply} method.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final Player player = (Player) stat.getOwner();
		if (player.getEquipment().hasDualWeaponEquipped(ItemSlot.SUB_HAND))
		{
			super.apply(stat);
		}
	}
}
