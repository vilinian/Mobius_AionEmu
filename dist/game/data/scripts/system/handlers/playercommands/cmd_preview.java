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
package system.handlers.playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemRemodelService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code preview} command for players.<br>
 * It allows users to view item previews using the {@link ItemRemodelService}.
 * @author Kashim
 */
public class cmd_preview extends PlayerCommand
{
	private static final int REMODEL_PREVIEW_DURATION = 15;
	
	/**
	 * Initializes the {@link cmd_preview} command.<br>
	 * This constructor sets up the preview functionality for players.
	 */
	public cmd_preview()
	{
		super("preview");
	}
	
	/**
	 * Executes the preview command for a specific item.<br>
	 * This method validates the input and calls {@code commandPreviewRemodelItem}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params An array of {@code String} containing the item ID.
	 */
	public void executeCommand(Player admin, String[] params)
	{
		if ((params.length < 1) || (params[0] == ""))
		{
			PacketSendUtility.sendMessage(admin, "Syntax: .preview <itemid>");
			return;
		}
		
		int itemId = 0;
		try
		{
			itemId = Integer.parseInt(params[0]);
		}
		catch (Exception e)
		{
			PacketSendUtility.sendMessage(admin, "Error! Item id's are numbers like 187000090 or [item:187000090]!");
			return;
		}
		
		ItemRemodelService.commandPreviewRemodelItem(admin, itemId, REMODEL_PREVIEW_DURATION);
	}
	
	/**
	 * Executes the command to add an item to a player.<br>
	 * It parses the provided parameters to identify the item ID and quantity.<br>
	 * The method handles both self-giving and giving items to other players.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the target name, item link, and count.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		executeCommand(player, params);
	}
}
