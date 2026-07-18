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
package system.handlers.ai.worlds.inggison;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;

import system.handlers.ai.GeneralNpcAI2;

/**
 * Handles the artificial intelligence for the {@code hugeegg} NPC.<br>
 * This class extends {@link GeneralNpcAI2} to define specific behaviors for these entities.
 * @author Cheatkiller
 */
@AIName("hugeegg")

// 217095, 217096
public class HugeEggAI2 extends GeneralNpcAI2
{
	/**
	 * Checks if the AI is currently allowed to process logic.<br>
	 * This method returns the current state of the {@code canThink} variable.
	 * @return {@code true} if the AI can think, or {@code false} otherwise.
	 */
	@Override
	public boolean canThink()
	{
		return false;
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
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		super.handleDied();
		if (Rnd.get(0, 100) < 50)
		{
			spawn(217097, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
			AI2Actions.deleteOwner(this);
		}
	}
}
