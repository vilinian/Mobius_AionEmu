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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to clear a player's inventory.<br>
 * It removes all {@code Item} objects from the target {@link Player}.<br>
 * This is used for administrative maintenance and clearing storage.
 * @author CoolyT
 */
public class cleanInventory extends AdminCommand
{
	/**
	 * Clears all items from a player's inventory.<br>
	 * This command is used by administrators to reset storage.<br>
	 * It calls the {@code String...)} method.
	 */
	public cleanInventory()
	{
		super("clean");
	}
	
	/**
	 * Clears all items from the inventory of the administrator.<br>
	 * It removes every {@code Item} currently held by the {@code Player}.<br>
	 * A success message is sent to the admin after completion.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings for additional arguments.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final Storage inv = admin.getInventory();
		for (Item item : inv.getItems())
		{
			inv.decreaseByItemId(item.getItemId(), item.getItemCount());
		}
		
		PacketSendUtility.sendMessage(admin, "Sucessfully cleared your Inventory.");
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
		PacketSendUtility.sendMessage(player, "<usage //clean>");
	}
}
