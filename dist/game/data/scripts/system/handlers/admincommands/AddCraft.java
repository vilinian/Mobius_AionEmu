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
 * Handles the admin command to add a craftable item to a player.<br>
 * This class allows administrators to grant specific crafting recipes or items via the console.
 * @author Lilith
 */
public class AddCraft extends AdminCommand
{
	private static final String LIST = "list";
	private static final String ESSENCETAPPING = "essencetapping";
	private static final String AETHERTAPPING = "aethertapping";
	private static final String COOKING = "cooking";
	private static final String WEAPONSMITHING = "weaponsmithing";
	private static final String ARMORSMITHING = "armorsmithing";
	private static final String TAILORING = "tailoring";
	private static final String ALCHEMY = "alchemy";
	private static final String HANDICRAFTING = "handicrafting";
	private static final String CONSTRUCTION = "construction";
	
	/**
	 * Initializes a new instance of the {@link AddCraft} class.<br>
	 * This command allows administrators to manage crafting permissions.
	 */
	public AddCraft()
	{
		super("addcraft");
	}
	
	/**
	 * Executes the command to grant a crafting skill to a player.<br>
	 * It parses the provided parameters to determine which skill to add.<br>
	 * If no parameters are provided, it displays the help menu.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments containing the skill type and other details.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length == 0)
		{
			showHelp(player);
			return;
		}
		
		if (LIST.equalsIgnoreCase(params[0]))
		{
			showList(player);
		}
		else if (ESSENCETAPPING.equalsIgnoreCase(params[0]))
		{
			addEssencetapping(player, params);
		}
		else if (AETHERTAPPING.equalsIgnoreCase(params[0]))
		{
			addAethertapping(player, params);
		}
		else if (COOKING.equalsIgnoreCase(params[0]))
		{
			addCooking(player, params);
		}
		else if (WEAPONSMITHING.equalsIgnoreCase(params[0]))
		{
			addWeaponsmithing(player, params);
		}
		else if (ARMORSMITHING.equalsIgnoreCase(params[0]))
		{
			addArmorsmithing(player, params);
		}
		else if (TAILORING.equalsIgnoreCase(params[0]))
		{
			addTailoring(player, params);
		}
		else if (ALCHEMY.equalsIgnoreCase(params[0]))
		{
			addAlchemy(player, params);
		}
		else if (HANDICRAFTING.equalsIgnoreCase(params[0]))
		{
			addHandicrafting(player, params);
		}
		else if (CONSTRUCTION.equalsIgnoreCase(params[0]))
		{
			addConstruction(player, params);
		}
	}
	
	/**
	 * Displays the list of available crafting types to a player.<br>
	 * This method sends a message containing all valid craft names.
	 * @param player The {@link Player} who will receive the message.
	 */
	protected void showList(Player player)
	{
		PacketSendUtility.sendMessage(player, "\n" + "AddCraft Name List:\n" + "- Essencetapping\n" + "- Aethertapping\n" + "- Cooking\n" + "- Weaponsmithing\n" + "- Armorsmithing\n" + "- Tailoring\n" + "- Alchemy\n" + "- Handicrafting\n" + "- Construction");
	}
	
	/**
	 * Adds the {@code Essencetapping} skill to a target player.<br>
	 * This method updates the target's skill level based on the provided parameters.<br>
	 * It sends confirmation messages to both the administrator and the target.
	 * @param player The {@link Player} who is executing the command.
	 * @param params An array of strings where {@code params[1]} must be a valid integer for the skill level.
	 */
	protected void addEssencetapping(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 30002, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Essencetapping.");
			PacketSendUtility.sendMessage(targetpl, "Your Essencetapping is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * This method adds the {@code Aethertapping} skill to a target player.<br>
	 * It requires the admin to select a valid target and a skill level.<br>
	 * The skill is added using the internal ID {@code 30003}.
	 * @param player The {@link Player} who is executing the command.
	 * @param params An array of strings where {@code params[1]} is the new skill level.
	 */
	protected void addAethertapping(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 30003, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Aethertapping.");
			PacketSendUtility.sendMessage(targetpl, "Your Aethertapping is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Adds the {@code cooking} skill to a target player.<br>
	 * This method requires the target to be another {@link Player}.<br>
	 * It updates the target's skill level based on the provided parameters.
	 * @param player The admin player executing the command.
	 * @param params An array where index 1 must be an integer representing the new skill level.
	 */
	protected void addCooking(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 40001, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Cooking.");
			PacketSendUtility.sendMessage(targetpl, "Your Cooking is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Grants the weaponsmithing skill to a target player.<br>
	 * This method updates the target's skill level based on the provided parameters.
	 * @param player The {@code Player} who is executing the command.
	 * @param params An array of strings where {@code params[1]} is the new skill level.
	 */
	protected void addWeaponsmithing(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 40002, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Weaponsmithing.");
			PacketSendUtility.sendMessage(targetpl, "Your Weaponsmithing is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Adds the Armorsmithing skill to a target player.<br>
	 * This method updates the skill level based on the provided parameters.<br>
	 * It sends a confirmation message to both the administrator and the target.
	 * @param player The {@code Player} executing the command.
	 * @param params A {@code String[]} containing the target information and the new skill level.
	 */
	protected void addArmorsmithing(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 40003, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Armorsmithing.");
			PacketSendUtility.sendMessage(targetpl, "Your Armorsmithing is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Adds the Tailoring skill to a target player.<br>
	 * This method requires a level as the second parameter in {@code params}.<br>
	 * It sends a confirmation message to both the administrator and the target.
	 * @param player The {@link Player} who is executing the command.
	 * @param params An array of strings containing the skill level.
	 */
	protected void addTailoring(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 40004, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Tailoring.");
			PacketSendUtility.sendMessage(targetpl, "Your Tailoring is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Adds the {@code alchemy} skill to a target player.<br>
	 * This method updates the target's skill level based on the provided parameters.<br>
	 * It sends a confirmation message to both the administrator and the target.
	 * @param player The {@link Player} who is executing the command.
	 * @param params A {@code String[]} containing the target information and the new skill level.
	 */
	protected void addAlchemy(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 40007, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Alchemy.");
			PacketSendUtility.sendMessage(targetpl, "Your Alchemy is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Adds the {@code handicrafting} skill to a target player.<br>
	 * This method updates the skill level based on the provided parameters.<br>
	 * It sends a confirmation message to both the administrator and the target.
	 * @param player The {@link Player} who is executing the command.
	 * @param params An array of strings containing the target information and the new skill level.
	 */
	protected void addHandicrafting(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 40008, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Handicrafting.");
			PacketSendUtility.sendMessage(targetpl, "Your Handicrafting is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Adds the {@code construction} skill to a target player.<br>
	 * This method updates the target's skill level based on the provided parameters.<br>
	 * It sends a confirmation message to both the administrator and the target.
	 * @param player The {@link Player} who is executing the command.
	 * @param params A {@code String[]} containing the target identifier and the new skill level.
	 */
	protected void addConstruction(Player player, String[] params)
	{
		final VisibleObject target = player.getTarget();
		
		int skillLevel = 0;
		
		try
		{
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters need to be an integer.");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, 40010, skillLevel);
			PacketSendUtility.sendMessage(player, "You acquired the skill Construction.");
			PacketSendUtility.sendMessage(targetpl, "Your Construction is now on Skill Level " + params[1] + ".");
		}
	}
	
	/**
	 * Displays the help message for the {@code //addcraft} command.<br>
	 * It informs the user how to add skills and how to view the available options.
	 * @param player The {@link Player} who will receive the help message.
	 */
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "\n" + "AddCraft Help:\n" + "Please target yourself and type\n" + "//addcraft <CraftName> <SkillLevel>\n" + "or if you need a list of possible craft names type\n" + "//addcraft list");
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
		PacketSendUtility.sendMessage(player, "\n" + "AddCraft Help:\n" + "Please target yourself and type\n" + "//addcraft <CraftName> <SkillLevel>\n" + "or if you need a list of possible craft names type\n" + "//addcraft list");
	}
	
}
