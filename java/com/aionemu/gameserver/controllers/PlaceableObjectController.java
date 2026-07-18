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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.housing.PlaceableHouseObject;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_HOUSE_OBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_OBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This controller manages the logic for objects that can be placed within a house.<br>
 * It handles interactions and updates for {@link HouseObject} instances.<br>
 * It extends {@link VisibleObjectController} to provide specialized behavior for placeable items.
 * @author Rolandas
 * @param <T>
 */
public class PlaceableObjectController<T extends PlaceableHouseObject>extends VisibleObjectController<HouseObject<T>>
{
	Map<Integer, ActionObserver> observed = new ConcurrentHashMap<>();
	
	/**
	 * This method registers a {@link Player} as an observer.<br>
	 * It creates a new {@code FlyRingObserver} for the target.<br>
	 * The observer is added to the player's observation controller.
	 * @param object The {@code VisibleObject} representing the player to observe.
	 */
	@Override
	public void see(VisibleObject object)
	{
		final Player p = (Player) object;
		final ActionObserver observer = new ActionObserver(ObserverType.MOVE);
		p.getObserveController().addObserver(observer);
		observed.put(p.getObjectId(), observer);
		PacketSendUtility.sendPacket(p, new SM_HOUSE_OBJECT(getOwner()));
	}
	
	/**
	 * Updates the visibility status of a specific object.<br>
	 * This method also clears the target if the object is the current target.
	 * @param object The {@code VisibleObject} that is no longer seen.
	 * @param isOutOfRange Whether the object is outside of the visible range.
	 */
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
		final Player p = (Player) object;
		final ActionObserver observer = observed.remove(p.getObjectId());
		if (isOutOfRange)
		{
			observer.moved();
			PacketSendUtility.sendPacket(p, new SM_DELETE_HOUSE_OBJECT(getOwner().getObjectId()));
		}
		
		p.getObserveController().removeObserver(observer);
	}
	
	/**
	 * Handles the logic when an object is despawned.<br>
	 * This method calls {@code onDespawn} on the owner of this object.
	 */
	@Override
	public void onDespawn()
	{
		getOwner().onDespawn();
	}
	
	/**
	 * Removes the object from the game world.<br>
	 * This method calls {@code boolean)} on the owner if it is currently spawned.<br>
	 * It then removes the owner from the {@link World} instance.
	 */
	@Override
	public void delete()
	{
		if (getOwner().isSpawned())
		{
			World.getInstance().despawn(getOwner(), false);
		}
		
		World.getInstance().removeObject(getOwner());
	}
	
	/**
	 * Handles the request from a {@link Player} to start a dialog.<br>
	 * This method is triggered when a player interacts with an NPC.
	 * @param player The {@code Player} object who initiated the request.
	 */
	public void onDialogRequest(Player player)
	{
		if (!MathUtil.isInRange(getOwner(), player, getOwner().getObjectTemplate().getTalkingDistance() + 2))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_TOO_FAR_TO_USE);
			return;
		}
		
		getOwner().onDialogRequest(player);
	}
}
