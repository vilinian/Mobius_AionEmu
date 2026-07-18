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

import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunctionProxy;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command for modifying character statistics.<br>
 * It allows administrators to adjust various {@link StatEnum} values for a specific target.<br>
 * This class interacts with {@link StatOwner} to apply changes to players or creatures.
 * @author MrPoke
 */
public class Stat extends AdminCommand
{
	private static final Logger log = LoggerFactory.getLogger(Stat.class);
	
	/**
	 * Initializes a new instance of the {@link Stat} command.<br>
	 * This constructor sets up the default behavior for the admin command.
	 */
	public Stat()
	{
		super("stat");
	}
	
	/**
	 * Executes the command to view or detail stats of a targeted creature.<br>
	 * It requires a valid target and a {@code StatEnum} as the first parameter.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the stat type.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length >= 1)
		{
			final VisibleObject target = admin.getTarget();
			if (target == null)
			{
				PacketSendUtility.sendMessage(admin, "No target selected");
				return;
			}
			
			if (target instanceof Creature)
			{
				final Creature creature = (Creature) target;
				
				final TreeSet<IStatFunction> stats = creature.getGameStats().getStatsByStatEnum(StatEnum.valueOf(params[0]));
				
				if (params.length == 1)
				{
					for (IStatFunction stat : stats)
					{
						PacketSendUtility.sendMessage(admin, stat.toString());
					}
				}
				else if ("details".equals(params[1]))
				{
					for (IStatFunction stat : stats)
					{
						final String details = collectDetails(stat);
						PacketSendUtility.sendMessage(admin, details);
						log.info(details);
					}
				}
			}
		}
	}
	
	/**
	 * This method gathers information about a specific {@code IStatFunction}.<br>
	 * It builds a string containing the function name and its owner details.<br>
	 * If the function is a proxy, it includes the proxied function name.<br>
	 * If the owner is an {@link Effect}, it adds the skill ID and name.
	 * @param stat The {@code IStatFunction} to extract details from.
	 * @return A formatted string containing all collected information.
	 */
	private String collectDetails(IStatFunction stat)
	{
		final StringBuffer sb = new StringBuffer();
		sb.append(stat.toString() + "\n");
		if (stat instanceof StatFunctionProxy)
		{
			final StatFunctionProxy proxy = (StatFunctionProxy) stat;
			sb.append(" -- " + proxy.getProxiedFunction().toString());
		}
		
		final StatOwner owner = stat.getOwner();
		if (owner instanceof Effect)
		{
			final Effect effect = (Effect) owner;
			sb.append("\n -- skillId: " + effect.getSkillId());
			sb.append("\n -- skillName: " + effect.getSkillName());
		}
		
		return sb.toString();
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
