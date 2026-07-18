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
package com.aionemu.gameserver.model.gameobjects;

import java.util.concurrent.atomic.AtomicReference;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.DialogPage;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HousingPostbox;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_OBJECT_USE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents a postbox object within the game world.<br>
 * This class handles interactions related to {@link HousingPostbox} objects.<br>
 * It allows players to access and manage items stored in their housing mailboxes.
 * @author Rolandas
 */
public class PostboxObject extends HouseObject<HousingPostbox>
{
	private final AtomicReference<Player> usingPlayer = new AtomicReference<>();
	
	/**
	 * Creates a new instance of a {@link PostboxObject}.<br>
	 * This constructor initializes the object with its owner and identification details.
	 * @param owner The {@link House} that owns this postbox.
	 * @param objId The unique identifier for the specific object in the world.
	 * @param templateId The ID of the visual model used for this object.
	 */
	public PostboxObject(House owner, int objId, int templateId)
	{
		super(owner, objId, templateId);
	}
	
	/**
	 * Handles the logic when a {@link Player} interacts with this postbox.<br>
	 * This method is triggered by the use action.
	 * @param player The {@code Player} who used the object.
	 */
	@Override
	public void onUse(Player player)
	{
		if (!usingPlayer.compareAndSet(null, player))
		{
			// The same player is using, return. It might be double-click
			if (usingPlayer.compareAndSet(player, player))
			{
				return;
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_OCCUPIED_BY_OTHER);
			return;
		}
		
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getObserveController().removeObserver(this);
				usingPlayer.set(null);
			}
		};
		
		player.getObserveController().attach(observer);
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_USE(getObjectTemplate().getNameId()));
		player.getController().addTask(TaskId.HOUSE_OBJECT_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), DialogPage.MAIL.id()));
					player.getMailbox().sendMailList(false);
					PacketSendUtility.sendPacket(player, new SM_OBJECT_USE_UPDATE(player.getObjectId(), 0, 0, PostboxObject.this));
				}
				finally
				{
					player.getObserveController().removeObserver(observer);
					usingPlayer.set(null);
				}
			}
		}, 0));
	}
	
	/**
	 * Checks if the postbox is available to expire.<br>
	 * It returns {@code true} if no player is currently using it.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return usingPlayer.get() == null;
	}
}
