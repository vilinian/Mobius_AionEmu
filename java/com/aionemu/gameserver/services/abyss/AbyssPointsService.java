/*
 * This file is part of the Mobius AionEmu project.
 * 
 * Mobius AionEmu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Mobius AionEmu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.abyss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;

/**
 * Manages the logic for handling {@link Player} abyss points and ranks.<br>
 * This service processes point updates and synchronizes rank information with the client.
 * @author ATracer
 * @author ThunderBolt - GloryPoints
 * @author Mobius
 */
public class AbyssPointsService
{
	private static final Logger log = LoggerFactory.getLogger(AbyssPointsService.class);
	
	/**
	 * Adds Abyss Points to a specific {@link Player}.<br>
	 * This method updates the player's points based on an interaction with a {@code VisibleObject}.<br>
	 * It also logs a warning if the added value exceeds {@code 30000}.
	 * @param player The {@link Player} who will receive the points.
	 * @param obj The {@link VisibleObject} associated with the point gain.
	 * @param value The amount of points to add.
	 */
	public static void addAp(Player player, VisibleObject obj, int value)
	{
		if (value > 30000)
		{
			log.warn("WARN BIG COUNT AP: " + value + " name: " + obj.getName() + " obj: " + obj.getObjectId() + " player: " + player.getObjectId());
		}
		
		addAp(player, value);
		
		// Only Players or SiegeNpc(from SiegeModType.SIEGE or .ASSAULT) can add siege points.
		if ((obj instanceof Player) || ((obj instanceof SiegeNpc) && !((SiegeNpc) obj).getSpawn().isPeace()))
		{
			SiegeService.getInstance().onSiegeApGained(player, value);
		}
	}
	
	/**
	 * Adds Glory Points to a specific {@link Player}.<br>
	 * This method updates the player's total GP based on an interaction with a {@code VisibleObject}.<br>
	 * It also logs a warning if the added value exceeds {@code 10000}.
	 * @param player The {@link Player} who will receive the points.
	 * @param obj The {@link VisibleObject} associated with the point gain.
	 * @param value The amount of Glory Points to add.
	 */
	public static void addGp(Player player, VisibleObject obj, int value)
	{
		if (value > 10000)
		{
			log.warn("WARN BIG COUNT GP: " + value + " name: " + obj.getName() + " obj: " + obj.getObjectId() + " player: " + player.getObjectId());
		}
		
		addGp(player, value);
		
		// Only Players or SiegeNpc(from SiegeModType.SIEGE or .ASSAULT) can add siege points.
		if ((obj instanceof Player) || ((obj instanceof SiegeNpc) && !((SiegeNpc) obj).getSpawn().isPeace()))
		{
			SiegeService.getInstance().onSiegeGpGained(player, value);
		}
	}
	
	/**
	 * Adds a specific amount of Abyss Points to a {@link Player}.<br>
	 * This method sends a system message to the player regarding the change.<br>
	 * It also updates the contribution points for the player's legion if applicable.
	 * @param player The {@code Player} object to receive the points.
	 * @param value The amount of points to add or set.
	 */
	public static void addAp(Player player, int value)
	{
		if (player == null)
		{
			return;
		}
		
		// Notify player of AP gained (This should happen before setAp happens.)
		if (value > 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_ABYSS_POINT_GAIN(value));
		}
		else // You used %num0 Abyss Points.
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300965, value * -1));
		}
		
		// Set the new AP value
		setAp(player, value);
		
		// Add Abyss Points to Legion
		if (player.isLegionMember() && (value > 0))
		{
			player.getLegion().addContributionPoints(value);
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_EDIT(0x03, player.getLegion()));
		}
	}
	
	/**
	 * Adds glory points to a {@link Player}.<br>
	 * This method updates the player's total and sends a system message.<br>
	 * It calls {@code int)} to apply the change.
	 * @param player The {@code Player} receiving the points.
	 * @param value The amount of glory points to add or subtract.
	 */
	public static void addGp(Player player, int value)
	{
		if (player == null)
		{
			return;
		}
		
		if (value > 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_GAIN(value));
		}
		else if (value < 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_LOSE_COMMON(value));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402219, value * -1));
		}
		
		setGp(player, value);
	}
	
	/**
	 * Sets the Abyss Points for a specific player.<br>
	 * This method updates the {@code Player} rank and checks for any rank changes.<br>
	 * It also sends an {@link SM_ABYSS_RANK} packet to the client.
	 * @param player The {@code Player} object to modify.
	 * @param value The new amount of Abyss Points to set.
	 */
	public static void setAp(Player player, int value)
	{
		if (player == null)
		{
			return;
		}
		
		final AbyssRank rank = player.getAbyssRank();
		
		final AbyssRankEnum oldAbyssRank = rank.getRank();
		rank.addAp(value);
		final AbyssRankEnum newAbyssRank = rank.getRank();
		
		checkRankChanged(player, oldAbyssRank, newAbyssRank);
		
		PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK(player.getAbyssRank()));
	}
	
	/**
	 * Sets the Glory Points for a specific player.<br>
	 * This method updates the {@code AbyssRank} of the provided {@link Player}.<br>
	 * It also checks if the rank has changed and sends an update packet to the client.
	 * @param player The {@link Player} whose points will be updated.
	 * @param value The amount of Glory Points to add to the current total.
	 */
	public static void setGp(Player player, int value)
	{
		if (player == null)
		{
			return;
		}
		
		final AbyssRank rank = player.getAbyssRank();
		
		final AbyssRankEnum oldAbyssRank = rank.getRank();
		rank.addGp(value);
		final AbyssRankEnum newAbyssRank = rank.getRank();
		
		checkRankGpChanged(player, oldAbyssRank, newAbyssRank);
		
		PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK(player.getAbyssRank()));
	}
	
	/**
	 * Checks if a {@link Player} has changed their abyss rank.<br>
	 * This method updates the player's skills and equipment limits if the rank changes.<br>
	 * It also broadcasts an update packet to other players.
	 * @param player The {@link Player} whose rank is being checked.
	 * @param oldAbyssRank The previous {@link AbyssRankEnum} value.
	 * @param newAbyssRank The new {@link AbyssRankEnum} value.
	 */
	public static void checkRankChanged(Player player, AbyssRankEnum oldAbyssRank, AbyssRankEnum newAbyssRank)
	{
		if (oldAbyssRank == newAbyssRank)
		{
			return;
		}
		
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ABYSS_RANK_UPDATE(0, player));
		
		player.getEquipment().checkRankLimitItems();
		AbyssSkillService.updateSkills(player);
	}
	
	/**
	 * Checks if a player's glory rank has changed.<br>
	 * Updates the player's rank and skills if the new rank is valid.<br>
	 * This method handles packet broadcasting and equipment checks.
	 * @param player The {@code Player} object to update.
	 * @param oldGloryRank The previous {@code AbyssRankEnum} value.
	 * @param newGloryRank The new {@code AbyssRankEnum} value.
	 */
	public static void checkRankGpChanged(Player player, AbyssRankEnum oldGloryRank, AbyssRankEnum newGloryRank)
	{
		if (oldGloryRank == newGloryRank)
		{
			return;
		}
		
		final Player onlinePlayer = World.getInstance().findPlayer(player.getName());
		
		if (onlinePlayer != null)
		{
			final AbyssRank abyssRank = onlinePlayer.getAbyssRank();
			
			// Don't update Rank if Governor, it will become auto governor
			if (abyssRank.getGp() >= AbyssRankEnum.SUPREME_COMMANDER.getRequiredGp())
			{
				log.error("Player " + player.getName() + " GP more than GP in abyssRankGP =  " + abyssRank.getGp());
			}
			else
			{
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ABYSS_RANK_UPDATE(0, player));
				
				player.getEquipment().checkRankLimitItems();
				AbyssSkillService.updateSkills(player);
			}
		}
	}
	
	// TODO - Please recheck
	/**
	 * Checks the {@code AbyssRank} of a {@link Player}.<br>
	 * It updates the rank based on current points if they are below a certain threshold.<br>
	 * The method also sends update packets to the player.
	 * @param player The {@link Player} object to check and update.
	 */
	public static void AbyssRankCheck(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		if (player.getAbyssRank().getGp() < AbyssRankEnum.STAR1_OFFICER.getRequiredGp())
		{
			if (player.getAbyssRank().getAp() < 1200)
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE9_SOLDIER);
			}
			else if ((player.getAbyssRank().getAp() >= 1200) && (player.getAbyssRank().getAp() < 4220))
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE8_SOLDIER);
			}
			else if ((player.getAbyssRank().getAp() >= 4220) && (player.getAbyssRank().getAp() < 10990))
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE7_SOLDIER);
			}
			else if ((player.getAbyssRank().getAp() >= 10990) && (player.getAbyssRank().getAp() < 23500))
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE6_SOLDIER);
			}
			else if ((player.getAbyssRank().getAp() >= 23500) && (player.getAbyssRank().getAp() < 42780))
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE5_SOLDIER);
			}
			else if ((player.getAbyssRank().getAp() >= 42780) && (player.getAbyssRank().getAp() < 69700))
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE4_SOLDIER);
			}
			else if ((player.getAbyssRank().getAp() >= 69700) && (player.getAbyssRank().getAp() < 105600))
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE3_SOLDIER);
			}
			else if ((player.getAbyssRank().getAp() >= 105600) && (player.getAbyssRank().getAp() < 150800))
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE2_SOLDIER);
			}
			else if (player.getAbyssRank().getAp() >= 150800)
			{
				player.getAbyssRank().setRank(AbyssRankEnum.GRADE1_SOLDIER);
			}
			
			PacketSendUtility.broadcastPacket(player, new SM_ABYSS_RANK_UPDATE(0, player));
			PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK_UPDATE(0, player));
			PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK(player.getAbyssRank()));
		}
	}
}
