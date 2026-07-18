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

/**
 * Handles the {@code /who} admin command.<br>
 * This class allows administrators to list players currently online in the game world.<br>
 * It provides a quick way to monitor active users.
 */
public class Who extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Who} command.<br>
	 * This class handles the {@code who} admin command.<br>
	 * It allows administrators to view player information.
	 */
	public Who()
	{
		super("who");
	}
	
	/**
	 * Lists all players currently online in the world.<br>
	 * It filters results based on optional parameters like race or membership status.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings used to filter the player list.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final Collection<Player> players = World.getInstance().getAllPlayers();
		
		PacketSendUtility.sendMessage(admin, "Player :");
		
		for (Player player : players)
		{
			if ((params != null) && (params.length > 0))
			{
				final String cmd = params[0].toLowerCase();
				
				if (("ely").startsWith(cmd))
				{
					if (player.getCommonData().getRace() == Race.ASMODIANS)
					{
						continue;
					}
				}
				
				if (("asmo").startsWith(cmd))
				{
					if (player.getCommonData().getRace() == Race.ELYOS)
					{
						continue;
					}
				}
				
				if (("member").startsWith(cmd) || ("premium").startsWith(cmd))
				{
					if (player.getPlayerAccount().getMembership() == 0)
					{
						continue;
					}
				}
			}
			
			PacketSendUtility.sendMessage(admin, "Char: " + player.getName() + " - Race: " + player.getCommonData().getRace().name() + " - Acc: " + player.getAcountName());
		}
	}
}
