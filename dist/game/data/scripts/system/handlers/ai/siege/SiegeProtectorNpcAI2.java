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
package system.handlers.ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence for {@link Npc} units acting as siege protectors.<br>
 * This class extends {@link SiegeNpcAI2} to provide specific behaviors for defending siege points.
 * @author ATracer, Source
 */
@AIName("siege_protector")
public class SiegeProtectorNpcAI2 extends SiegeNpcAI2
{
	// spawn for quest
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		super.handleDied();
		final int npc = getOwner().getNpcId();
		switch (npc)
		{
			case 259614:
				spawn(701237, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				despawnClaw();
				break;
		}
	}
	
	/**
	 * Removes the claw object from the game world.<br>
	 * This method schedules a deletion task for the NPC with ID {@code 701237}.<br>
	 * The removal occurs after a delay of {@code 300000} milliseconds.
	 */
	private void despawnClaw()
	{
		final Npc claw = getPosition().getWorldMapInstance().getNpc(701237);
		ThreadPoolManager.getInstance().schedule(() -> claw.getController().onDelete(), 60000 * 5);
	}
}
