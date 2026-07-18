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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * This class handles the calculation of stats that are duplicated from other sources.<br>
 * It ensures that specific {@link StatEnum} values are correctly processed when they appear multiple times.
 */
class DuplicateStatFunction extends StatFunction
{
	/**
	 * Applies weapon and fusioned item modifiers to a {@link Stat2} object.<br>
	 * This method identifies the best applicable modifier from the player's weapons.<br>
	 * It handles specific logic for PVP ratios versus standard stats.
	 * @param stat The {@code Stat2} object to be updated.
	 */
	@Override
	public void apply(Stat2 stat)
	{
		final Item mainWeapon = ((Player) stat.getOwner()).getEquipment().getMainHandWeapon();
		final Item offWeapon = ((Player) stat.getOwner()).getEquipment().getOffHandWeapon();
		
		if (mainWeapon != null)
		{
			StatFunction func1 = null;
			StatFunction func2 = null;
			final List<StatFunction> functions = new ArrayList<>();
			final List<StatFunction> functions1 = mainWeapon.getItemTemplate().getModifiers();
			
			if (functions1 != null)
			{
				final List<StatFunction> f1 = getFunctions(functions1, stat, mainWeapon);
				if (!f1.isEmpty())
				{
					func1 = f1.get(0);
					functions.addAll(f1);
				}
			}
			
			if (mainWeapon.hasFusionedItem())
			{
				final ItemTemplate template = mainWeapon.getFusionedItemTemplate();
				final List<StatFunction> functions2 = template.getModifiers();
				if (functions2 != null)
				{
					final List<StatFunction> f2 = getFunctions(functions2, stat, mainWeapon);
					if (!f2.isEmpty())
					{
						func2 = f2.get(0);
						functions.addAll(f2);
					}
				}
			}
			else if (offWeapon != null)
			{
				final List<StatFunction> functions2 = offWeapon.getItemTemplate().getModifiers();
				if (functions2 != null)
				{
					functions.addAll(getFunctions(functions2, stat, offWeapon));
				}
			}
			
			if ((func1 != null) && (func2 != null))
			{
				// for fusioned weapons
				if (Math.abs(func1.getValue()) >= Math.abs(func2.getValue()))
				{
					functions.remove(func2);
				}
				else
				{
					functions.remove(func1);
				}
			}
			
			if (!functions.isEmpty())
			{
				if ((getName() == StatEnum.PVP_ATTACK_RATIO) || (getName() == StatEnum.PVP_DEFEND_RATIO))
				{
					for (StatFunction function : functions)
					{
						function.apply(stat);
					}
				}
				else
				{
					functions.stream().max(Comparator.comparingInt(StatFunction::getValue)).orElse(null).apply(stat);
				}
				
				functions.clear();
			}
		}
	}
	
	/**
	 * Filters a list of {@link StatFunction} objects based on specific criteria.<br>
	 * It checks if the function name matches and validates it against the provided {@code stat} and {@code item}.
	 * @param list The original list of functions to filter.
	 * @param stat The current statistic being calculated.
	 * @param item The item associated with the calculation.
	 * @return A new list containing only the valid functions.
	 */
	private List<StatFunction> getFunctions(List<StatFunction> list, Stat2 stat, Item item)
	{
		final List<StatFunction> functions = new ArrayList<>();
		for (StatFunction func : list)
		{
			final StatFunctionProxy func2 = new StatFunctionProxy(item, func);
			if ((func.getName() == getName()) && func2.validate(stat, func2))
			{
				functions.add(func);
			}
		}
		
		return functions;
	}
	
	/**
	 * Returns the execution priority of this function.<br>
	 * This value determines the order in which functions are applied.<br>
	 * The current priority is set to {@code 60}.
	 * @return The integer priority level.
	 */
	@Override
	public int getPriority()
	{
		return 60;
	}
}
