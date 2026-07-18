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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /job} player command.<br>
 * This class processes requests to display or manage character job information.<br>
 * It extends {@link PlayerCommand} to integrate with the game's chat system.
 */
public class cmd_job extends PlayerCommand
{
	/**
	 * Initializes a new instance of the {@code cmd_job} command.<br>
	 * This class handles the {@code job} player command.<br>
	 * It extends the functionality provided by {@link PlayerCommand}.
	 */
	public cmd_job()
	{
		super("job");
	}
	
	/**
	 * Grants a set of specific skills to the player.<br>
	 * This method adds multiple skill IDs and levels to the {@code Player} object.
	 * @param player The {@link Player} who will receive the new skills.
	 * @param params Optional arguments that are not used by this command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		player.getSkillList().addSkill(player, 30002, 499); // Vita
		player.getSkillList().addSkill(player, 30003, 499); // Ether
		player.getSkillList().addSkill(player, 40001, 550); // Cuisine
		player.getSkillList().addSkill(player, 40002, 550); // Armes
		player.getSkillList().addSkill(player, 40003, 550); // Armure
		player.getSkillList().addSkill(player, 40004, 550); // Couture
		player.getSkillList().addSkill(player, 40007, 550); // Alchimie
		player.getSkillList().addSkill(player, 40008, 550); // Artisanat
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
		PacketSendUtility.sendMessage(player, "Syntax: .job ");
	}
}
