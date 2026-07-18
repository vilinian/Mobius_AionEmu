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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles player commands related to the skill system.<br>
 * This class processes requests for learning or managing skills using {@link SkillLearnService}.<br>
 * It allows players to interact with their character's abilities via chat commands.
 * @author ATracer
 */
public class cmd_skills extends PlayerCommand
{
	/**
	 * Initializes the {@code skills} command handler.<br>
	 * This constructor registers the command for use by players.<br>
	 * It calls the superclass constructor to set the command name.
	 */
	public cmd_skills()
	{
		super("skills");
	}
	
	/**
	 * Automatically adds all missing skills to the specified player.<br>
	 * This method interacts with the {@link SkillLearnService}.
	 * @param player The {@code Player} who will receive the new skills.
	 * @param params Additional arguments that are not used by this command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		SkillLearnService.addMissingSkills(player);
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
		PacketSendUtility.sendMessage(player, "Syntax : .skills");
	}
}
