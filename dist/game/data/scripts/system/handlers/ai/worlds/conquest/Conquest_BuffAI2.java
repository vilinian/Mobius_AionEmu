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
package system.handlers.ai.worlds.conquest;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Handles the artificial intelligence for conquest buff NPCs.<br>
 * This class manages how these entities behave within the {@code conquest} world system.
 * @author Falke_34, CoolyT
 */
@AIName("conquest_buff")
public class Conquest_BuffAI2 extends NpcAI2
{
	Map<Integer, Integer> skillByNpc = new HashMap<>();
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		final Creature shugo = getOwner();
		final int npcId = shugo.getObjectTemplate().getTemplateId();
		
		// Npc | Skill
		skillByNpc.put(856175, 21924); // Pawrunerk - Boost Attack Power
		skillByNpc.put(856176, 21925); // Chitrunerk - Movement Speed Increase
		skillByNpc.put(856177, 21926); // Rapirunerk - Attack Speed/Casting Speed Increase
		skillByNpc.put(856178, 21927); // Dandrunerk - Boost Defense
		
		if (!skillByNpc.containsKey(npcId))
		{
			return;
		}
		
		final int skillId = skillByNpc.get(npcId);
		
		shugo.setTarget(player);
		shugo.getController().useSkill(skillId, 1); // Boost Defense
		shugo.getController().delete();
	}
}
