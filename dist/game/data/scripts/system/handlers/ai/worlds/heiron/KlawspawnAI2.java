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
package system.handlers.ai.worlds.heiron;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

import system.handlers.ai.GeneralNpcAI2;

/**
 * This class defines the artificial intelligence behavior for {@code klawspawn} NPCs in Heiron.<br>
 * It extends {@link GeneralNpcAI2} to provide specific logic for these creatures.
 * @author cheatkiller
 */
@AIName("klawspawn")
public class KlawspawnAI2 extends GeneralNpcAI2
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
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It checks if an NPC with ID {@code 212120} exists in the current world.<br>
	 * If it does not exist, there is a 10% chance to spawn one and die silently.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		final Npc npc = getOwner().getPosition().getWorldMapInstance().getNpc(212120);
		if (npc == null)
		{
			if (Rnd.get(0, 100) < 10)
			{
				spawn(212120, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				AI2Actions.dieSilently(this, creature);
			}
		}
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
		AI2Actions.deleteOwner(this);
	}
}
