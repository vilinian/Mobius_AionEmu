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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to remove effects from a target.<br>
 * This class allows administrators to clear buffs or debuffs from a {@link Player}.
 * @author Hilgert
 */
public class Dispel extends AdminCommand
{
	/**
	 * Initializes a new {@link Dispel} command.<br>
	 * This constructor registers the {@code dispel} admin command.
	 */
	public Dispel()
	{
		super("dispel");
	}
	
	/**
	 * Removes all active buff effects from a selected target.<br>
	 * The {@code admin} must have a valid {@link Player} or {@link VisibleObject} selected.<br>
	 * If the target is a {@code Player}, all their effects are cleared.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings for additional command arguments.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		Player target = null;
		final VisibleObject creature = admin.getTarget();
		
		if (creature == null)
		{
			PacketSendUtility.sendMessage(admin, "You should select a target first!");
			return;
		}
		
		if (creature instanceof Player)
		{
			target = (Player) creature;
			target.getEffectController().removeAllEffects();
			PacketSendUtility.sendMessage(admin, creature.getName() + " had all buff effects dispelled !");
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
