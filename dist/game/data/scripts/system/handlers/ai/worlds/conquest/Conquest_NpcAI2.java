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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.ConquestSpawnManager;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldPosition;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for NPCs located within conquest zones.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behaviors for these entities.
 * @author Falke_34, CoolyT
 */
@AIName("conquest_npc")
public class Conquest_NpcAI2 extends AggressiveNpcAI2
{
	/**
	 * This method is called when the NPC dies.<br>
	 * It handles specific respawn logic for conquest monsters.<br>
	 * It determines which new object to spawn based on the current NPC ID.<br>
	 * Finally, it calls {@code reSpawn} to refresh the entity.
	 */
	@Override
	protected void handleDied()
	{
		final WorldPosition p = getPosition();
		final int cId = getNpcId();
		int npcId = 0;
		
		final List<Integer> npcs = new ArrayList<>();
		final List<Integer> portals = new ArrayList<>();
		
		npcs.add(856175); // Pawrunerk
		npcs.add(856176); // Chitrunerk
		npcs.add(856177); // Rapirunerk
		npcs.add(856178); // Dandrunerk
		
		if (((cId >= 236331) && (cId <= 236334)) || ((cId >= 236359) && (cId <= 236362))) // Shugo Monster
		{
			// Inggison Portals
			portals.add(833018); // Secret Portal
			portals.add(833019); // Questionable Portal
			
			final int index = Rnd.get(0, 1);
			npcId = portals.get(index);
		}
		else if (((cId >= 236387) && (cId <= 236390)) || ((cId >= 236415) && (cId <= 236418))) // Owl Monster
		{
			// Gelkmaros Portals
			portals.add(833021); // Secret Portal
			portals.add(833022); // Questionable Portal
			
			final int index = Rnd.get(0, 1);
			npcId = portals.get(index);
		}
		else // it wasn't a Shugo or a Owl Monster
		{
			final int chance = Rnd.get(100);
			if (chance <= 30) // 30% Chance that a Buff Shugo appears.
			{
				final int index = Rnd.get(0, npcs.size() - 1);
				npcId = npcs.get(index);
			}
		}
		
		if (npcId <= 0)
		{
			reSpawn();
			return;
		}
		
		final VisibleObject obj = spawn(npcId, p.getX(), p.getY(), p.getZ(), (byte) 0);
		
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if ((obj != null) && obj.isSpawned())
			{
				obj.getController().delete();
			}
		}, 120000); // 2 Minutes.
		reSpawn();
	}
	
	/**
	 * Handles the logic for respawning an NPC.<br>
	 * It retrieves the {@code SpawnTemplate} and creates a new instance via {@link ConquestSpawnManager}.<br>
	 * This method is called when an NPC needs to be placed back into the world.
	 */
	private void reSpawn()
	{
		final SpawnTemplate st = getSpawnTemplate();
		final WorldPosition spawnPos = new WorldPosition(st.getWorldId());
		spawnPos.setXYZH(st.getX(), st.getY(), st.getZ(), st.getHeading());
		ConquestSpawnManager.spawnByLoc(spawnPos);
		super.handleDied();
	}
}
