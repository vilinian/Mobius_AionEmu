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

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.taskmanager.tasks.MovementNotifyTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Handles administrative commands related to {@link WorldMapInstance} management.<br>
 * This class allows administrators to interact with and modify map data via the game console.
 * @author Rolandas
 */
public class Map extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Map} command.<br>
	 * This constructor initializes the admin command with the name {@code map}.
	 */
	public Map()
	{
		super("map");
	}
	
	/**
	 * Executes map-related administrative commands.<br>
	 * This method can freeze or unfreeze all {@code Npc} entities on the current world map.<br>
	 * It also allows the admin to view broadcast statistics.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element determines the action like "freeze", "unfreeze", or "stats".
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final WorldMapInstance instance = admin.getPosition().getWorldMapInstance();
		if ("freeze".equalsIgnoreCase(params[0]))
		{
			for (Npc npc : instance.getNpcs())
			{
				npc.getAi2().onGeneralEvent(AIEventType.FREEZE);
			}
			
			PacketSendUtility.sendMessage(admin, "World map is frozen!");
		}
		else if ("unfreeze".equalsIgnoreCase(params[0]))
		{
			for (Npc npc : instance.getNpcs())
			{
				npc.getAi2().onGeneralEvent(AIEventType.UNFREEZE);
			}
			
			PacketSendUtility.sendMessage(admin, "World map is unfrozen!");
		}
		else if ("stats".equalsIgnoreCase(params[0]))
		{
			for (String line : MovementNotifyTask.getInstance().dumpBroadcastStats())
			{
				PacketSendUtility.sendMessage(admin, line);
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
		PacketSendUtility.sendMessage(player, "usage: //map freeze | unfreeze | stats");
	}
}
