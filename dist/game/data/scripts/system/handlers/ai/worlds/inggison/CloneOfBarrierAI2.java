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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * This class handles the artificial intelligence for the {@code omegaclone} NPC.<br>
 * It extends {@link AggressiveNpcAI2} to provide specific behaviors for this entity in the Inggison world.
 * @author Luzien
 */
@AIName("omegaclone") // 281948
public class CloneOfBarrierAI2 extends AggressiveNpcAI2
{
	/**
	 * This method is called when the NPC dies.<br>
	 * It removes a specific effect from nearby NPCs if they are alive.<br>
	 * Finally, it calls {@code handleDied}.
	 */
	@Override
	protected void handleDied()
	{
		for (VisibleObject object : getKnownList().getKnownObjects().values())
		{
			if ((object instanceof Npc) && isInRange(object, 5))
			{
				final Npc npc = (Npc) object;
				if ((npc.getNpcId() == 216516) && !npc.getLifeStats().isAlreadyDead())
				{
					npc.getEffectController().removeEffect(18671);
					break;
				}
			}
		}
		
		super.handleDied();
	}
}
