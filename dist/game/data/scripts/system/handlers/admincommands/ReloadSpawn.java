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

import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Handles the admin command to reload spawn data.<br>
 * This class refreshes {@link SpawnEngine} configurations without restarting the server.
 * @author Luno, reworked Bobobear
 */
public class ReloadSpawn extends AdminCommand
{
	/**
	 * Initializes the {@link ReloadSpawn} command.<br>
	 * This constructor registers the command with the name {@code reload_spawn}.
	 */
	public ReloadSpawn()
	{
		super("reload_spawn");
	}
	
	/**
	 * Reloads the spawn data for a specific map or all maps.<br>
	 * It identifies the target map by parsing the provided parameters.<br>
	 * If {@code All} is specified, it reloads every registered map.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the name of the location to reload.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		int worldId;
		String destination;
		
		worldId = 0;
		destination = "null";
		
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax //reload_spawn <location name | all>");
		}
		else
		{
			final StringBuilder sbDestination = new StringBuilder();
			for (String p : params)
			{
				sbDestination.append(p + " ");
			}
			
			destination = sbDestination.toString().trim();
			
			if (destination.equalsIgnoreCase("Sanctum"))
			{
				worldId = WorldMapType.SANCTUM.getId();
			}
			else if (destination.equalsIgnoreCase("Kaisinel"))
			{
				worldId = WorldMapType.KAISINEL_ACADEMY.getId();
			}
			else if (destination.equalsIgnoreCase("Poeta"))
			{
				worldId = WorldMapType.POETA.getId();
			}
			else if (destination.equalsIgnoreCase("Verteron"))
			{
				worldId = WorldMapType.VERTERON.getId();
			}
			else if (destination.equalsIgnoreCase("Eltnen"))
			{
				worldId = WorldMapType.ELTNEN.getId();
			}
			else if (destination.equalsIgnoreCase("Theobomos"))
			{
				worldId = WorldMapType.THEOBOMOS.getId();
			}
			else if (destination.equalsIgnoreCase("Heiron"))
			{
				worldId = WorldMapType.HEIRON.getId();
			}
			else if (destination.equalsIgnoreCase("Pandaemonium"))
			{
				worldId = WorldMapType.PANDAEMONIUM.getId();
			}
			else if (destination.equalsIgnoreCase("Marchutan"))
			{
				worldId = WorldMapType.MARCHUTAN_PRIORY.getId();
			}
			else if (destination.equalsIgnoreCase("Ishalgen"))
			{
				worldId = WorldMapType.ISHALGEN.getId();
			}
			else if (destination.equalsIgnoreCase("Altgard"))
			{
				worldId = WorldMapType.ALTGARD.getId();
			}
			else if (destination.equalsIgnoreCase("Morheim"))
			{
				worldId = WorldMapType.MORHEIM.getId();
			}
			else if (destination.equalsIgnoreCase("Brusthonin"))
			{
				worldId = WorldMapType.BRUSTHONIN.getId();
			}
			else if (destination.equalsIgnoreCase("Beluslan"))
			{
				worldId = WorldMapType.BELUSLAN.getId();
			}
			else if (destination.equalsIgnoreCase("Inggison"))
			{
				worldId = WorldMapType.INGGISON.getId();
			}
			else if (destination.equalsIgnoreCase("Gelkmaros"))
			{
				worldId = WorldMapType.GELKMAROS.getId();
			}
			else if (destination.equalsIgnoreCase("Silentera"))
			{
				worldId = 600010000;
			}
			else if (destination.equalsIgnoreCase("Reshanta"))
			{
				worldId = WorldMapType.RESHANTA.getId();
			}
			else if (destination.equalsIgnoreCase("Kaisinel Academy"))
			{
				worldId = 110070000;
			}
			else if (destination.equalsIgnoreCase("Marchutan Priory"))
			{
				worldId = 120080000;
			}
			else if (destination.equalsIgnoreCase("Oriel"))
			{
				worldId = 700010000;
			}
			else if (destination.equalsIgnoreCase("Pernon"))
			{
				worldId = 710010000;
			}
			else if (destination.equalsIgnoreCase("All"))
			{
				worldId = 0;
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Could not find the specified map !");
			}
		}
		
		final String destinationMap = destination;
		
		// despawn specified map, no instance
		if (destination.equalsIgnoreCase("All"))
		{
			reloadMap(WorldMapType.SANCTUM.getId(), player, "Sanctum");
			reloadMap(WorldMapType.KAISINEL_ACADEMY.getId(), player, "Kaisinel");
			reloadMap(WorldMapType.POETA.getId(), player, "Poeta");
			reloadMap(WorldMapType.VERTERON.getId(), player, "Verteron");
			reloadMap(WorldMapType.ELTNEN.getId(), player, "Eltnen");
			reloadMap(WorldMapType.THEOBOMOS.getId(), player, "Theobomos");
			reloadMap(WorldMapType.HEIRON.getId(), player, "Heiron");
			reloadMap(WorldMapType.PANDAEMONIUM.getId(), player, "Pandaemonium");
			reloadMap(WorldMapType.MARCHUTAN_PRIORY.getId(), player, "Marchutan");
			reloadMap(WorldMapType.ISHALGEN.getId(), player, "Ishalgen");
			reloadMap(WorldMapType.ALTGARD.getId(), player, "Altgard");
			reloadMap(WorldMapType.MORHEIM.getId(), player, "Morheim");
			reloadMap(WorldMapType.BRUSTHONIN.getId(), player, "Brusthonin");
			reloadMap(WorldMapType.BELUSLAN.getId(), player, "Beluslan");
			reloadMap(WorldMapType.INGGISON.getId(), player, "Inggison");
			reloadMap(WorldMapType.GELKMAROS.getId(), player, "Gelkmaros");
			reloadMap(600010000, player, "Silentera");
			reloadMap(WorldMapType.RESHANTA.getId(), player, "Reshanta");
			reloadMap(110070000, player, "Kaisinel Academy");
			reloadMap(120080000, player, "Marchutan Priory");
			reloadMap(700010000, player, "Oriel");
			reloadMap(710010000, player, "Pernon");
		}
		else
		{
			reloadMap(worldId, player, destinationMap);
		}
	}
	
	/**
	 * Reloads all spawns for a specific world map.<br>
	 * This method clears existing objects and triggers the {@link SpawnEngine}.<br>
	 * It also sends a confirmation message to the administrator.
	 * @param worldId The unique identifier of the world to reload.
	 * @param admin The {@code Player} who executed the command.
	 * @param destinationMap The name of the map for use in the success message.
	 */
	private void reloadMap(int worldId, Player admin, String destinationMap)
	{
		final int IdWorld = worldId;
		final Player adm = admin;
		final String dest = destinationMap;
		
		if (IdWorld != 0)
		{
			World.getInstance().doOnAllObjects(object ->
			{
				if (object.getWorldId() != IdWorld)
				{
					return;
				}
				
				if ((object instanceof Npc) || (object instanceof Gatherable) || (object instanceof StaticObject))
				{
					object.getController().onDelete();
				}
			});
			SpawnEngine.spawnWorldMap(IdWorld);
			PacketSendUtility.sendMessage(adm, "Spawns for map: " + IdWorld + " (" + dest + ") reloaded succesfully");
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
		PacketSendUtility.sendMessage(player, "syntax //reload_spawn <location name | all>");
	}
}
