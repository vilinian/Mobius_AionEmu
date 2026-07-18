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
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class provides a collection of utility methods for calculating various player statistics.<br>
 * It contains the logic required to derive complex values from {@link Player} data and {@link StatEnum} types.
 * @author ATracer
 */
public class PlayerStatFunctions
{
	private static final List<IStatFunction> FUNCTIONS = new ArrayList<>();
	static
	{
		FUNCTIONS.add(new PhysicalAttackFunction());
		FUNCTIONS.add(new MagicalAttackFunction());
		FUNCTIONS.add(new AttackSpeedFunction());
		FUNCTIONS.add(new BoostCastingTimeFunction());
		FUNCTIONS.add(new PvPAttackRatioFunction());
		FUNCTIONS.add(new PvPDefendRatioFunction());
		FUNCTIONS.add(new PvPPhysicalAttackRatioFunction());
		FUNCTIONS.add(new PvPMagicalAttackRatioFunction());
		FUNCTIONS.add(new PvPPhysicalDefendRatioFunction());
		FUNCTIONS.add(new PvPMagicalDefendRatioFunction());
		FUNCTIONS.add(new PDefFunction());
		FUNCTIONS.add(new MaxHpFunction());
		FUNCTIONS.add(new MaxMpFunction());
		FUNCTIONS.add(new FlyBoostSpeedFunction());
		
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.BLOCK, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.PARRY, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.EVASION, 0.3f));
		
		// new 7.x
		FUNCTIONS.add(new PvEAttackFunction());
		FUNCTIONS.add(new PvEDefenseFunction());
		FUNCTIONS.add(new PvPAttackFunction());
		FUNCTIONS.add(new PvPDefenseFunction());
	}
	
	/**
	 * Retrieves the list of all registered stat functions.<br>
	 * This method returns the internal {@code FUNCTIONS} list.
	 * @return a {@code List} containing all {@link IStatFunction} objects.
	 */
	public static List<IStatFunction> getFunctions()
	{
		return FUNCTIONS;
	}
	
	/**
	 * Registers the default set of stat functions for a specific player.<br>
	 * This method adds all predefined {@link IStatFunction} objects to the player's stats.<br>
	 * It ensures that the player can calculate various attributes like attack and defense correctly.
	 * @param player The {@code Player} object to receive the stat functions.
	 */
	public static void addPredefinedStatFunctions(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		player.getGameStats().addEffectOnly(null, FUNCTIONS);
	}
}
