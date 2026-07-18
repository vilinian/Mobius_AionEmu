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
package system.handlers.ai.worlds;

import com.aionemu.gameserver.ai2.AIName;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * This class handles the artificial intelligence for Agrint helper NPCs.<br>
 * It extends {@link AggressiveNpcAI2} to provide specific behaviors for these entities.
 * @author xTz
 */
@AIName("helpers_agrint")
public class HelpersAgrintAI2 extends AggressiveNpcAI2
{
	/**
	 * Adjusts the damage value for the owner.<br>
	 * This method overrides the default behavior of {@code modifyDamage}.<br>
	 * It currently returns a fixed value of {@code 1}.
	 * @param damage The original damage amount to be modified.
	 * @return The modified damage result.
	 */
	@Override
	public int modifyOwnerDamage(int damage)
	{
		return 1;
	}
	
	/**
	 * Adjusts the amount of damage dealt by an attack.<br>
	 * This method is used to calculate final damage values.
	 * @param damage The initial damage value to be modified.
	 * @return The multiplier applied to the damage.
	 */
	@Override
	public int modifyDamage(int damage)
	{
		return 1;
	}
}
