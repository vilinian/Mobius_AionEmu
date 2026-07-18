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
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;

import system.handlers.ai.NoActionAI2;

/**
 * Handles the artificial intelligence behavior for {@code sacred_image} creatures.<br>
 * This class defines how these specific world entities interact with their environment.
 * @author Steve
 */
@AIName("sacred_image")

// 258280, 258281, 258313, 258314
public class SacredImageAI2 extends NoActionAI2
{
	/**
	 * Handles the logic when this AI sees a {@code Creature}.<br>
	 * It checks the distance between the entities.
	 * @param creature The {@code Creature} that was seen.
	 */
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		checkDistance(creature);
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		checkDistance(creature);
	}
	
	/**
	 * Checks the distance between the owner and a {@link Creature}.<br>
	 * It triggers a skill if the target is within a specific range.<br>
	 * This method ignores certain race combinations based on NPC IDs.
	 * @param creature The {@code Creature} to check against.
	 */
	private void checkDistance(Creature creature)
	{
		final int spellid = getOwner().getNpcId() == 258281 ? 20373 : 20374;
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			if ((player.getRace().equals(Race.ASMODIANS) && (getOwner().getNpcId() == 258281)) || (player.getRace().equals(Race.ELYOS) && (getOwner().getNpcId() == 258280)))
			{
				return;
			}
			
			if (MathUtil.isIn3dRangeLimited(getOwner(), creature, 0, 25))
			{
				SkillEngine.getInstance().getSkill(getOwner(), spellid, 65, player).useNoAnimationSkill();
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
}
