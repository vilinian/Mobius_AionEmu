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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to update spawn data in the game world.<br>
 * It allows administrators to refresh {@link SpawnTemplate} information dynamically.
 * @author KID
 * @modified Rolandas
 */
public class SpawnUpdate extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link SpawnUpdate} class.<br>
	 * This command allows administrators to update spawn data in the game world.
	 */
	public SpawnUpdate()
	{
		super("spawnu");
	}
	
	/**
	 * Updates the coordinates or walker ID of a targeted {@code Npc}.<br>
	 * The command requires the admin to target an {@code Npc} and provide specific parameters.<br>
	 * It supports setting x, y, z, h, xyz, xyzh, or w values.<br>
	 * Changes are saved to the spawn data and synchronized with the client.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings starting with "set" followed by the coordinate type and optional value.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params[0].equalsIgnoreCase("set"))
		{
			Npc npc = null;
			if ((admin.getTarget() != null) && (admin.getTarget() instanceof Npc))
			{
				npc = (Npc) admin.getTarget();
			}
			
			if (npc == null)
			{
				PacketSendUtility.sendMessage(admin, "you need to target Npc type.");
				return;
			}
			
			final SpawnTemplate spawn = npc.getSpawn();
			
			if (params[1].equalsIgnoreCase("x"))
			{
				float x;
				if (params.length < 3)
				{
					x = admin.getX();
				}
				else
				{
					x = Float.parseFloat(params[2]);
				}
				
				npc.getPosition().setXYZH(x, null, null, null);
				PacketSendUtility.sendPacket(admin, new SM_DELETE(npc, 0));
				PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
				PacketSendUtility.sendMessage(admin, "updated npcs x to " + x + ".");
				try
				{
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					PacketSendUtility.sendMessage(admin, "Could not save spawn");
				}
				return;
			}
			
			if (params[1].equalsIgnoreCase("y"))
			{
				float y;
				if (params.length < 3)
				{
					y = admin.getY();
				}
				else
				{
					y = Float.parseFloat(params[2]);
				}
				
				npc.getPosition().setXYZH(null, y, null, null);
				PacketSendUtility.sendPacket(admin, new SM_DELETE(npc, 0));
				PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
				PacketSendUtility.sendMessage(admin, "updated npcs y to " + y + ".");
				try
				{
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					PacketSendUtility.sendMessage(admin, "Could not save spawn");
				}
				return;
			}
			
			if (params[1].equalsIgnoreCase("z"))
			{
				float z;
				if (params.length < 3)
				{
					z = admin.getZ();
				}
				else
				{
					z = Float.parseFloat(params[2]);
				}
				
				npc.getPosition().setZ(z);
				PacketSendUtility.sendPacket(admin, new SM_DELETE(npc, 0));
				PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
				PacketSendUtility.sendMessage(admin, "updated npcs z to " + z + ".");
				try
				{
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					PacketSendUtility.sendMessage(admin, "Could not save spawn");
				}
				return;
			}
			
			if (params[1].equalsIgnoreCase("h"))
			{
				byte h;
				if (params.length < 3)
				{
					byte heading = admin.getHeading();
					if (heading > 60)
					{
						heading -= 60;
					}
					else
					{
						heading += 60;
					}
					
					h = heading;
				}
				else
				{
					h = Byte.parseByte(params[2]);
				}
				
				npc.getPosition().setH(h);
				PacketSendUtility.sendPacket(admin, new SM_DELETE(npc, 0));
				PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
				PacketSendUtility.sendMessage(admin, "updated npcs heading to " + h + ".");
				try
				{
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					PacketSendUtility.sendMessage(admin, "Could not save spawn");
				}
				return;
			}
			
			if (params[1].equalsIgnoreCase("xyz"))
			{
				PacketSendUtility.sendPacket(admin, new SM_DELETE(npc, 0));
				npc.getPosition().setXYZH(admin.getX(), null, null, null);
				try
				{
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
					PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
					npc.getPosition().setXYZH(null, admin.getY(), null, null);
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
					PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
					npc.getPosition().setXYZH(null, null, admin.getZ(), null);
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
					PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
					PacketSendUtility.sendMessage(admin, "updated npcs coordinates to " + admin.getX() + ", " + admin.getY() + ", " + admin.getZ() + ".");
				}
				catch (IOException e)
				{
					e.printStackTrace();
					PacketSendUtility.sendMessage(admin, "Could not save spawn");
				}
				return;
			}
			
			if (params[1].equalsIgnoreCase("xyzh"))
			{
				PacketSendUtility.sendPacket(admin, new SM_DELETE(npc, 0));
				npc.getPosition().setXYZH(admin.getX(), null, null, null);
				try
				{
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
					PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
					npc.getPosition().setXYZH(null, admin.getY(), null, null);
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
					PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
					npc.getPosition().setXYZH(null, null, admin.getZ(), null);
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
					PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
					npc.getPosition().setXYZH(null, null, null, admin.getHeading());
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
					PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
					PacketSendUtility.sendMessage(admin, "updated npcs coordinates to " + admin.getX() + ", " + admin.getY() + ", " + admin.getZ() + "," + admin.getHeading() + ".");
				}
				catch (IOException e)
				{
					e.printStackTrace();
					PacketSendUtility.sendMessage(admin, "Could not save spawn");
				}
				return;
			}
			
			if (params[1].equalsIgnoreCase("w"))
			{
				String walkerId = null;
				if (params.length == 3)
				{
					walkerId = params[2].toUpperCase();
				}
				
				if (walkerId != null)
				{
					final WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(walkerId);
					if (template == null)
					{
						PacketSendUtility.sendMessage(admin, "No such template exists in npc_walker.xml.");
						return;
					}
					
					final String walkerIdValue = walkerId;
					final List<SpawnGroup2> allSpawns = DataManager.SPAWNS_DATA2.getSpawnsByWorldId(npc.getWorldId());
					final List<SpawnTemplate> allSpots = allSpawns.stream().flatMap(sg -> sg.getSpawnTemplates().stream()).collect(Collectors.toList());
					final List<SpawnTemplate> sameIds = allSpots.stream().filter(st -> Objects.equals(st.getWalkerId(), walkerIdValue)).collect(Collectors.toList());
					if (sameIds.size() >= template.getPool())
					{
						PacketSendUtility.sendMessage(admin, "Can not assign, walker pool reached the limit.");
						return;
					}
				}
				
				spawn.setWalkerId(walkerId);
				PacketSendUtility.sendPacket(admin, new SM_DELETE(npc, 0));
				PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
				if (walkerId == null)
				{
					PacketSendUtility.sendMessage(admin, "removed npcs walker_id for " + npc.getNpcId() + ".");
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "updated npcs walker_id to " + walkerId + ".");
				}
				
				try
				{
					DataManager.SPAWNS_DATA2.saveSpawn(admin, npc, false);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					PacketSendUtility.sendMessage(admin, "Could not save spawn");
				}
			}
		}
	}
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "<usage //spawnu set (x | y | z | h | w | xyz)");
	}
}
