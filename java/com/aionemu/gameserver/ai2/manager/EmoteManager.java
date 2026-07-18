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

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the processing and broadcasting of in-game emotes for players and NPCs.<br>
 * It handles {@link EmotionType} requests and sends the corresponding {@code SM_EMOTION} packets to nearby creatures.
 * @author ATracer
 */
public class EmoteManager
{
	/**
	 * Triggers the attack animation for a specific NPC.<br>
	 * This method updates the {@code CreatureState} and sends the required packets to clients.
	 * @param owner The {@link Npc} that will perform the attack.
	 */
	public static void emoteStartAttacking(Npc owner)
	{
		final Creature target = (Creature) owner.getTarget();
		owner.unsetState(CreatureState.WALKING);
		if (!owner.isInState(CreatureState.WEAPON_EQUIPPED))
		{
			owner.setState(CreatureState.WEAPON_EQUIPPED);
			PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, target.getObjectId()));
			PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.ATTACKMODE, 0, target.getObjectId()));
		}
	}
	
	/**
	 * Stops the {@link Npc} from its current attacking state.<br>
	 * It removes the {@code WEAPON_EQUIPPED} state from the owner.<br>
	 * If the target is a {@link Player}, it sends a system message to that player.
	 * @param owner The {@link Npc} that should stop attacking.
	 */
	public static void emoteStopAttacking(Npc owner)
	{
		owner.unsetState(CreatureState.WEAPON_EQUIPPED);
		if ((owner.getTarget() != null) && (owner.getTarget() instanceof Player))
		{
			PacketSendUtility.sendPacket((Player) owner.getTarget(), SM_SYSTEM_MESSAGE.STR_UI_COMBAT_NPC_RETURN(owner.getObjectTemplate().getNameId()));
		}
	}
	
	/**
	 * Makes an {@link Npc} start a following animation.<br>
	 * This method removes the {@code WALKING} state from the owner.<br>
	 * It broadcasts the {@code START_EMOTE2} and {@code NEUTRALMODE} emotions.
	 * @param owner The {@link Npc} that will perform the action.
	 */
	public static void emoteStartFollowing(Npc owner)
	{
		owner.unsetState(CreatureState.WALKING);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.NEUTRALMODE, 0, 0));
	}
	
	/**
	 * Starts the walking animation for a specific {@link Npc}.<br>
	 * This method updates the creature state to {@code WALKING}.<br>
	 * It also broadcasts the walk emotion packet to all players.
	 * @param owner The {@link Npc} that will perform the action.
	 */
	public static void emoteStartWalking(Npc owner)
	{
		owner.setState(CreatureState.WALKING);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.WALK));
	}
	
	/**
	 * Stops the walking animation for a specific NPC.<br>
	 * This method removes the {@code CreatureState.WALKING} state from the owner.
	 * @param owner The {@link Npc} that will stop walking.
	 */
	public static void emoteStopWalking(Npc owner)
	{
		owner.unsetState(CreatureState.WALKING);
	}
	
	/**
	 * Starts the returning emote for a specific NPC.<br>
	 * This method broadcasts the {@code START_EMOTE2} emotion and sets the mode to {@code NEUTRALMODE}.
	 * @param owner The {@link Npc} that will perform the action.
	 */
	public static void emoteStartReturning(Npc owner)
	{
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.NEUTRALMODE, 0, 0));
	}
	
	/**
	 * Starts the idling animation for a specific NPC.<br>
	 * This method sets the {@code CreatureState} to {@code WALKING}.<br>
	 * It also broadcasts the required emotion packets to all clients.
	 * @param owner The {@link Npc} that will perform the idle emote.
	 */
	public static void emoteStartIdling(Npc owner)
	{
		owner.setState(CreatureState.WALKING);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.NEUTRALMODE, 0, 0));
	}
	
	/**
	 * Starts the first dancing animation for an {@link Npc}.<br>
	 * This method changes the state of the owner to {@code ACTIVE}.<br>
	 * It broadcasts a dance emotion packet to all players.
	 * @param owner The {@link Npc} that will perform the dance.
	 */
	public static void emoteStartDancing1(Npc owner)
	{
		owner.unsetState(CreatureState.NPC_IDLE);
		owner.setState(CreatureState.ACTIVE);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 134, 0));
	}
	
	/**
	 * Starts the second dancing animation for an {@link Npc}.<br>
	 * This method changes the state to {@code ACTIVE}.<br>
	 * It broadcasts a specific {@link SM_EMOTION} packet.
	 * @param owner The {@link Npc} that will perform the dance.
	 */
	public static void emoteStartDancing2(Npc owner)
	{
		owner.unsetState(CreatureState.NPC_IDLE);
		owner.setState(CreatureState.ACTIVE);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 133, 0));
	}
	
	/**
	 * Starts the third dancing animation for an {@link Npc}.<br>
	 * This method changes the state to {@code ACTIVE}.<br>
	 * It broadcasts a specific {@link SM_EMOTION} packet.
	 * @param owner The {@link Npc} that will perform the dance.
	 */
	public static void emoteStartDancing3(Npc owner)
	{
		owner.unsetState(CreatureState.NPC_IDLE);
		owner.setState(CreatureState.ACTIVE);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 142, 0));
	}
	
	/**
	 * Starts the fourth dancing animation for an {@link Npc}.<br>
	 * This method changes the state to {@code ACTIVE}.<br>
	 * It broadcasts a specific {@link SM_EMOTION} packet.
	 * @param owner The {@link Npc} that will perform the dance.
	 */
	public static void emoteStartDancing4(Npc owner)
	{
		owner.unsetState(CreatureState.NPC_IDLE);
		owner.setState(CreatureState.ACTIVE);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 19, 0));
	}
	
	/**
	 * Starts the singing emote for a specific NPC.<br>
	 * This method changes the {@code CreatureState} of the owner to {@code ACTIVE}.<br>
	 * It also broadcasts an {@link SM_EMOTION} packet with emotion type {@code EMOTE}.
	 * @param owner The {@link Npc} that will perform the singing action.
	 */
	public static void emoteStartSinging(Npc owner)
	{
		owner.unsetState(CreatureState.NPC_IDLE);
		owner.setState(CreatureState.ACTIVE);
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 113, 0));
	}
}
