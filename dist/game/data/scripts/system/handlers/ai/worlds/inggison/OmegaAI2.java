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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.ai.Percentage;
import com.aionemu.gameserver.model.ai.SummonGroup;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import system.handlers.ai.SummonerAI2;

/**
 * Handles the specific AI behavior for the {@code omega} summon.<br>
 * This class extends {@link SummonerAI2} to define unique actions and logic.<br>
 * It is used by the game server to manage how this entity interacts with players and the world.
 * @author Luzien, xTz
 */
@AIName("omega") // 216516
public class OmegaAI2 extends SummonerAI2
{
	/**
	 * Executes specific skill actions before an NPC is spawned.<br>
	 * This method triggers {@code AI2Actions.useSkill} for IDs {@code 19189} and {@code 19191}.<br>
	 * It handles the initial setup sequence for the summoner.
	 * @param percent The current health percentage used for spawning logic.
	 */
	@Override
	protected void handleBeforeSpawn(Percentage percent)
	{
		AI2Actions.useSkill(this, 19189);
		AI2Actions.useSkill(this, 19191);
	}
	
	/**
	 * This method is called when a {@link SummonGroup} has finished spawning.<br>
	 * It handles the logic required after all summons are created.
	 * @param summonGroup The group of summons that just finished spawning.
	 */
	@Override
	protected void handleSpawnFinished(SummonGroup summonGroup)
	{
		if (summonGroup.getNpcId() == 281948)
		{
			AI2Actions.useSkill(this, 18671);
		}
	}
	
	/**
	 * Checks if there is a {@code Player} within a range of 30.<br>
	 * This method determines if any known player is close enough to trigger an action.
	 * @return {@code true} if at least one player is in range, {@code false} otherwise.
	 */
	@Override
	protected boolean checkBeforeSpawn()
	{
		boolean hit = false;
		for (Player player : getKnownList().getKnownPlayers().values())
		{
			if (isInRange(player, 30))
			{
				hit = true;
				break;
			}
		}
		
		return hit;
	}
}
