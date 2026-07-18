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
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.handler.FollowEventHandler;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence for delivery man NPCs.<br>
 * This class manages their behavior when following a {@link Player}.<br>
 * It ensures they perform specific actions related to their delivery tasks.
 * @author -Nemesiss-
 * @Reworked GiGatR00n (Aion-Core)
 */
@AIName("deliveryman")
public class DeliveryManAI2 extends FollowingNpcAI2
{
	public static int EVENT_SET_CREATOR = 1;
	private static int SERVICE_TIME = 5 * 60 * 1000;
	private static int SPAWN_ACTION_DELAY = 1500;
	private Player owner;
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It schedules a deletion task and a spawn action using {@link ThreadPoolManager}.<br>
	 * It calls {@code handleSpawned} from the parent class.
	 */
	@Override
	protected void handleSpawned()
	{
		ThreadPoolManager.getInstance().schedule(new DeleteDeliveryMan(), SERVICE_TIME);
		ThreadPoolManager.getInstance().schedule(new DeliveryManSpawnAction(), SPAWN_ACTION_DELAY);
		
		super.handleSpawned();
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It broadcasts a system message to the {@link Player} owner.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(true, 390267, getObjectId(), 1, new NpcShout().getParam()));
		
		super.handleDespawned();
	}
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		if (owner != null)
		{
			if (player.equals(owner))
			{
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 18));
				player.getMailbox().sendMailList(true);
			}
		}
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		if (owner != null)
		{
			if (creature == owner)
			{
				FollowEventHandler.creatureMoved(this, creature);
			}
		}
	}
	
	/**
	 * Processes custom events for the NPC.<br>
	 * This method checks the {@code eventId} to determine which action to take.<br>
	 * It handles specific logic like setting the owner of the NPC.
	 * @param eventId The unique identifier for the event type.
	 * @param args A variable list of arguments used by the event.
	 */
	@Override
	protected void handleCustomEvent(int eventId, Object... args)
	{
		if (eventId == EVENT_SET_CREATOR)
		{
			owner = (Player) args[0];
		}
	}
	
	private final class DeleteDeliveryMan implements Runnable
	{
		@Override
		public void run()
		{
			AI2Actions.deleteOwner(DeliveryManAI2.this);
		}
	}
	
	private final class DeliveryManSpawnAction implements Runnable
	{
		@Override
		public void run()
		{
			PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(true, 390266, getObjectId(), 1, new NpcShout().getParam()));
			handleFollowMe(owner);
			handleCreatureMoved(owner);
		}
	}
}
