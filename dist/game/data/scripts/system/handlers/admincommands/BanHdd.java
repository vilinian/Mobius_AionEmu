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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.BannedHDDManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to ban a player based on their Hard Disk Drive (HDD) identifier.<br>
 * This class utilizes {@link BannedHDDManager} to manage and enforce hardware-based bans.
 * @author Alex
 */
public class BanHdd extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link BanHdd} command.<br>
	 * This class handles the admin command for banning hard drives.
	 */
	public BanHdd()
	{
		super("banhdd");
	}
	
	/**
	 * Executes the command to ban a specific HDD serial.<br>
	 * It parses the time in minutes and an optional HDD serial from {@code params}.<br>
	 * If no serial is provided, it attempts to retrieve it from the selected target.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the ban duration in minutes and the HDD serial.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			onFail(player, "Please add one or more parameters");
			return;
		}
		
		int time;
		String hdd_serial;
		String targetName = "direct_type";
		
		// try parsing
		try
		{
			time = Integer.parseInt(params[0]);
			
			if (time == 0) // 0 is 10 years since system don't allow infinte banns without rework - it's pseudo infinity
			{
				time = 60 * 24 * 365 * 10;
			}
		}
		catch (NumberFormatException e)
		{
			onFail(player, "Please enter a valid integer amount of minutes");
			return;
		}
		
		// is mac defined?
		if (params.length > 1)
		{
			hdd_serial = params[1];
		}
		else
		{
			// no address defined
			final VisibleObject target = player.getTarget();
			if ((target != null) && (target instanceof Player))
			{
				if (target.getObjectId().equals(player.getObjectId()))
				{
					onFail(player, "Omg, disselect yourself please.");
					return;
				}
				
				final Player targetpl = (Player) target;
				hdd_serial = targetpl.getClientConnection().getHddSerial();
				targetName = targetpl.getName();
				targetpl.getClientConnection().closeNow();
			}
			else
			{
				onFail(player, "You should select a player or give me any hdd serial");
				return;
			}
		}
		
		BannedHDDManager.getInstance().banAddress(hdd_serial, System.currentTimeMillis() + (time * 60 * 1000), "author=" + player.getName() + ", " + player.getObjectId() + "; target=" + targetName);
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
		if (!message.isEmpty())
		{
			PacketSendUtility.sendMessage(player, message);
		}
		
		PacketSendUtility.sendMessage(player, "Syntax: //banhdd [time in minutes] <hdd_serial>");
		PacketSendUtility.sendMessage(player, "Note: 0 minutes will cause permanent ban");
	}
	
}
