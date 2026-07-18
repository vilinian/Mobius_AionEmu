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

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to increase a player's level.<br>
 * It allows administrators to manually adjust character progression levels.
 * @author Shift
 */
public class LevelUp extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link LevelUp} command.<br>
	 * This class handles the admin command for increasing a player's level.
	 */
	public LevelUp()
	{
		super("levelup");
	}
	
	/**
	 * Executes the command to increase a target player's level.<br>
	 * It adds the specified amount from {@code params[0]} to the target's current level.<br>
	 * The method ensures the new level does not exceed {@code PLAYER_MAX_LEVEL}.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments where the first value is the amount of levels to add.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		Player target = null;
		final VisibleObject creature = player.getTarget();
		
		if (player.getTarget() instanceof Player)
		{
			target = (Player) creature;
		}
		
		if (target == null)
		{
			PacketSendUtility.sendMessage(player, "You should select a target first!");
			return;
		}
		
		if (params.length < 1)
		{
			PacketSendUtility.sendMessage(player, "You should enter second params!");
			return;
		}
		
		int level;
		try
		{
			level = Integer.parseInt(params[0]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "You should enter valid second params!");
			return;
		}
		
		final Player playerT = target;
		
		if ((playerT.getCommonData().getLevel() + level) <= GSConfig.PLAYER_MAX_LEVEL)
		{
			final int newLevel = playerT.getCommonData().getLevel() + level;
			playerT.getCommonData().setLevel(newLevel);
		}
		else
		{
			PacketSendUtility.sendMessage(player, "The value of <level> will plus calculated to the current player level!");
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
		PacketSendUtility.sendMessage(player, "syntax //levelup <target> <level>");
		PacketSendUtility.sendMessage(player, "The value of <level> will plus calculated to the current player level!");
	}
	
}
