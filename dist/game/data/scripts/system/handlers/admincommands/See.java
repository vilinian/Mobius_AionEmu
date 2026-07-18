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

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.services.player.PlayerVisualStateService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the {@code see} admin command to reveal players in the game world.<br>
 * It allows administrators to bypass normal visibility restrictions for specific targets.
 * @author Mathew
 */
public class See extends AdminCommand
{
	/**
	 * Registers the {@code see} admin command.<br>
	 * This allows administrators to view players in the game world.
	 */
	public See()
	{
		super("see");
	}
	
	/**
	 * Toggles the {@code CreatureSeeState} for the administrator.<br>
	 * It grants or removes vision based on the current state of the {@code Player}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings used for additional arguments.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (admin.getSeeState() < 2)
		{
			admin.setSeeState(CreatureSeeState.SEARCH10);
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
			PacketSendUtility.sendMessage(admin, "You got vision.");
			if (SecurityConfig.INVIS)
			{
				PlayerVisualStateService.seeValidate(admin);
			}
		}
		else
		{
			admin.unsetSeeState(CreatureSeeState.SEARCH10);
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
			PacketSendUtility.sendMessage(admin, "You lost vision.");
			if (SecurityConfig.INVIS)
			{
				PlayerVisualStateService.seeValidate(admin);
			}
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
		PacketSendUtility.sendMessage(player, "Syntax: //see");
	}
}
