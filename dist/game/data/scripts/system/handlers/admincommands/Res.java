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
import com.aionemu.gameserver.network.aion.serverpackets.SM_RESURRECT;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to resurrect a player.<br>
 * It uses {@link PlayerReviveService} to bring a target back to life.
 * @author Sarynth
 */
public class Res extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Res} command.<br>
	 * This class handles the resurrection of players via an admin command.
	 */
	public Res()
	{
		super("res");
	}
	
	/**
	 * Executes the command to resurrect a targeted player.<br>
	 * It checks if the target is a dead {@code Player}.<br>
	 * Use {@code instant} for immediate revival or {@code prompt} to notify them.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element determines the resurrection type.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final VisibleObject target = admin.getTarget();
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "No target selected.");
			return;
		}
		
		if (!(target instanceof Player))
		{
			PacketSendUtility.sendMessage(admin, "You can only resurrect other players.");
			return;
		}
		
		final Player player = (Player) target;
		if (!player.getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendMessage(admin, "That player is already alive.");
			return;
		}
		
		// Default action is to prompt for resurrect.
		if ((params == null) || (params.length == 0) || ("prompt").startsWith(params[0]))
		{
			player.setPlayerResActivate(true);
			PacketSendUtility.sendPacket(player, new SM_RESURRECT(admin));
			return;
		}
		
		if (("instant").startsWith(params[0]))
		{
			PlayerReviveService.skillRevive(player);
			return;
		}
		
		PacketSendUtility.sendMessage(admin, "[Resurrect] Usage: target player and use //res <instant|prompt>");
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
		// TODO Auto-generated method stub
	}
}
