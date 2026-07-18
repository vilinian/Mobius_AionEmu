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
package com.aionemu.gameserver.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.GameServer.StartupHook;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.Race;

/**
 * This utility class handles the retrieval and management of {@code ZCX} related information.<br>
 * It provides helper methods to interact with game data and configurations.
 * @author Alcapwnd
 */
public class ZCXInfo
{
	private static String VERSION = null;
	private static String MINOR = null;
	private static String COPYRIGHT = null;
	private static final Logger log = LoggerFactory.getLogger(ZCXInfo.class);
	private static int ELYOS_COUNT = 0;
	private static int ASMOS_COUNT = 0;
	private static double ELYOS_RATIO = 0.0;
	private static double ASMOS_RATIO = 0.0;
	private static final ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Displays the server information and a decorative ASCII art logo to the console.<br>
	 * This method calls {@code readInfo} to load data before printing.<br>
	 * It shows the version, minor patch, and copyright details.
	 * @throws IOException If an error occurs while reading the information file.
	 */
	public static void getInfo() throws IOException
	{
		readInfo();
		System.out.println("");
		System.out.println("                            ooo        ooooo            .o8        o8o");
		System.out.println("                            `88.       .888'           \"888        `\"'");
		System.out.println("                             888b     d'888   .ooooo.   888oooo.  oooo  oooo  oooo   .oooo.o");
		System.out.println("                             8 Y88. .P  888  d88' `88b  d88' `88b `888  `888  `888  d88(  \"8");
		System.out.println("                             8  `888'   888  888   888  888   888  888   888   888  `\"Y88b.");
		System.out.println("                             8    Y     888  888   888  888   888  888   888   888  o.  )88b");
		System.out.println("                            o8o        o888o `Y8bod8P'  `Y8bod8P' o888o  `V88V\"V8P' 8\"\"888P'");
		System.out.println("");
		System.out.println("                       .o.        o8o                        oooooooooooo");
		System.out.println("                      .888.       `\"'                        `888'     `8");
		System.out.println("                     .8\"888.     oooo   .ooooo.  ooo. .oo.    888         ooo. .oo.  .oo.   oooo  oooo");
		System.out.println("                    .8' `888.    `888  d88' `88b `888P\"Y88b   888oooo8    `888P\"Y88bP\"Y88b  `888  `888");
		System.out.println("                   .88ooo8888.    888  888   888  888   888   888    \"     888   888   888   888   888");
		System.out.println("                  .8'     `888.   888  888   888  888   888   888       o  888   888   888   888   888");
		System.out.println("                 o88o     o8888o o888o `Y8bod8P' o888o o888o o888ooooood8 o888o o888o o888o  `V88V\"V8P'");
		System.out.println("");
		System.out.println("############################### This is Mobius AionEmu - based on an AionGermany source ###############################");
		System.out.println("");
		System.out.println("");
		System.out.println("\t\t\t\t\tThanks to all who helped this project!");
		System.out.println("\t\t\t\t\t\tMajor Patch: " + getVersion());
		System.out.println("\t\t\t\t\t\tMinor Patch: " + getMinor());
		System.out.println("\t\t\t\t\t\tCopyright: " + getCopyright());
		System.out.println("");
	}
	
	/**
	 * Reads configuration data from the {@code info.txt} file.<br>
	 * This method populates the version, minor, and copyright fields.<br>
	 * It uses {@code setVersion}, {@code setMinor}, and {@code setCopyright} to store the values.
	 * @throws IOException If an error occurs while reading the file.
	 */
	public static void readInfo() throws IOException
	{
		FileReader fr = null;
		try
		{
			fr = new FileReader("./config/info.txt");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		final BufferedReader br = new BufferedReader(fr);
		
		setVersion(br.readLine());
		setMinor(br.readLine());
		setCopyright(br.readLine());
		br.close();
	}
	
	/**
	 * Updates the current software version.<br>
	 * This method sets the {@code VERSION} variable to a new value.
	 * @param version The new version string to store.
	 */
	public static void setVersion(String version)
	{
		VERSION = version;
	}
	
	/**
	 * Sets the minor version of the application.<br>
	 * This updates the {@code MINOR} field in the {@link ZCXInfo} class.
	 * @param minor The new minor version string to set.
	 */
	public static void setMinor(String minor)
	{
		MINOR = minor;
	}
	
	/**
	 * Sets the global copyright string.<br>
	 * This value is used by {@code getInfo} to display information.
	 * @param copyright The new copyright text to store.
	 */
	public static void setCopyright(String copyright)
	{
		COPYRIGHT = copyright;
	}
	
	/**
	 * Retrieves the current version of the application.<br>
	 * This method returns the {@code VERSION} string.
	 * @return The version string as a {@code String}.
	 */
	public static String getVersion()
	{
		return VERSION;
	}
	
	/**
	 * Retrieves the minor version of the software.<br>
	 * This value is set during the initialization process.
	 * @return The {@code String} representing the minor version.
	 */
	public static String getMinor()
	{
		return MINOR;
	}
	
	/**
	 * Retrieves the copyright information for the project.<br>
	 * This method returns the value stored in the {@code COPYRIGHT} variable.
	 * @return The copyright string as a {@code String}.
	 */
	public static String getCopyright()
	{
		return COPYRIGHT;
	}
	
	/**
	 * Checks if the ratio limitation feature is enabled in {@link GSConfig}.<br>
	 * It registers a startup hook to count characters for each race.<br>
	 * This method initializes the population data when the server starts.
	 */
	public static void checkForRatioLimitation()
	{
		if (GSConfig.ENABLE_RATIO_LIMITATION)
		{
			GameServer.addStartupHook(new StartupHook()
			{
				@Override
				public void onStartup()
				{
					lock.lock();
					try
					{
						ASMOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ASMODIANS);
						ELYOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ELYOS);
						computeRatios();
					}
					catch (Exception e)
					{
						log.error("[Error] Something went wrong on checking ratio limitation");
						e.printStackTrace();
					}
					finally
					{
						lock.unlock();
					}
					
					displayRatios(false);
				}
			});
		}
	}
	
	/**
	 * Updates the count for a specific {@link Race}.<br>
	 * This method increments the internal counter and recalculates the ratios.<br>
	 * It ensures thread safety using a lock.
	 * @param race The type of {@code Race} to update.
	 * @param i The amount to add to the count.
	 */
	public static void updateRatio(Race race, int i)
	{
		lock.lock();
		try
		{
			switch (race)
			{
				case ASMODIANS:
					ASMOS_COUNT += i;
					break;
				case ELYOS:
					ELYOS_COUNT += i;
					break;
				default:
					break;
			}
			
			computeRatios();
		}
		catch (Exception e)
		{
			log.error("[Error] Cant update ratio limits");
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		
		displayRatios(true);
	}
	
	/**
	 * Calculates the percentage distribution between {@code ASMOS_COUNT} and {@code ELYOS_COUNT}.<br>
	 * It uses values from {@code RATIO_MIN_CHARACTERS_COUNT} to handle small sample sizes.<br>
	 * The results are stored in the {@code ASMOS_RATIO} and {@code ELYOS_RATIO} variables.
	 */
	private static void computeRatios()
	{
		if ((ASMOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT) && (ELYOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT))
		{
			ASMOS_RATIO = ELYOS_RATIO = 50.0;
		}
		else
		{
			ASMOS_RATIO = (ASMOS_COUNT * 100.0) / (ASMOS_COUNT + ELYOS_COUNT);
			ELYOS_RATIO = (ELYOS_COUNT * 100.0) / (ASMOS_COUNT + ELYOS_COUNT);
		}
	}
	
	/**
	 * Prints the current faction ratios to the server log.<br>
	 * It shows the percentage for both Elyos and Asmodians.
	 * @param updated Indicates if the ratios were recently refreshed. Use {@code true} if they were just recalculated, otherwise use {@code false}.
	 */
	private static void displayRatios(boolean updated)
	{
		GameServer.log.info("[GameServer] Actual Factions Ratio " + (updated ? "updated " : "") + ": Elyos " + String.format("%.1f", ELYOS_RATIO) + " % - Asmodians " + String.format("%.1f", ASMOS_RATIO) + " %");
	}
	
	/**
	 * Retrieves the current ratio for a specific {@link Race}.<br>
	 * This method checks if the race is {@code ASMODIANS} or {@code ELYOS}.<br>
	 * It returns {@code 0.0} if the race does not match.
	 * @param race The {@code Race} type to check.
	 * @return The calculated ratio as a {@code double}.
	 */
	public static double getRatiosFor(Race race)
	{
		switch (race)
		{
			case ASMODIANS:
				return ASMOS_RATIO;
			case ELYOS:
				return ELYOS_RATIO;
			default:
				return 0.0;
		}
	}
	
	/**
	 * Retrieves the total count of players for a specific race.<br>
	 * This method checks the {@code Race} type and returns the corresponding count.
	 * @param race The {@link Race} to check.
	 * @return The number of players in that race, or 0 if the race is unknown.
	 */
	public static int getCountFor(Race race)
	{
		switch (race)
		{
			case ASMODIANS:
				return ASMOS_COUNT;
			case ELYOS:
				return ELYOS_COUNT;
			default:
				return 0;
		}
	}
}
