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
package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;

/**
 * This service manages the logic for the {@code Abyss} system.<br>
 * It handles player rankings, rewards, and related game mechanics.<br>
 * Use this class to interact with {@link Player} data regarding abyss progression.
 * @author ATracer
 */
public class AbyssService
{
	private static final int[] abyssMapList =
	{
		// //***////
		210020000, // Elten
		210040000, // Heiron
		210050000, // Inggison
		210060000, // Theobomos
		210070000, // Cygnea
		210090000, // Idian Depths.
		210100000, // Iluma
		220020000, // Morheim
		220040000, // Beluslan
		220050000, // Brusthonin
		220070000, // Gelkmaros
		220080000, // Enshar
		220100000, // Idian Depths
		220110000, // Norvsvold
		400010000, // Reshanta
		400020000, // Belus
		400040000, // Aspida
		400050000, // Atanatos
		400060000, // Disillon
		600010000, // Silentera Canyon
		600090000, // Kaldor
		600100000
	}; // Levinshor
	
	/**
	 * Checks if a {@link Player} is currently located on a PvP map.<br>
	 * It compares the player's world ID against the known abyss map list.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is on a PvP map, otherwise {@code false}.
	 */
	public static boolean isOnPvpMap(Player player)
	{
		for (int i : abyssMapList)
		{
			if (i == player.getWorldId())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sends a system message to all players when a ranked player dies.<br>
	 * This method checks if the observer is in the same world as the {@code victim}.<br>
	 * It excludes players who are currently inside an instance.
	 * @param victim The {@link Player} who was killed.
	 */
	public static void rankedKillAnnounce(Player victim)
	{
		World.getInstance().doOnAllPlayers(p ->
		{
			if ((p != victim) && (victim.getWorldType() == p.getWorldType()) && !p.isInInstance())
			{
				PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_ABYSS_ORDER_RANKER_DIE(victim, AbyssRankEnum.getRankDescriptionId(victim)));
			}
		});
	}
	
	/**
	 * Sends a system message to all players in the same world.<br>
	 * This message announces that a specific skill was used by a player.<br>
	 * It excludes the original {@code player} and those inside instances.
	 * @param player The {@link Player} who performed the action.
	 * @param nameId The unique identifier for the skill description.
	 */
	public static void rankerSkillAnnounce(Player player, int nameId)
	{
		World.getInstance().doOnAllPlayers(p ->
		{
			if ((p != player) && (player.getWorldType() == p.getWorldType()) && !p.isInInstance())
			{
				PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_SKILL_ABYSS_SKILL_IS_FIRED(player, new DescriptionId(nameId)));
			}
		});
	}
}
