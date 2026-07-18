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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to deal damage to a specific target.<br>
 * It allows administrators to modify the health of {@link Creature} or {@link Player} objects.
 * @author Source
 */
public class Damage extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Damage} command.<br>
	 * This class handles the admin command for dealing damage to entities.
	 */
	public Damage()
	{
		super("damage");
	}
	
	/**
	 * Executes the command to deal damage to a targeted creature.<br>
	 * It allows for both fixed values and percentage-based calculations.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the damage amount or percentage.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length > 1)
		{
			onFail(admin, null);
		}
		
		final VisibleObject target = admin.getTarget();
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "No target selected");
		}
		else if (target instanceof Creature)
		{
			final Creature creature = (Creature) target;
			int dmg;
			try
			{
				final String percent = params[0];
				final Pattern damage = Pattern.compile("([^%]+)%");
				final Matcher result = damage.matcher(percent);
				
				if (result.find())
				{
					dmg = Integer.parseInt(result.group(1));
					
					if (dmg < 100)
					{
						creature.getController().onAttack(admin, (int) ((dmg / 100f) * creature.getLifeStats().getMaxHp()), true);
					}
					else
					{
						creature.getController().onAttack(admin, creature.getLifeStats().getMaxHp() + 1, true);
					}
				}
				else
				{
					creature.getController().onAttack(admin, Integer.parseInt(params[0]), true);
				}
			}
			catch (Exception ex)
			{
				onFail(admin, null);
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
		PacketSendUtility.sendMessage(player, "syntax //damage <dmg | dmg%>" + "\n<dmg> must be a number.");
	}
}
