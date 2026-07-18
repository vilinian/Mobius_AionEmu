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
package com.aionemu.gameserver.ai2.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcUiType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the visual appearance and rendering logic for {@link Npc} entities.<br>
 * It handles how objects are displayed to players within the game world.
 * @author Alcapwnd
 */
public class LookManager
{
	private static byte hBeforeChange = 0;
	private static byte hAfterChange = 0;
	private static byte hFinalChange = 0;
	private static final Logger log = LoggerFactory.getLogger(LookManager.class);
	
	/**
	 * Adjusts the facing direction of an {@link Npc}.<br>
	 * This method checks if the NPC should face the {@link Player} or move randomly.<br>
	 * It respects settings in {@code AIConfig.ACTIVE_NPC_LOOKING}.
	 * @param npc The NPC object to update.
	 * @param player The player character used as a reference point.
	 */
	public static void corrigateHeading(Npc npc, Player player)
	{
		if (!AIConfig.ACTIVE_NPC_LOOKING || (npc == null))
		{
			return;
		}
		
		if (npc.isBoss() || npc.isAttackableNpc())
		{
			randomHeading(npc, player);
		}
		else if (npc.isFlag() || npc.isRaidMonster() || isValidRace(npc.getRace()) || isValidTribe(npc.getTribe()) || isValidUiType(npc.getUiType()))
		{
			return;
		}
		else
		{
			coordinateHeading(npc, player);
		}
	}
	
	/**
	 * Checks if a specific {@link TribeClass} is allowed.<br>
	 * It returns {@code true} for valid types like {@code TELEPORTOR_LI}.<br>
	 * It returns {@code false} for any other values.
	 * @param tribe The {@code TribeClass} to validate.
	 * @return {@code true} if the tribe is valid, otherwise {@code false}.
	 */
	private static boolean isValidTribe(TribeClass tribe)
	{
		switch (tribe)
		{
			case TELEPORTOR_LI:
				return true;
			case TELEPORTOR_DA:
				return true;
			case FIELD_OBJECT_ALL:
				return true;
			case FIELD_OBJECT_LIGHT:
				return true;
			case FIELD_OBJECT_DARK:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Checks if a specific {@link Race} is allowed.<br>
	 * It returns {@code true} for valid door races.<br>
	 * It returns {@code false} for all other types.
	 * @param race The {@code Race} object to validate.
	 * @return {@code true} if the race is valid, otherwise {@code false}.
	 */
	private static boolean isValidRace(Race race)
	{
		switch (race)
		{
			case DRAGON_CASTLE_DOOR:
				return true;
			case PC_LIGHT_CASTLE_DOOR:
				return true;
			case PC_DARK_CASTLE_DOOR:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Checks if the provided {@code NpcUiType} is a valid type for crafting.<br>
	 * It returns {@code true} only for specific craft-related types.
	 * @param uiType The {@code NpcUiType} to validate.
	 * @return {@code true} if the type is valid, otherwise {@code false}.
	 */
	private static boolean isValidUiType(NpcUiType uiType)
	{
		switch (uiType)
		{
			case CRAFT:
				return true;
			case CRAFT_UI_ALWAYS:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Assigns a random heading to an {@link Npc}.<br>
	 * This method ensures the new heading is different from the current one.<br>
	 * It then calls {@code Player)} to apply the change.
	 * @param npc The {@link Npc} object to modify.
	 * @param player The {@link Player} object associated with the action.
	 */
	private static void randomHeading(Npc npc, Player player)
	{
		hBeforeChange = npc.getPosition().getHeading();
		int randomHeading = Rnd.get(1, 100);
		hAfterChange = (byte) randomHeading;
		
		if (hBeforeChange == hAfterChange)
		{
			randomHeading = Rnd.get(1, 100);
			hAfterChange = (byte) randomHeading;
		}
		
		npc.getPosition().setH(hAfterChange);
		updateHeading(npc, player);
		hBeforeChange = 0;
		hAfterChange = 0;
		
	}
	
	/**
	 * This method adjusts the heading of an {@link Npc}.<br>
	 * It calculates a new rotation based on the current state.<br>
	 * The final result is applied to the {@code npc} position.<br>
	 * It then calls {@code Player)} to refresh the view.
	 * @param npc The {@code Npc} object to update.
	 * @param player The {@code Player} observing the NPC.
	 */
	private static void coordinateHeading(Npc npc, Player player)
	{
		int randomHeading = 0;
		if (npc.getOldHeading() == 0)
		{
			randomHeading = Rnd.get(1, 100);
			hAfterChange = (byte) randomHeading;
			npc.getPosition().setH(hAfterChange);
			npc.setOldHeading(hAfterChange);
		}
		else
		{
			hBeforeChange = npc.getOldHeading();
			randomHeading = Rnd.get(hBeforeChange, hBeforeChange + 5);
			hAfterChange = (byte) randomHeading;
			hFinalChange = (byte) (hBeforeChange + hAfterChange);
			npc.getPosition().setH(hFinalChange);
			npc.setOldHeading(hFinalChange);
		}
		
		updateHeading(npc, player);
		hBeforeChange = 0;
		hAfterChange = 0;
	}
	
	/**
	 * Updates the heading of an {@link Npc} for a specific {@link Player}.<br>
	 * This method sends an {@code SM_NPC_INFO} packet to synchronize the view.
	 * @param npc The {@code Npc} object to update.
	 * @param player The {@code Player} who needs to receive the update.
	 */
	private static void updateHeading(Npc npc, Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_NPC_INFO(npc, player));
	}
	
	/**
	 * Initializes the {@code LookManager} state.<br>
	 * This method resets all heading change variables to {@code 0}.<br>
	 * It logs a message to indicate that the manager has started.
	 */
	public void onStart()
	{
		hBeforeChange = 0;
		hAfterChange = 0;
		hFinalChange = 0;
		log.info("LookManager initialized");
	}
	
	private static class SingletonHolder
	{
		protected static final LookManager instance = new LookManager();
	}
	
	/**
	 * Provides access to the singleton instance of {@link LookManager}.<br>
	 * Use this method to get the global manager for look-related operations.
	 * @return The single instance of {@code LookManager}.
	 */
	public static LookManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
}
