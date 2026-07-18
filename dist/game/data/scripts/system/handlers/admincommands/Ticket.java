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

import com.aionemu.gameserver.model.Support;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.SupportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the creation and management of support tickets via admin commands.<br>
 * This class allows administrators to interact with {@link Support} requests.<br>
 * It processes specific inputs to facilitate communication between players and staff.
 * @author paranaix
 */
public class Ticket extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Ticket} class.<br>
	 * This constructor initializes the command with the name {@code ticket}.
	 */
	public Ticket()
	{
		super("ticket");
	}
	
	/**
	 * Executes the ticket management command.<br>
	 * It allows an administrator to either accept or peek at a support ticket.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be either "accept" or "peek".
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //ticket <accept | peek>");
			return;
		}
		
		if (params[0].equals("accept"))
		{
			accept(admin);
		}
		else if (params[0].equals("peek"))
		{
			peek(admin);
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //ticket <accept | peek>");
		}
	}
	
	/**
	 * This method allows an administrator to view the details of a pending ticket.<br>
	 * It retrieves the current ticket from {@link SupportService}.<br>
	 * If no ticket exists, it sends a notification to the {@code admin}.
	 * @param admin The {@code Player} who is executing the command.
	 */
	public void accept(Player admin)
	{
		final StringBuilder builder = new StringBuilder();
		final Support support = SupportService.getInstance().getTicket();
		
		if (support == null)
		{
			PacketSendUtility.sendMessage(admin, "There are no tickets available at the moment");
			return;
		}
		
		builder.append("===============\n");
		builder.append("From: " + support.getOwner().getName() + "\n");
		builder.append(support.getSummary() + "\n");
		builder.append("===============");
		
		PacketSendUtility.sendMessage(admin, builder.toString());
	}
	
	/**
	 * Allows an administrator to view the details of the next available support ticket.<br>
	 * It retrieves a {@link com.aionemu.gameserver.model.Support} object from the {@link com.aionemu.gameserver.services.SupportService}.<br>
	 * If no tickets exist, it sends a notification to the {@code admin}.
	 * @param admin The {@code Player} who is executing the command.
	 */
	public void peek(Player admin)
	{
		final StringBuilder builder = new StringBuilder();
		final Support support = SupportService.getInstance().peek();
		
		if (support == null)
		{
			PacketSendUtility.sendMessage(admin, "There are no tickets available at the moment");
			return;
		}
		
		builder.append("===============\n");
		builder.append("From: " + support.getOwner().getName() + "\n");
		builder.append(support.getSummary() + "\n");
		builder.append("===============");
		
		PacketSendUtility.sendMessage(admin, builder.toString());
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
		PacketSendUtility.sendMessage(player, "Syntax: //ticket <accept | peek>");
	}
}
