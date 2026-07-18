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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Handles the admin command for teleporting players to specific coordinates.<br>
 * It utilizes {@link TeleportService2} to move a {@code Player} to a new location.<br>
 * This class validates the target destination using {@link GeoService}.
 * @author Source
 * @rework Kill3r
 */
public class Warp extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Warp} class.<br>
	 * This command allows administrators to teleport players to specific locations.
	 */
	public Warp()
	{
		super("warp");
	}
	
	/**
	 * Executes the command to teleport a player to a specific location.<br>
	 * It parses coordinates and map data from the provided parameters.<br>
	 * The method validates the destination and handles both standard and bracketed formats.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the location string, map ID, x, y, z, and layer information.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params.length < 5) || !GeoDataConfig.GEO_ENABLE)
		{
			onFail(player, "");
			return;
		}
		
		// [pos:Location;1 120010000 1304.7 1423.1 0.0 0] <-- uses this format of Location
		try
		{
			String LocS, first, last;
			float x, y, z;
			LocS = "";
			int mapL = 0;
			int layerI = -1;
			// int race;
			
			first = params[0];
			mapL = Integer.parseInt(params[1]);
			x = Float.parseFloat(params[2]);
			y = Float.parseFloat(params[3]);
			z = Float.parseFloat(params[4]);
			last = params[5];
			
			final Pattern f = Pattern.compile("\\[pos:([^;]+);\\s*+(\\d{1})");
			final Pattern l = Pattern.compile("(\\d)\\]");
			final Matcher fm = f.matcher(first);
			final Matcher lm = l.matcher(last);
			
			if (fm.find())
			{
				LocS = fm.group(1);
				// race = Integer.parseInt(fm.group(2));
			}
			
			if (lm.find())
			{
				layerI = Integer.parseInt(lm.group(1));
			}
			
			z = GeoService.getInstance().getZ(mapL, x, y);
			PacketSendUtility.sendMessage(player, "Map ID (" + mapL + ")\n" + "x: " + x + "y: " + y + "z: " + z + " L(" + layerI + ")");
			
			if (mapL == 400010000)
			{
				PacketSendUtility.sendMessage(player, "Sorry you can't warp at abyss");
			}
			else
			{
				TeleportService2.teleportTo(player, mapL, x, y, z);
				PacketSendUtility.sendMessage(player, "You have successfully warped to this location --- > " + LocS);
			}
			
		}
		catch (NumberFormatException e)
		{
			// [pos:Location;120010000 1304.7 1423.1 0.0 0] <-- uses this format of Location
			
			if (params.length < 5)
			{
				onFail(player, "");
				return;
			}
			
			String locS, first, last;
			float xF, yF, zF;
			locS = "";
			int mapL = 0;
			int layerI = -1;
			
			first = params[0];
			xF = Float.parseFloat(params[1]);
			yF = Float.parseFloat(params[2]);
			zF = Float.parseFloat(params[3]);
			last = params[4];
			
			final Pattern f = Pattern.compile("\\[pos:([^;]+);\\s*+(\\d{9})");
			final Pattern l = Pattern.compile("(\\d)\\]");
			final Matcher fm = f.matcher(first);
			final Matcher lm = l.matcher(last);
			
			if (fm.find())
			{
				locS = fm.group(1);
				mapL = Integer.parseInt(fm.group(2));
			}
			
			if (lm.find())
			{
				layerI = Integer.parseInt(lm.group(1));
			}
			
			zF = GeoService.getInstance().getZ(mapL, xF, yF);
			PacketSendUtility.sendMessage(player, "MapId (" + mapL + ")\n" + "x:" + xF + " y:" + yF + " z:" + zF + " l(" + layerI + ")");
			
			if (mapL == 400010000)
			{
				PacketSendUtility.sendMessage(player, "Sorry you can't warp at abyss");
			}
			else
			{
				TeleportService2.teleportTo(player, mapL, xF, yF, zF);
				PacketSendUtility.sendMessage(player, "You have successfully warp -> " + locS);
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
		if (!GeoDataConfig.GEO_ENABLE)
		{
			PacketSendUtility.sendMessage(player, "You must turn on geo in config to use this command!");
			return;
		}
		
		PacketSendUtility.sendMessage(player, "syntax //warp <@link>");
	}
}
