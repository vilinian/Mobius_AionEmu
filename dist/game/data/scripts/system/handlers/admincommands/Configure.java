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

import java.lang.reflect.Field;
import java.util.Map;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.BaseConfig;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.DropConfig;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.main.FallDamageConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.HTMLConfig;
import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.NameConfig;
import com.aionemu.gameserver.configs.main.PeriodicSaveConfig;
import com.aionemu.gameserver.configs.main.PricesConfig;
import com.aionemu.gameserver.configs.main.PunishmentConfig;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.configs.main.ShutdownConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.main.ThreadConfig;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.configs.network.IPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the {@code Configure} admin command to modify server settings dynamically.<br>
 * This class allows administrators to update various configuration values without restarting the server.
 * @author ATracer
 * @modified Rolandas
 */
public class Configure extends AdminCommand
{
	private static final Map<String, Class<?>> commands = Map.ofEntries(Map.entry("admin", AdminConfig.class), Map.entry("ai", AIConfig.class), Map.entry("autogroup", AutoGroupConfig.class), Map.entry("base", BaseConfig.class), Map.entry("cache", CacheConfig.class), Map.entry("craft", CraftConfig.class), Map.entry("custom", CustomConfig.class), Map.entry("developer", DeveloperConfig.class), Map.entry("drop", DropConfig.class), Map.entry("enchants", EnchantsConfig.class), Map.entry("events", EventsConfig.class), Map.entry("falldamage", FallDamageConfig.class), Map.entry("gameserver", GSConfig.class), Map.entry("geodata", GeoDataConfig.class), Map.entry("group", GroupConfig.class), Map.entry("html", HTMLConfig.class), Map.entry("housing", HousingConfig.class), Map.entry("legions", LegionConfig.class), Map.entry("logging", LoggingConfig.class), Map.entry("membership", MembershipConfig.class), Map.entry("name", NameConfig.class), Map.entry("periodicsave", PeriodicSaveConfig.class), Map.entry("prices", PricesConfig.class), Map.entry("punishment", PunishmentConfig.class), Map.entry("ranking", RankingConfig.class), Map.entry("rates", RateConfig.class), Map.entry("security", SecurityConfig.class), Map.entry("shutdown", ShutdownConfig.class), Map.entry("siege", SiegeConfig.class), Map.entry("thread", ThreadConfig.class), Map.entry("weddings", WeddingsConfig.class), Map.entry("world", WorldConfig.class), Map.entry("ipconfig", IPConfig.class), Map.entry("network", NetworkConfig.class));
	
	/**
	 * Initializes the configuration for this command.<br>
	 * It sets up the necessary settings to handle admin commands.
	 */
	public Configure()
	{
		super("configure");
	}
	
	/**
	 * Executes the command to view or modify configuration properties.<br>
	 * It allows administrators to see current values or update them dynamically.<br>
	 * The method supports both {@code show} and {@code set} actions for various config classes.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the action, config name, property name, and optional new value.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		String command = "";
		if (params.length == 3)
		{
			// show
			command = params[0];
			if (!"show".equalsIgnoreCase(command))
			{
				PacketSendUtility.sendMessage(player, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
				return;
			}
		}
		else if (params.length == 4)
		{
			// set
			command = params[0];
			if (!"set".equalsIgnoreCase(command))
			{
				PacketSendUtility.sendMessage(player, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
				return;
			}
		}
		else
		{
			PacketSendUtility.sendMessage(player, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
			return;
		}
		
		final Class<?> classToMofify = commands.get(params[1].toLowerCase());
		
		if (command.equalsIgnoreCase("show"))
		{
			final String fieldName = params[2];
			Field someField;
			try
			{
				someField = classToMofify.getDeclaredField(fieldName.toUpperCase());
				PacketSendUtility.sendMessage(player, "Current value is " + someField.get(null));
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(player, "Error! Wrong property or value.");
				return;
			}
		}
		else if (command.equalsIgnoreCase("set"))
		{
			final String fieldName = params[2];
			final String newValue = params[3];
			if (classToMofify != null)
			{
				Field someField;
				try
				{
					someField = classToMofify.getDeclaredField(fieldName.toUpperCase());
					final Class<?> classType = someField.getType();
					if (classType == String.class)
					{
						someField.set(null, newValue);
					}
					else if ((classType == int.class) || (classType == Integer.class))
					{
						someField.set(null, Integer.parseInt(newValue));
					}
					else if ((classType == Boolean.class) || (classType == boolean.class))
					{
						someField.set(null, Boolean.valueOf(newValue));
					}
					else if ((classType == byte.class) || (classType == Byte.class))
					{
						someField.set(null, Byte.valueOf(newValue));
					}
					else if ((classType == float.class) || (classType == Float.class))
					{
						someField.set(null, Float.valueOf(newValue));
					}
					
				}
				catch (Exception e)
				{
					PacketSendUtility.sendMessage(player, "Error! Wrong property or value.");
					return;
				}
			}
			
			PacketSendUtility.sendMessage(player, "Property changed and applyed");
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
		PacketSendUtility.sendMessage(player, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
	}
}
