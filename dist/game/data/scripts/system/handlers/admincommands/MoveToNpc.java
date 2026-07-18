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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to teleport a {@link Player} to a specific NPC.<br>
 * It validates the target coordinates using an {@link NpcTemplate}.<br>
 * This class utilizes {@link TeleportService2} to execute the movement.
 * @author MrPoke, lord_rex and ginho1
 * @modified Alex
 */
public class MoveToNpc extends AdminCommand
{
	/**
	 * Initializes the {@link MoveToNpc} admin command.<br>
	 * This allows administrators to teleport players to specific NPCs.
	 */
	public MoveToNpc()
	{
		super("movetonpc");
	}
	
	/**
	 * Teleports the player to a specific NPC.<br>
	 * It parses the first parameter for an NPC ID or a name with coordinates.<br>
	 * If multiple NPCs share the same name, it lists their IDs in the chat.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the NPC identifier or location string.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		int npcId = 0;
		String npcName = "";
		final String npc = params[0];
		String message = "";
		try
		{
			if (params[0].contains("[where:"))
			{
				final Pattern f = Pattern.compile("\\[where:([^;]+);\\s*+(\\d{6})");
				final Matcher fm = f.matcher(npc);
				if (fm.find())
				{
					npcName = fm.group(1);
					npcId = Integer.parseInt(fm.group(2));
				}
			}
			else
			{
				npcId = Integer.valueOf(npc);
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			onFail(player, e.getMessage());
		}
		catch (NumberFormatException e)
		{
			for (String param : params)
			{
				npcName += param + " ";
			}
			
			npcName = npcName.substring(0, npcName.length() - 1);
			
			for (NpcTemplate template : DataManager.NPC_DATA.getNpcData().valueCollection())
			{
				if (template.getName().equalsIgnoreCase(npcName))
				{
					if (npcId == 0)
					{
						npcId = template.getTemplateId();
					}
					else
					{
						if (message.isEmpty())
						{
							message += "Found others (" + npcName + "): \n";
						}
						
						message += "Id: " + template.getTemplateId() + "\n";
					}
				}
			}
			
			if (npcId == 0)
			{
				PacketSendUtility.sendMessage(player, "NPC " + npcName + " cannot be found");
				return;
			}
		}
		
		if (npcId > 0)
		{
			if (!message.isEmpty())
			{
				message = "Teleporting to Npc: " + npcName + " " + npcId + "\n" + message;
			}
			else
			{
				message = "Teleporting to Npc: " + npcName + " " + npcId;
			}
			
			PacketSendUtility.sendMessage(player, message);
			TeleportService2.teleportToNpc(player, npcId);
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
		PacketSendUtility.sendMessage(player, "syntax //movetonpc <npc_id|npc name>");
	}
}
