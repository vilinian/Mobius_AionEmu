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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to change a player's race.<br>
 * It allows administrators to modify a {@link Player} object to a new {@link Race}.<br>
 * This class ensures that the character's data is updated and synchronized correctly.
 * @author ginho1
 */
public class ChangeRace extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link ChangeRace} command.<br>
	 * This class handles the logic for changing a player's race via an admin command.
	 */
	public ChangeRace()
	{
		super("changerace");
	}
	
	/**
	 * Changes the race of the administrator who runs this command.<br>
	 * It toggles between {@code Race.ELYOS} and {@code Race.ASMODIANS}.<br>
	 * The method also updates the player's known list and sends a new packet.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings that are not used by this command.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (admin.getCommonData().getRace() == Race.ELYOS)
		{
			admin.getCommonData().setRace(Race.ASMODIANS);
		}
		else
		{
			admin.getCommonData().setRace(Race.ELYOS);
		}
		
		admin.clearKnownlist();
		PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
		admin.updateKnownlist();
	}
}
