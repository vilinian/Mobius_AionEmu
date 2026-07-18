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
package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.templates.revive_start_points.InstanceReviveStartPoints;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Handles the logic for reviving a {@link Player} who is currently in a dead state.<br>
 * It manages requirements such as proximity to a {@link Kisk}, item usage, and animation triggers.
 * @author Jego, xTz
 */
public class PlayerReviveService
{
	/**
	 * Revives a player specifically from a duel state.<br>
	 * This method sets the health and mana to 30 percent.<br>
	 * It also triggers a resurrection animation and protection task.
	 * @param player The {@link Player} object to be revived.
	 */
	public static void duelRevive(Player player)
	{
		revive(player, 30, 30, false, 0);
		player.getController().startProtectionActiveTask();
		player.setPortAnimation(4);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		if (player.getIsFlyingBeforeDeath())
		{
			player.getFlyController().startFly();
		}
		
		player.getGameStats().updateStatsAndSpeedVisually();
		player.unsetResPosState();
	}
	
	/**
	 * Revives a {@link Player} using their specific resurrection skill.<br>
	 * This method checks if the player is in a resurrection state before proceeding.<br>
	 * It updates the player's health, mana, and visual status.<br>
	 * The method also handles special cases like flying states and prison locations.
	 * @param player The {@link Player} object to be revived.
	 */
	public static void skillRevive(Player player)
	{
		if (!(player.getResStatus()))
		{
			cancelRes(player);
			return;
		}
		
		revive(player, 10, 10, true, player.getResurrectionSkill());
		player.getController().startProtectionActiveTask();
		player.setPortAnimation(4);
		if (player.getIsFlyingBeforeDeath())
		{
			player.setState(CreatureState.FLYING);
		}
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		
		// if player was flying before res, start flying
		if (player.getIsFlyingBeforeDeath())
		{
			player.getFlyController().startFly();
		}
		
		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison())
		{
			TeleportService2.teleportToPrison(player);
		}
		
		if (player.isInResPostState())
		{
			TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceId(), player.getResPosX(), player.getResPosY(), player.getResPosZ());
		}
		
		player.unsetResPosState();
		
		// unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
	}
	
	/**
	 * Revives a {@link Player} using the rebirth mechanic.<br>
	 * This method checks if the player is eligible for resurrection.<br>
	 * It handles soul sickness and sets the correct health and mana percentages.<br>
	 * The player will receive a resurrection animation and system message.
	 * @param player The {@link Player} object to be revived.
	 */
	public static void rebirthRevive(Player player)
	{
		if (!player.canUseRebirthRevive())
		{
			return;
		}
		
		if (player.getRebirthResurrectPercent() <= 0)
		{
			PacketSendUtility.sendMessage(player, "Error: Rebirth effect missing percent.");
			player.setRebirthResurrectPercent(5);
		}
		
		boolean soulSickness = true;
		int rebirthResurrectPercent = player.getRebirthResurrectPercent();
		if (player.getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES)
		{
			rebirthResurrectPercent = 100;
			soulSickness = false;
		}
		
		player.getController().startProtectionActiveTask();
		player.setPortAnimation(4);
		revive(player, rebirthResurrectPercent, rebirthResurrectPercent, soulSickness, player.getRebirthSkill());
		if (player.getIsFlyingBeforeDeath())
		{
			player.setState(CreatureState.FLYING);
		}
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		if (player.getIsFlyingBeforeDeath())
		{
			player.getFlyController().startFly();
		}
		
		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison())
		{
			TeleportService2.teleportToPrison(player);
		}
		
		player.unsetResPosState();
		
		// if player was flying before res, start flying unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
	}
	
	/**
	 * Resurrects the specified {@link Player} using a bind revive. <br>
	 * This method acts as a shortcut for calling {@code bindRevive(Player player, int skillId)} with a default value of {@code 0}.
	 * @param player The {@code Player} object to be revived.
	 */
	public static void bindRevive(Player player)
	{
		bindRevive(player, 0);
	}
	
	/**
	 * Revives the player using a specific skill and binds them to their location.<br>
	 * This method sets health and mana to 25 percent.<br>
	 * It also handles teleportation logic based on the player's current status.
	 * @param player The {@link Player} object to be revived.
	 * @param skillId The unique identifier for the resurrection skill.
	 */
	public static void bindRevive(Player player, int skillId)
	{
		revive(player, 25, 25, true, skillId);
		player.getController().startProtectionActiveTask();
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		if (player.getIsFlyingBeforeDeath())
		{
			player.getFlyController().startFly();
		}
		
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (player.isInPrison())
		{
			TeleportService2.teleportToPrison(player);
		}
		else
		{
			boolean isInvadeActiveVortex = false;
			for (VortexLocation loc : VortexService.getInstance().getVortexLocations().values())
			{
				isInvadeActiveVortex = loc.isInsideActiveVortex(player) && player.getRace().equals(loc.getInvadersRace());
				if (isInvadeActiveVortex)
				{
					TeleportService2.teleportTo(player, loc.getResurrectionPoint());
				}
			}
			
			if (!isInvadeActiveVortex)
			{
				TeleportService2.moveToBindLocation(player, true);
			}
		}
		
		player.unsetResPosState();
	}
	
	/**
	 * Revives the specified {@link Player} using a kisk.<br>
	 * This method calls {@code kiskRevive(Player player, int skillId)} with a default ID of {@code 0}.
	 * @param player The {@link Player} object to be revived.
	 */
	public static void kiskRevive(Player player)
	{
		kiskRevive(player, 0);
	}
	
	/**
	 * Revives a {@link Player} using a Kisk object.<br>
	 * If no Kisk is found, it calls {@code bindRevive} instead.<br>
	 * This method handles teleportation and status updates during the revival process.
	 * @param player The {@link Player} to be revived.
	 * @param skillId The ID of the resurrection skill used for the revive.
	 */
	public static void kiskRevive(Player player, int skillId)
	{
		final Kisk kisk = player.getKisk();
		if (kisk == null)
		{
			bindRevive(player);
			return;
		}
		
		if (player.isInPrison())
		{
			TeleportService2.teleportToPrison(player);
		}
		else if (kisk.isActive())
		{
			final WorldPosition bind = kisk.getPosition();
			kisk.resurrectionUsed();
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
			revive(player, 25, 25, false, skillId);
			player.getController().startProtectionActiveTask();
			player.setPortAnimation(4);
			if (player.getIsFlyingBeforeDeath())
			{
				player.getFlyController().startFly();
			}
			
			player.getGameStats().updateStatsAndSpeedVisually();
			player.unsetResPosState();
			TeleportService2.moveToKiskLocation(player, bind);
		}
	}
	
	/**
	 * Revives the specified {@link Player} using the instance revive logic.<br>
	 * This method calls {@code instanceRevive(Player player, int skillId)} with a default value of {@code 0}.
	 * @param player The {@link Player} object to be revived.
	 */
	public static void instanceRevive(Player player)
	{
		instanceRevive(player, 0);
	}
	
	/**
	 * Revives a player within an instance using a specific skill.<br>
	 * This method handles the resurrection logic, protection tasks, and teleportation to start points.<br>
	 * If the map is not an instance type, it falls back to {@code bindRevive}.
	 * @param player The {@code Player} object to be revived.
	 * @param skillId The unique identifier for the resurrection skill used.
	 */
	public static void instanceRevive(Player player, int skillId)
	{
		// Revive in Instances
		if (player.getPosition().getWorldMapInstance().getInstanceHandler().onReviveEvent(player))
		{
			return;
		}
		
		final WorldMap map = World.getInstance().getWorldMap(player.getWorldId());
		if (map == null)
		{
			bindRevive(player);
			return;
		}
		
		revive(player, 25, 25, true, skillId);
		player.getController().startProtectionActiveTask();
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		final InstanceReviveStartPoints revivePoint = TeleportService2.getReviveInstanceStartPoints(map.getMapId());
		if (map.isInstanceType() && (revivePoint != null))
		{
			TeleportService2.teleportTo(player, revivePoint.getReviveWorld(), revivePoint.getX(), revivePoint.getY(), revivePoint.getZ(), revivePoint.getH());
		}
		else
		{
			bindRevive(player);
		}
		
		player.unsetResPosState();
	}
	
	/**
	 * Revives a {@link Player} with specific stats and conditions.<br>
	 * This method resets the player's health, mana, and aggro status.<br>
	 * It also handles soul sickness and protection tasks.
	 * @param player The {@link Player} object to revive.
	 * @param hpPercent The target health percentage for the player.
	 * @param mpPercent The target mana percentage for the player.
	 * @param setSoulsickness Whether to apply a soul sickness penalty.
	 * @param resurrectionSkill The skill ID used for the resurrection process.
	 */
	public static void revive(Player player, int hpPercent, int mpPercent, boolean setSoulsickness, int resurrectionSkill)
	{
		player.getKnownList().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player visitor)
			{
				final VisibleObject target = visitor.getTarget();
				if ((target != null) && (target.getObjectId() == player.getObjectId()) && (visitor.getRace() != player.getRace()))
				{
					visitor.setTarget(null);
					PacketSendUtility.sendPacket(visitor, new SM_TARGET_SELECTED(null));
				}
			}
		});
		final boolean isNoResurrectPenalty = player.getController().isNoResurrectPenaltyInEffect();
		player.getMoveController().stopFalling();
		player.setPlayerResActivate(false);
		player.getLifeStats().setCurrentHpPercent(isNoResurrectPenalty ? 100 : hpPercent);
		player.getLifeStats().setCurrentMpPercent(isNoResurrectPenalty ? 100 : mpPercent);
		if ((player.getCommonData().getDp() > 0) && !isNoResurrectPenalty)
		{
			player.getCommonData().setDp(0);
		}
		
		player.getLifeStats().triggerRestoreOnRevive();
		if (!isNoResurrectPenalty && setSoulsickness)
		{
			player.getController().updateSoulSickness(resurrectionSkill);
		}
		
		if (player.getResurrectionSkill() > 0)
		{
			player.setResurrectionSkill(0);
		}
		
		player.getController().startProtectionActiveTask();
		player.setPortAnimation(4);
		player.getAggroList().clear();
		player.getController().onBeforeSpawn(false);
		if (player.isInGroup2())
		{
			PlayerGroupService.updateGroup(player, GroupEvent.MOVEMENT);
		}
		
		if (player.isInAlliance2())
		{
			PlayerAllianceService.updateAlliance(player, PlayerAllianceEvent.MOVEMENT);
		}
	}
	
	/**
	 * Revives the player using their self-resurrection item.<br>
	 * This method checks for a valid {@code Item} in the player's inventory.<br>
	 * It handles cooldowns, animations, and updates the player state after revival.
	 * @param player The {@link Player} object to be revived.
	 */
	public static void itemSelfRevive(Player player)
	{
		final Item item = player.getSelfRezStone();
		if (item == null)
		{
			if (player.getAccessLevel() == 0)
			{
				cancelRes(player);
			}
			return;
		}
		
		// Add Cooldown and use item
		final ItemUseLimits useLimits = item.getItemTemplate().getUseLimits();
		final int useDelay = useLimits.getDelayTime();
		player.addItemCoolDown(useLimits.getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemTemplate().getTemplateId()), true);
		if (!player.getInventory().decreaseByObjectId(item.getObjectId(), 1))
		{
			cancelRes(player);
			return;
		}
		
		// Tombstone Self-Rez retail verified 15%
		revive(player, 15, 15, true, player.getResurrectionSkill());
		player.getController().startProtectionActiveTask();
		player.setPortAnimation(4);
		if (player.getIsFlyingBeforeDeath())
		{
			player.setState(CreatureState.FLYING);
		}
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		if (player.getIsFlyingBeforeDeath())
		{
			player.getFlyController().startFly();
		}
		
		player.getGameStats().updateStatsAndSpeedVisually();
		if (player.isInPrison())
		{
			TeleportService2.teleportToPrison(player);
		}
		
		player.unsetResPosState();
		player.setIsFlyingBeforeDeath(false);
	}
	
	/**
	 * Resurrects the player at their original starting position.<br>
	 * This method is a convenience wrapper for {@code startPositionRevive(Player, int)}.<br>
	 * It uses a default skill ID of {@code 0}.
	 * @param player The {@link Player} object to be revived.
	 */
	public static void startPositionRevive(Player player)
	{
		startPositionRevive(player, 0);
	}
	
	/**
	 * Revives the {@code Player} and moves them to a starting position.<br>
	 * This method sets health and mana to 25 percent.<br>
	 * It also handles teleportation based on whether the player is in prison.
	 * @param player The {@code Player} object to revive.
	 * @param skillId The ID of the resurrection skill used.
	 */
	public static void startPositionRevive(Player player, int skillId)
	{
		revive(player, 25, 25, true, skillId);
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (player.isInPrison())
		{
			TeleportService2.teleportToPrison(player);
		}
		else
		{
			TeleportService2.teleportWorldStartPoint(player, player.getWorldId());
		}
		
		player.unsetResPosState();
	}
	
	/**
	 * Starts the Luna revive process for a specific player.<br>
	 * This method calls {@code int)} with a skill ID of {@code 0}.
	 * @param player The {@code Player} object to perform the revive on.
	 */
	public static void startLunaRevive(Player player)
	{
		startPositionRevive(player, 0);
	}
	
	/**
	 * Cancels a resurrection attempt for the specified player.<br>
	 * This method logs a potential self-resurrection hack to the {@code AuditLogger}.<br>
	 * It then sends a die packet to the {@code Player} controller.
	 * @param player The {@code Player} object to process.
	 */
	private static void cancelRes(Player player)
	{
		AuditLogger.info(player, "Possible selfres hack.");
		player.getController().sendDie();
	}
}
