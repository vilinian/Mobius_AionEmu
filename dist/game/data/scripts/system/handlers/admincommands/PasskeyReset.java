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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to reset a player's passkey.<br>
 * This class allows administrators to clear existing security keys for specific accounts.
 * @author cura
 */
public class PasskeyReset extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link PasskeyReset} command.<br>
	 * This class handles the admin command for resetting player passkeys.
	 */
	public PasskeyReset()
	{
		super("passkeyreset");
	}
	
	/**
	 * Resets the passkey for a specific player.<br>
	 * It validates the target name and ensures the new passkey is between 6 and 8 digits.<br>
	 * The method updates the database and notifies the login server of the change.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the target player name and the new passkey.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax: //passkeyreset <player> <passkey>");
			return;
		}
		
		final String name = Util.convertName(params[0]);
		final int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
		if (accountId == 0)
		{
			PacketSendUtility.sendMessage(player, "player " + name + " can't find!");
			PacketSendUtility.sendMessage(player, "syntax: //passkeyreset <player> <passkey>");
			return;
		}
		
		try
		{
			Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "parameters should be number!");
			return;
		}
		
		final String newPasskey = params[1];
		if (!((newPasskey.length() > 5) && (newPasskey.length() < 9)))
		{
			PacketSendUtility.sendMessage(player, "passkey is 6~8 digits!");
			return;
		}
		
		DAOManager.getDAO(PlayerPasskeyDAO.class).updateForcePlayerPasskey(accountId, newPasskey);
		LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", -1, player.getObjectId());
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
		PacketSendUtility.sendMessage(player, "syntax: //passkeyreset <player> <passkey>");
	}
}
