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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.abyss.AbyssRankUpdateService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands related to player rankings.<br>
 * This class allows administrators to view or manage ranking data within the game.<br>
 * It extends {@link AdminCommand} to provide specific functionality for rank management.
 * @author ATracer
 */
public class Ranking extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Ranking} class.<br>
	 * This constructor registers the command as {@code ranking}.
	 */
	public Ranking()
	{
		super("ranking");
	}
	
	/**
	 * Executes the ranking update command.<br>
	 * It triggers a global update of abyss ranks via {@link AbyssRankUpdateService}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be "update".
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length == 0)
		{
			onFail(admin, null);
		}
		else if ("update".equalsIgnoreCase(params[0]))
		{
			AbyssRankUpdateService.getInstance().performUpdate();
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
		PacketSendUtility.sendMessage(player, "syntax //ranking update");
	}
}
