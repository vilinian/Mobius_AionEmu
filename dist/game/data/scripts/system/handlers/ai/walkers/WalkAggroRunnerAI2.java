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
package system.handlers.ai.walkers;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the AI logic for aggressive runner NPCs that move while attacking.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific walking behaviors.
 * @author Rolandas
 */
@AIName("aggrorunner")
public class WalkAggroRunnerAI2 extends AggressiveNpcAI2
{
	/**
	 * This method is called when an NPC reaches its destination.<br>
	 * It triggers the logic for completing a movement action.<br>
	 * It calls {@code onMoveArrived} to process the event.
	 */
	@Override
	protected void handleMoveArrived()
	{
		super.handleMoveArrived();
		getOwner().setState(CreatureState.WEAPON_EQUIPPED);
	}
}
