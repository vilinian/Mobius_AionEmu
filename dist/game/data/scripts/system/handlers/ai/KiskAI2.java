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
package system.handlers.ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Request;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.KiskService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the artificial intelligence logic for {@link Kisk} NPCs.<br>
 * This class manages behaviors such as responding to player interactions and managing emotions.
 * @author ATracer, Source
 */
@AIName("kisk")
public class KiskAI2 extends NpcAI2
{
	private final int CANCEL_DIALOG_METERS = 5;
	
	/**
	 * Retrieves the {@link Kisk} object that owns this AI instance.<br>
	 * This method casts the owner to a {@code Kisk} type.
	 * @return The {@code Kisk} object associated with this AI.
	 */
	@Override
	public Kisk getOwner()
	{
		return (Kisk) super.getOwner();
	}
	
	/**
	 * Handles the logic when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the owner has full health.<br>
	 * If true, it sends a system message to all members of the owner.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		if (getLifeStats().isFullyRestoredHp())
		{
			for (Player member : getOwner().getCurrentMemberList())
			{
				PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_ATTACKED);
			}
		}
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		if (isAlreadyDead())
		{
			PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.DIE, 0, 0));
			getOwner().broadcastPacket(SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_DESTROYED);
		}
		
		super.handleDied();
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It removes the kisk from the {@link KiskService}.<br>
	 * If the owner is not dead, it sends a system message to the {@link Player} owner.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		KiskService.getInstance().removeKisk(getOwner());
		if (!isAlreadyDead())
		{
			getOwner().broadcastPacket(SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_REMOVED);
		}
		
		super.handleDespawned();
	}
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It handles the logic for registering a {@link Player} to a bindstone.<br>
	 * The method checks if the player is already registered or has permission to join.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		if (player.getKisk() == getOwner())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_BINDSTONE_ALREADY_REGISTERED);
			return;
		}
		
		if (getOwner().canBind(player))
		{
			AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_REGISTER_BINDSTONE, getOwner().getObjectId(), CANCEL_DIALOG_METERS, new AI2Request()
			{
				private boolean decisionTaken = false;
				
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (!decisionTaken)
					{
						// Check again if it's full (If they waited to press OK)
						if (!getOwner().canBind(responder))
						{
							PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
							return;
						}
						
						KiskService.getInstance().onBind(getOwner(), responder);
					}
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					decisionTaken = true;
				}
			});
		}
		else if (getOwner().getCurrentMemberCount() >= getOwner().getMaxMembers())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_FULL);
		}
		else
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
		}
	}
	
	/**
	 * This method handles specific logic for the {@code bomb} NPC.<br>
	 * It checks the type of {@code question} provided by the system.<br>
	 * It returns a predefined {@link AIAnswer} based on the question type.
	 * @param question The {@code AIQuestion} being asked to this instance.
	 * @return The corresponding {@code AIAnswer} or {@code null}.
	 */
	@Override
	protected AIAnswer pollInstance(AIQuestion question)
	{
		switch (question)
		{
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
}
