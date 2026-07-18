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

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command for broadcasting a custom message to all players.<br>
 * It allows administrators to send text messages using different {@code ChatType} values.
 * @author Divinity
 */
public class Say extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@code Say} command.<br>
	 * This class allows administrators to send messages to other players.<br>
	 * It extends the base functionality provided by {@link AdminCommand}.
	 */
	public Say()
	{
		super("say");
	}
	
	/**
	 * Executes the command to send a message to a selected target.<br>
	 * It joins all provided parameters into a single string.<br>
	 * The message is sent as a normal chat packet to either a {@code Player} or an {@code Npc}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings that will be joined together to form the message body.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length < 1)
		{
			onFail(admin, null);
			return;
		}
		
		final VisibleObject target = admin.getTarget();
		
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "You must select a target !");
			return;
		}
		
		final StringBuilder sbMessage = new StringBuilder();
		
		for (String p : params)
		{
			sbMessage.append(p + " ");
		}
		
		final String sMessage = sbMessage.toString().trim();
		
		if (target instanceof Player)
		{
			PacketSendUtility.broadcastPacket(((Player) target), new SM_MESSAGE(((Player) target), sMessage, ChatType.NORMAL), true);
		}
		else if (target instanceof Npc)
		{
			// admin is not right, but works
			PacketSendUtility.broadcastPacket(admin, new SM_MESSAGE(target.getObjectId(), ((Npc) target).getName(), sMessage, ChatType.NORMAL), true);
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
		PacketSendUtility.sendMessage(player, "Syntax: //say <Text>");
	}
}
