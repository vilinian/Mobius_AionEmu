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
package system.handlers.ai.quests;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the specific AI behavior for the Scarecrow NPC used in certain quests.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specialized logic for quest-related interactions.
 * @author FrozenKiller
 */
@AIName("examscarecrow") // NPC: 836530, 806814 QUEST:60003, 70003
public class ExamScareCrowAI2 extends AggressiveNpcAI2
{
	int rewardCount = Rnd.get(3, 10);
	int attackCount = 0;
	
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
	 * Handles the logic when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the attacker is a {@link Player} with specific quest requirements.<br>
	 * If the player meets the criteria, it tracks attacks and grants a reward item.<br>
	 * The reward count is reset after each successful distribution.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			switch (player.getRace())
			{
				case ASMODIANS:
				{
					if (player.getQuestStateList().hasQuest(70003) && (player.getInventory().getItemCountByItemId(182216395) < 1))
					{
						attackCount++;
						if (attackCount == rewardCount)
						{
							ItemService.addItem(player, 182216395, 1);
							rewardCount = Rnd.get(3, 10);
							attackCount = 0;
						}
					}
					break;
				}
				case ELYOS:
				{
					if (player.getQuestStateList().hasQuest(60003) && (player.getInventory().getItemCountByItemId(182216247) < 1))
					{
						attackCount++;
						if (attackCount == rewardCount)
						{
							ItemService.addItem(player, 182216247, 1);
							rewardCount = Rnd.get(3, 10);
							attackCount = 0;
						}
					}
					break;
				}
				default:
					break;
			}
		}
	}
}
