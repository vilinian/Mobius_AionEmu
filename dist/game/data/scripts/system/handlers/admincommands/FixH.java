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
package system.handlers.admincommands;

import java.io.IOException;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the {@code FixH} admin command to repair hitboxes in the game world.<br>
 * This utility helps synchronize object positions with their collision data for {@link Npc} and {@link VisibleObject} entities.
 */
public class FixH extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link FixH} command.<br>
	 * This class handles the {@code fixh} admin command.
	 */
	public FixH()
	{
		super("fixh");
	}
	
	/**
	 * Fixes the spawn of a targeted {@code Npc}.<br>
	 * It deletes the current NPC and recreates it at its original location.<br>
	 * The new spawn is saved to the database as permanent.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings for additional arguments.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (admin.getAccessLevel() < 1)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to use this command!");
			return;
		}
		
		if (admin.getTarget() != null)
		{
			if (admin.getTarget() instanceof Npc)
			{
				final Npc target = (Npc) admin.getTarget();
				final SpawnTemplate temp = target.getSpawn();
				final int respawnTime = 295;
				final boolean permanent = true;
				
				// delete spawn,npc
				target.getController().delete();
				
				// spawn npc
				final int templateId = temp.getNpcId();
				final float x = temp.getX();
				final float y = temp.getY();
				final float z = temp.getZ();
				byte heading = admin.getHeading();
				if (heading > 60)
				{
					heading -= 60;
				}
				else
				{
					heading += 60;
				}
				
				final int worldId = temp.getWorldId();
				
				final SpawnTemplate spawn = SpawnEngine.addNewSpawn(worldId, templateId, x, y, z, heading, respawnTime);
				
				if (spawn == null)
				{
					PacketSendUtility.sendMessage(admin, "There is no template with id " + templateId);
					return;
				}
				
				final VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, admin.getInstanceId());
				
				if (visibleObject == null)
				{
					PacketSendUtility.sendMessage(admin, "npc id " + templateId + " was not found!");
				}
				else if (permanent)
				{
					try
					{
						DataManager.SPAWNS_DATA2.saveSpawn(admin, visibleObject, false);
					}
					catch (IOException e)
					{
						PacketSendUtility.sendMessage(admin, "Could not save spawn");
					}
				}
				
				if (visibleObject != null)
				{
					final String objectName = visibleObject.getObjectTemplate().getName();
					PacketSendUtility.sendMessage(admin, objectName + "FixH");
				}
			}
			
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "Only in target!");
		}
	}
	
	/**
	 * Spawns a new NPC into the game world.<br>
	 * This method creates a {@link SpawnTemplate} and adds it to the server.<br>
	 * It returns the newly created object.
	 * @param npcId The unique identifier for the NPC type.
	 * @param mapId The ID of the map where the NPC will appear.
	 * @param instanceId The specific instance ID for the location.
	 * @param x The X coordinate for the spawn position.
	 * @param y The Y coordinate for the spawn position.
	 * @param z The Z coordinate for the spawn position.
	 * @param heading The direction the NPC faces.
	 * @param walkerId The ID of the walking animation to use.
	 * @param walkerIdx The index of the specific walk animation.
	 * @param respawnTime The time in seconds before the NPC respawns.
	 * @return The {@code VisibleObject} created by the spawn engine.
	 */
	protected VisibleObject spawn(int npcId, int mapId, int instanceId, float x, float y, float z, byte heading, String walkerId, int walkerIdx, int respawnTime)
	{
		final SpawnTemplate template = SpawnEngine.addNewSpawn(mapId, npcId, x, y, z, heading, respawnTime);
		return SpawnEngine.spawnObject(template, instanceId);
	}
	
}
