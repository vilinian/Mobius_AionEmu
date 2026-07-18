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
package com.aionemu.gameserver.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL_REQUEST_CANCEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Manages the logic for player-to-player duels within the game world.<br>
 * This service handles duel requests, matchmaking, and processing results via {@code handleDuelRequest}. It ensures that all duel interactions are synchronized and correctly updated in the {@link World}.
 * @author Simple, Sphinx, xTz
 * @reworked Kill3r
 */
public class DuelService
{
	private static Logger log = LoggerFactory.getLogger(DuelService.class);
	private final Map<Integer, Integer> duels;
	private final Map<Integer, Future<?>> drawTasks;
	
	/**
	 * Retrieves the singleton instance of the {@link DuelService}.<br>
	 * This method provides a global access point to the duel management system.
	 * @return The active {@code DuelService} instance.
	 */
	public static DuelService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link DuelService} class.<br>
	 * This constructor initializes the internal data structures.<br>
	 * It prevents other classes from creating new instances of this service.
	 */
	private DuelService()
	{
		duels = new ConcurrentHashMap<>();
		drawTasks = new ConcurrentHashMap<>();
		log.info("DuelService started.");
	}
	
	/**
	 * Handles a new duel request between two players.<br>
	 * This method validates if the players are eligible to duel based on their location and race.<br>
	 * If valid, it sends a confirmation window to the responder.
	 * @param requester The player who is initiating the duel.
	 * @param responder The player who is receiving the duel request.
	 */
	public void onDuelRequest(Player requester, Player responder)
	{
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if (requester.isInsideZoneType(ZoneType.PVP) || responder.isInsideZoneType(ZoneType.PVP))
		{
			PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_INVALID(responder.getName()));
			return;
		}
		
		if (requester.isEnemy(responder) || isDueling(requester.getObjectId()) || isDueling(responder.getObjectId()))
		{
			PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_HE_REJECT_DUEL(responder.getName()));
			return;
		}
		
		for (ZoneInstance zone : responder.getPosition().getMapRegion().getZones(responder))
		{
			if ((!zone.isOtherRaceDuelsAllowed() && !responder.getRace().equals(requester.getRace())) || (!zone.isSameRaceDuelsAllowed() && responder.getRace().equals(requester.getRace())))
			{
				PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_MSG_DUEL_CANT_IN_THIS_ZONE);
				return;
			}
		}
		
		final RequestResponseHandler rrh = new RequestResponseHandler(requester)
		{
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				rejectDuelRequest((Player) requester, responder);
			}
			
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				if (!isDueling(requester.getObjectId()))
				{
					startDuel((Player) requester, responder);
				}
			}
		};
		responder.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_REQUEST, rrh);
		PacketSendUtility.sendPacket(responder, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_REQUEST, 0, 0, requester.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DUEL_REQUESTED(requester.getName()));
	}
	
	/**
	 * Confirms a duel request between two players.<br>
	 * This method checks if the players are on the same race.<br>
	 * It then sends a confirmation window to the {@code requester}.
	 * @param requester The player who initiated the duel request.
	 * @param responder The player who is receiving the duel request.
	 */
	public void confirmDuelWith(Player requester, Player responder)
	{
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if (requester.isEnemy(responder))
		{
			return;
		}
		
		final RequestResponseHandler rrh = new RequestResponseHandler(responder)
		{
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				log.debug("[Duel] Player " + responder.getName() + " confirmed his duel with " + requester.getName());
			}
			
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				cancelDuelRequest(responder, (Player) requester);
			}
		};
		requester.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_WITHDRAW_REQUEST, rrh);
		PacketSendUtility.sendPacket(requester, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_WITHDRAW_REQUEST, 0, 0, responder.getName()));
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_REQUEST_TO_PARTNER(responder.getName()));
	}
	
	/**
	 * Handles the logic when a player declines a duel request.<br>
	 * It sends a cancellation packet to the {@code requester}.<br>
	 * It also sends a system message to the {@code responder}.
	 * @param requester The player who sent the initial duel request.
	 * @param responder The player who rejected the duel request.
	 */
	private void rejectDuelRequest(Player requester, Player responder)
	{
		log.debug("[Duel] Player " + responder.getName() + " rejected duel request from " + requester.getName());
		PacketSendUtility.sendPacket(requester, new SM_DUEL_REQUEST_CANCEL(1300097, responder.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DUEL_REJECT_DUEL(requester.getName()));
	}
	
	/**
	 * Cancels a pending duel request sent by the {@code owner}.<br>
	 * It notifies the {@code target} that the request was cancelled.<br>
	 * It also sends a system message to the {@code owner}.
	 * @param owner The player who initiated the duel request.
	 * @param target The player who received the duel request.
	 */
	private void cancelDuelRequest(Player owner, Player target)
	{
		log.debug("[Duel] Player " + owner.getName() + " cancelled his duel request with " + target.getName());
		PacketSendUtility.sendPacket(target, new SM_DUEL_REQUEST_CANCEL(1300134, owner.getName()));
		PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_DUEL_WITHDRAW_REQUEST(target.getName()));
	}
	
	/**
	 * Initializes the duel between two players.<br>
	 * This method sends start packets to both {@code Player} objects.<br>
	 * It calls {@code int)} and {@code Player)}.<br>
	 * A system message is broadcasted to all players after a 3000 millisecond delay.
	 * @param requester The player who initiated the duel request.
	 * @param responder The player who accepted the duel request.
	 */
	private void startDuel(Player requester, Player responder)
	{
		PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_STARTED(responder.getObjectId()));
		PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_STARTED(requester.getObjectId()));
		createDuel(requester.getObjectId(), responder.getObjectId());
		createTask(requester, responder);
		ThreadPoolManager.getInstance().schedule(() -> World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DUEL_START_BROADCAST(requester.getName(), responder.getName()))), 3000); // 3 Sec delay
	}
	
	/**
	 * Handles the logic for a player losing a duel.<br>
	 * This method clears aggro lists and cancels active skills for both participants.<br>
	 * It also updates summon behaviors and sends the final result packets to all players.
	 * @param player The {@link Player} who has lost the duel.
	 */
	public void loseDuel(Player player)
	{
		if (!isDueling(player.getObjectId()))
		{
			return;
		}
		
		final int opponnentId = duels.get(player.getObjectId());
		
		player.getAggroList().clear();
		
		final Player opponent = World.getInstance().findPlayer(opponnentId);
		
		if (opponent != null)
		{
			/**
			 * all debuffs are removed from winner, but buffs will remain Stop casting or skill use
			 */
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();
			opponent.getAggroList().clear();
			
			/**
			 * cancel attacking winner by summon
			 */
			if (player.getSummon() != null)
			{
				// if (player.getSummon().getTarget().isTargeting(opponnentId))
				SummonsService.doMode(SummonMode.GUARD, player.getSummon(), UnsummonType.UNSPECIFIED);
			}
			
			/**
			 * cancel attacking loser by summon
			 */
			if (opponent.getSummon() != null)
			{
				// if (opponent.getSummon().getTarget().isTargeting(player.getObjectId()))
				SummonsService.doMode(SummonMode.GUARD, opponent.getSummon(), UnsummonType.UNSPECIFIED);
			}
			
			/**
			 * cancel attacking winner by summoned object
			 */
			if (player.getSummonedObj() != null)
			{
				player.getSummonedObj().getController().cancelCurrentSkill();
			}
			
			/**
			 * cancel attacking loser by summoned object
			 */
			if (opponent.getSummonedObj() != null)
			{
				opponent.getSummonedObj().getController().cancelCurrentSkill();
			}
			
			PacketSendUtility.sendPacket(opponent, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON, player.getName()));
			PacketSendUtility.sendPacket(player, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST, opponent.getName()));
			World.getInstance().doOnAllPlayers(players -> PacketSendUtility.sendPacket(players, SM_SYSTEM_MESSAGE.STR_DUEL_STOP_BROADCAST(opponent.getName(), player.getName())));
		}
		else
		{
			log.warn("CHECKPOINT : duel opponent is already out of world");
		}
		
		removeDuel(player.getObjectId(), opponnentId);
	}
	
	/**
	 * Handles the logic for when a player loses an arena duel.<br>
	 * It removes debuffs and cancels skills for both participants.<br>
	 * The method then cleans up the active duel state.
	 * @param player The {@link Player} who lost the duel.
	 */
	public void loseArenaDuel(Player player)
	{
		if (!isDueling(player.getObjectId()))
		{
			return;
		}
		
		/**
		 * all debuffs are removed from loser Stop casting or skill use
		 */
		player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
		player.getController().cancelCurrentSkill();
		
		final int opponnentId = duels.get(player.getObjectId());
		final Player opponent = World.getInstance().findPlayer(opponnentId);
		
		if (opponent != null)
		{
			/**
			 * all debuffs are removed from winner, but buffs will remain Stop casting or skill use
			 */
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();
		}
		else
		{
			log.warn("CHECKPOINT : duel opponent is already out of world");
		}
		
		removeDuel(player.getObjectId(), opponnentId);
	}
	
	/**
	 * Schedules a task to handle a draw result if the duel is not finished.<br>
	 * This task runs after 5 minutes of active combat.<br>
	 * It sends packets to both players and broadcasts a timeout message.
	 * @param requester The player who initiated the duel.
	 * @param responder The player who accepted the duel.
	 */
	private void createTask(Player requester, Player responder)
	{
		// Schedule for draw
		final Future<?> task = ThreadPoolManager.getInstance().schedule(() ->
		{
			if (isDueling(requester.getObjectId(), responder.getObjectId()))
			{
				PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_DRAW, requester.getName()));
				PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_DRAW, responder.getName()));
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DUEL_TIMEOUT_BROADCAST(requester.getName(), responder.getName())));
				removeDuel(requester.getObjectId(), responder.getObjectId());
			}
		}, 5 * 60 * 1000); // 5 minutes battle retail like
		
		drawTasks.put(requester.getObjectId(), task);
		drawTasks.put(responder.getObjectId(), task);
	}
	
	/**
	 * Checks if a specific player is currently participating in a duel.<br>
	 * It verifies if the {@code playerObjId} exists within the active duels map.
	 * @param playerObjId The unique identifier of the player to check.
	 * @return {@code true} if the player is dueling, otherwise {@code false}.
	 */
	public boolean isDueling(int playerObjId)
	{
		return (duels.containsKey(playerObjId) && duels.containsValue(playerObjId));
	}
	
	/**
	 * Checks if a specific player is currently in a duel with another target.<br>
	 * This method verifies the relationship between two objects in the {@code duels} map.
	 * @param playerObjId The unique identifier of the first player.
	 * @param targetObjId The unique identifier of the target player.
	 * @return {@code true} if the players are currently dueling, otherwise {@code false}.
	 */
	public boolean isDueling(int playerObjId, int targetObjId)
	{
		return duels.containsKey(playerObjId) && (duels.get(playerObjId) == targetObjId);
	}
	
	/**
	 * Initializes a new duel between two players.<br>
	 * This method maps both IDs in the {@code duels} collection.<br>
	 * It establishes a mutual connection between the participants.
	 * @param requesterObjId The unique ID of the player who started the duel.
	 * @param responderObjId The unique ID of the player who accepted the duel.
	 */
	public void createDuel(int requesterObjId, int responderObjId)
	{
		duels.put(requesterObjId, responderObjId);
		duels.put(responderObjId, requesterObjId);
	}
	
	/**
	 * Removes a duel between two players from the system.<br>
	 * This method cleans up both active duels and associated tasks.
	 * @param requesterObjId The unique ID of the player who requested the duel.
	 * @param responderObjId The unique ID of the player who responded to the duel.
	 */
	private void removeDuel(int requesterObjId, int responderObjId)
	{
		duels.remove(requesterObjId);
		duels.remove(responderObjId);
		removeTask(requesterObjId);
		removeTask(responderObjId);
	}
	
	/**
	 * Cancels and removes a pending draw task for a specific player.<br>
	 * This method checks if the {@code Future<?>} exists in the {@code drawTasks} map.<br>
	 * If the task is still running, it cancels the execution immediately.
	 * @param playerId The unique identifier of the player whose task should be removed.
	 */
	private void removeTask(int playerId)
	{
		final Future<?> task = drawTasks.get(playerId);
		if ((task != null) && !task.isDone())
		{
			task.cancel(true);
			drawTasks.remove(playerId);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final DuelService instance = new DuelService();
	}
}
