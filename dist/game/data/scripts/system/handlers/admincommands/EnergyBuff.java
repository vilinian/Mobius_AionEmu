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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to apply an energy buff to a target.<br>
 * This class allows administrators to grant temporary energy boosts to {@link Player} objects.
 * @author Source
 */
public class EnergyBuff extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link EnergyBuff} class.<br>
	 * This class handles the admin command for granting energy buffs.
	 */
	public EnergyBuff()
	{
		super("energy");
	}
	
	/**
	 * Executes the command to manage energy buffs for a targeted player.<br>
	 * It allows admins to view, add, or reset repose and salvation values.<br>
	 * The method also supports refreshing the stats of the target player.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the action type and specific values.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final VisibleObject target = player.getTarget();
		if (target == null)
		{
			PacketSendUtility.sendMessage(player, "No target selected");
			return;
		}
		
		final Creature creature = (Creature) target;
		if ((params == null) || (params.length < 1))
		{
			onFail(player, null);
		}
		else if (target instanceof Player)
		{
			if (params[0].equals("repose"))
			{
				final Player targetPlayer = (Player) creature;
				if (params[1].equals("info"))
				{
					PacketSendUtility.sendMessage(player, "Current EoR: " + targetPlayer.getCommonData().getCurrentReposteEnergy() + "\n Max EoR: " + targetPlayer.getCommonData().getMaxReposteEnergy());
				}
				else if (params[1].equals("add"))
				{
					targetPlayer.getCommonData().addReposteEnergy(Long.parseLong(params[2]));
				}
				else if (params[1].equals("reset"))
				{
					targetPlayer.getCommonData().setCurrentReposteEnergy(0);
				}
			}
			else if (params[0].equals("salvation"))
			{
				final Player targetPlayer = (Player) creature;
				if (params[1].equals("info"))
				{
					PacketSendUtility.sendMessage(player, "Current EoS: " + targetPlayer.getCommonData().getCurrentSalvationPercent());
				}
				else if (params[1].equals("add"))
				{
					targetPlayer.getCommonData().addSalvationPoints(Long.parseLong(params[2]));
				}
				else if (params[1].equals("reset"))
				{
					targetPlayer.getCommonData().resetSalvationPoints();
				}
			}
			else if (params[0].equals("refresh"))
			{
				final Player targetPlayer = (Player) creature;
				PacketSendUtility.sendPacket(targetPlayer, new SM_STATS_INFO(targetPlayer));
			}
		}
		else
		{
			PacketSendUtility.sendMessage(player, "This is not player");
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
		final String syntax = "//energy repose|salvation|refresh info|reset|add [points]";
		PacketSendUtility.sendMessage(player, syntax);
	}
}
