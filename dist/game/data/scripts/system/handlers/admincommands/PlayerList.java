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

import java.util.Collection;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Handles the admin command to display a list of online players.<br>
 * It retrieves all active {@link Player} objects from the current {@link World}.<br>
 * This allows administrators to monitor player activity across the server.
 * @author Antraxx
 */
public class PlayerList extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link PlayerList} command.<br>
	 * This constructor initializes the admin command with the name {@code playerlist}.
	 */
	public PlayerList()
	{
		super("playerlist");
	}
	
	/**
	 * Displays a list of currently connected players to the admin.<br>
	 * It filters the results based on the provided parameters like race or membership status.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments used to filter the list (e.g., "ely", "asmo", "premium", "vip").
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax //playerlist <all|ely|asmo|premium|vip>");
			return;
		}
		
		// get all currently connected players
		final Collection<Player> players = World.getInstance().getAllPlayers();
		PacketSendUtility.sendMessage(player, "Currently connected players:");
		
		for (Player p : players)
		{
			if (params.length > 0)
			{
				final String cmd = params[0].toLowerCase().trim();
				if (("ely").startsWith(cmd))
				{
					if (p.getCommonData().getRace() == Race.ASMODIANS)
					{
						continue;
					}
				}
				
				if (("asmo").startsWith(cmd))
				{
					if (p.getCommonData().getRace() == Race.ELYOS)
					{
						continue;
					}
				}
				
				if (("premium").startsWith(cmd))
				{
					if (p.getPlayerAccount().getMembership() == 2)
					{
						continue;
					}
				}
				
				if (("vip").startsWith(cmd))
				{
					if (p.getPlayerAccount().getMembership() == 1)
					{
						continue;
					}
				}
			}
			
			PacketSendUtility.sendMessage(player, "Char: " + p.getName() + " (" + p.getAcountName() + ") " + " - " + p.getCommonData().getRace().name() + "/" + p.getCommonData().getPlayerClass().name() + " - Location: " + WorldMapType.getWorld(p.getWorldId()).name());
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
		PacketSendUtility.sendMessage(player, "syntax //playerlist <all|ely|asmo|premium|vip>");
	}
}
