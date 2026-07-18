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

import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /userinfo} command for players.<br>
 * It retrieves and displays information about a specific player to the user.
 * @author Maestros
 */
public class cmd_userinfo extends PlayerCommand
{
	/**
	 * Registers the {@code userinfo} command.<br>
	 * This method initializes the command handler for displaying user information.
	 */
	public cmd_userinfo()
	{
		super("userinfo");
	}
	
	/**
	 * Displays information about the object currently targeted by the {@code player}.<br>
	 * It checks if the target is an {@link Npc} or a {@link Gatherable} object.<br>
	 * If the target is another {@link Player}, it sends a warning message instead.
	 * @param player The player who is executing the command.
	 * @param params Additional arguments provided with the command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final VisibleObject target = player.getTarget();
		
		if (target instanceof Player)
		{
			PacketSendUtility.sendMessage(player, "You may not view other players' info.");
		}
		else if (target instanceof Npc)
		{
			final Npc npc = (Npc) player.getTarget();
			PacketSendUtility.sendMessage(player, "[NPC Info]" + "\nName: " + npc.getName() + "\nId: " + npc.getNpcId() + "\nMap ID: " + player.getTarget().getWorldId());
		}
		else if (target instanceof Gatherable)
		{
			final Gatherable gather = (Gatherable) target;
			PacketSendUtility.sendMessage(player, "[Gather Info]\n" + "Name: " + gather.getName() + "\nId: " + gather.getObjectTemplate().getTemplateId() + "\nMap ID: " + player.getTarget().getWorldId());
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
		// TODO Auto-generated method stub
	}
}
