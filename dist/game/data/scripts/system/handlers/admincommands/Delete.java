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

import java.io.IOException;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to remove objects from the game world.<br>
 * It allows administrators to delete {@link Npc}, {@link VisibleObject}, and {@link Player} entities.
 * @author Luno
 */
public class Delete extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@code Delete} command.<br>
	 * This class handles the removal of objects in the game world.<br>
	 * It extends the {@link AdminCommand} class.
	 */
	public Delete()
	{
		super("delete");
	}
	
	/**
	 * Executes the command to delete a target {@link Npc}.<br>
	 * It checks if the target is a valid non-pooled and non-siege spawn.<br>
	 * The method removes the spawn from the world and saves the change.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments for additional command configuration.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final VisibleObject cre = player.getTarget();
		if (!(cre instanceof Npc))
		{
			PacketSendUtility.sendMessage(player, "Wrong target");
			return;
		}
		
		final Npc npc = (Npc) cre;
		final SpawnTemplate template = npc.getSpawn();
		if (template.hasPool())
		{
			PacketSendUtility.sendMessage(player, "Can't delete pooled spawn template");
			return;
		}
		
		if (template instanceof SiegeSpawnTemplate)
		{
			PacketSendUtility.sendMessage(player, "Can't delete siege spawn template");
			return;
		}
		
		npc.getController().onDelete();
		try
		{
			DataManager.SPAWNS_DATA2.saveSpawn(player, npc, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			PacketSendUtility.sendMessage(player, "Could not remove spawn");
			return;
		}
		
		PacketSendUtility.sendMessage(player, "Spawn removed");
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
