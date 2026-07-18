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

import java.util.Arrays;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.handler.MoveEventHandler;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * Handles the specific AI behavior for the {@code polorserin} NPC.<br>
 * This class extends {@link WalkGeneralRunnerAI2} to provide custom movement and logic.
 * @author Rolandas
 */
@AIName("polorserin")
public class PolorSerinAI2 extends WalkGeneralRunnerAI2
{
	static final int[] stopAdults =
	{
		203129,
		203132
	};
	
	/**
	 * This method is called when an NPC reaches its destination.<br>
	 * It checks if specific adult NPCs are nearby to determine the next action.<br>
	 * If adults are near, it triggers {@code onMoveArrived} and removes the weapon state.<br>
	 * Otherwise, it calls {@code handleMoveArrived} and equips the weapon.
	 */
	@Override
	protected void handleMoveArrived()
	{
		boolean adultsNear = false;
		for (VisibleObject object : getOwner().getKnownList().getKnownObjects().values())
		{
			if (object instanceof Npc)
			{
				final Npc npc = (Npc) object;
				final int npcId = npc.getNpcId();
				if (!Arrays.stream(stopAdults).anyMatch(e -> e == npcId))
				{
					continue;
				}
				
				if (MathUtil.isIn3dRange(npc, getOwner(), getOwner().getAggroRange()))
				{
					adultsNear = true;
					break;
				}
			}
		}
		
		if (adultsNear)
		{
			MoveEventHandler.onMoveArrived(this);
			getOwner().unsetState(CreatureState.WEAPON_EQUIPPED);
		}
		else
		{
			super.handleMoveArrived();
			getOwner().setState(CreatureState.WEAPON_EQUIPPED);
		}
	}
}
