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

import static com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.STR_ITEM_CANT_USE_UNTIL_DELAY_TIME;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for NPCs that interact with items.<br>
 * This class manages how an {@link NpcAI2} processes item usage actions.
 * @author xTz
 * @modified vlog
 * @Modified Majka Ajural
 */
@AIName("useitem")
public class ActionItemNpcAI2 extends NpcAI2
{
	protected int startBarAnimation = 1;
	protected int cancelBarAnimation = 2;
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		handleUseItemStart(player);
	}
	
	/**
	 * Handles the logic when a player starts using an item on this NPC.<br>
	 * It checks if the owner is busy and sends a system message if necessary.<br>
	 * If there is a delay, it manages animations and schedules the completion task.
	 * @param player The {@code Player} who initiated the action.
	 */
	protected void handleUseItemStart(Player player)
	{
		// If the npc is busy for some quest send a message to player
		if ((getOwner() != null) && getOwner().getIsQuestBusy())
		{
			PacketSendUtility.sendPacket(player, STR_ITEM_CANT_USE_UNTIL_DELAY_TIME);
			handleUseItemFinish(player);
			return;
		}
		
		final int delay = getTalkDelay();
		if (delay > 1)
		{
			final ItemUseObserver observer = new ItemUseObserver()
			{
				@Override
				public void abort()
				{
					player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
					PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, cancelBarAnimation));
					player.getObserveController().removeObserver(this);
				}
			};
			
			player.getObserveController().attach(observer);
			PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), startBarAnimation));
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
			player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(() ->
			{
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), cancelBarAnimation));
				player.getObserveController().removeObserver(observer);
				handleUseItemFinish(player);
			}, delay));
		}
		else
		{
			handleUseItemFinish(player);
		}
	}
	
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	protected void handleUseItemFinish(Player player)
	{
		if (getOwner().isInInstance())
		{
			AI2Actions.handleUseItemFinish(this, player);
		}
	}
	
	/**
	 * Retrieves the delay time for NPC speech.<br>
	 * This method converts the template value into milliseconds.<br>
	 * It calls {@code getObjectTemplate} to fetch the base value.
	 * @return The total delay in milliseconds as an {@code int}.
	 */
	protected int getTalkDelay()
	{
		return getObjectTemplate().getTalkDelay() * 1000;
	}
}
