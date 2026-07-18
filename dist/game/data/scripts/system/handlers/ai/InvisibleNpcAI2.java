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
package system.handlers.ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;

/**
 * Handles the artificial intelligence for NPCs that are currently invisible.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behaviors for hidden entities.
 * @author xTz
 */
@AIName("invisible_npc")
public class InvisibleNpcAI2 extends AggressiveNpcAI2
{
	/**
	 * This method is called when the AI entity is first spawned.<br>
	 * It checks if the NPC is alive before performing initial actions.<br>
	 * It uses skill {@code 19493} and sets the visual state to {@code HIDE1}.
	 */
	@Override
	public void handleSpawned()
	{
		super.handleSpawned();
		if (!isAlreadyDead())
		{
			AI2Actions.useSkill(InvisibleNpcAI2.this, 19493);
			getOwner().setVisualState(CreatureVisualState.HIDE1);
		}
	}
	
	/**
	 * Handles the logic when this AI is attacked by a {@code Creature}.<br>
	 * It removes specific effects from the owner.<br>
	 * It also updates the visual state of the owner to be visible.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		getEffectController().removeEffect(19493);
		getOwner().unsetVisualState(CreatureVisualState.HIDE1);
		getOwner().getEffectController().removeAllEffects();
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		super.handleBackHome();
		if (!isAlreadyDead())
		{
			AI2Actions.useSkill(InvisibleNpcAI2.this, 19493);
			getOwner().setVisualState(CreatureVisualState.HIDE1);
		}
	}
}
