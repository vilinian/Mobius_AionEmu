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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence for siege mine NPCs.<br>
 * This class extends {@link SiegeNpcAI2} to provide specific behaviors for mining entities.
 * @author Source
 */
@AIName("siege_mine")
public class MineAI2 extends SiegeNpcAI2
{
	/**
	 * This method handles the specific actions taken when a {@code Creature} attacks.<br>
	 * It uses skill {@code 18407} on the current AI instance.<br>
	 * It also schedules a task to delete the owner after {@code 1500} milliseconds.
	 * @param creature The {@code Creature} that triggered the aggro behavior.
	 */
	@Override
	protected void handleCreatureAggro(Creature creature)
	{
		AI2Actions.useSkill(this, 18407);
		ThreadPoolManager.getInstance().schedule(() -> AI2Actions.deleteOwner(MineAI2.this), 1500);
	}
}
