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
import com.aionemu.gameserver.services.toypet.PetAdoptionService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands related to the {@link com.aionemu.gameserver.services.toypet.PetAdoptionService}.<br>
 * This class allows administrators to manage and interact with player pets via chat commands.
 * @author ATracer
 */
public class Pet extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Pet} class.<br>
	 * This constructor initializes the command with the name {@code pet}.
	 */
	public Pet()
	{
		super("pet");
	}
	
	/**
	 * Executes the command to add a pet to a player.<br>
	 * It parses the provided parameters to identify the pet ID and name.<br>
	 * The method calls {@code addPet} to complete the action.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the command type, pet ID, and pet name.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final String command = params[0];
		if ("add".equals(command))
		{
			final int petId = Integer.parseInt(params[1]);
			final String name = params[2];
			PetAdoptionService.addPet(player, petId, name, 0, 0);
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
		PacketSendUtility.sendMessage(player, "syntax //pet <add [petid name]>");
	}
}
