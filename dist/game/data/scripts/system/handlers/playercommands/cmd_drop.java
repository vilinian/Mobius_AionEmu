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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /drop} command for players.<br>
 * This class allows a player to discard items from their inventory into the game world.
 * @author Eloann
 */
public class cmd_drop extends PlayerCommand
{
	/**
	 * Registers the {@code drop} command for players.<br>
	 * This allows users to interact with items in the game world.
	 */
	public cmd_drop()
	{
		super("drop");
	}
	
	/**
	 * Displays the drop information for a specific NPC.<br>
	 * It identifies the NPC using either an ID provided in {@code params} or by targeting an {@link Npc}.<br>
	 * The method lists all drop groups and items along with their respective rates.
	 * @param player The admin player executing the command.
	 * @param params Optional arguments where the first value can be a numeric NPC ID.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		NpcDrop npcDrop = null;
		if (params.length > 0)
		{
			final int npcId = Integer.parseInt(params[0]);
			final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
			if (npcTemplate == null)
			{
				PacketSendUtility.sendMessage(player, "Incorrect npcId: " + npcId);
				return;
			}
			
			npcDrop = npcTemplate.getNpcDrop();
		}
		else
		{
			final VisibleObject visibleObject = player.getTarget();
			
			if (visibleObject == null)
			{
				PacketSendUtility.sendMessage(player, "You have no target !");
				return;
			}
			
			if (visibleObject instanceof Player)
			{
				PacketSendUtility.sendMessage(player, "Your target must be a npc !");
				return;
			}
			
			if (visibleObject instanceof Npc)
			{
				npcDrop = ((Npc) visibleObject).getNpcDrop();
			}
		}
		
		if (npcDrop == null)
		{
			final Npc npc = (Npc) player.getTarget();
			PacketSendUtility.sendMessage(player, "NPC ID :" + " " + npc.getNpcId() + " has no drops.");
			return;
		}
		
		int count = 0;
		PacketSendUtility.sendMessage(player, "[Mob Drops]\n");
		for (DropGroup dropGroup : npcDrop.getDropGroup())
		{
			PacketSendUtility.sendMessage(player, "DropGroup: " + dropGroup.getGroupName());
			for (Drop drop : dropGroup.getDrop())
			{
				PacketSendUtility.sendMessage(player, "[item:" + drop.getItemId() + "]" + " Rate: " + drop.getChance());
				count++;
			}
		}
		
		PacketSendUtility.sendMessage(player, "There are " + count + " drops on NPC.");
		final Npc npc = (Npc) player.getTarget();
		PacketSendUtility.sendMessage(player, "NpcId :" + " " + npc.getNpcId());
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
