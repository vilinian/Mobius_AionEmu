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
package com.aionemu.gameserver.model.gameobjects.player.motion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.MotionDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages a collection of motion data for players.<br>
 * It provides a way to store and retrieve {@link com.aionemu.gameserver.dao.MotionDAO} entries efficiently.<br>
 * Use this class to handle player movement animations within the game world.
 * @author MrPoke
 */
public class MotionList
{
	private final Player owner;
	private Map<Integer, Motion> activeMotions;
	private Map<Integer, Motion> motions;
	
	/**
	 * Creates a new {@code MotionList} for a specific player.<br>
	 * This object manages the motions associated with a {@link Player}.
	 * @param owner The {@code Player} who owns this motion list.
	 */
	public MotionList(Player owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Retrieves the current list of active motions for the player.<br>
	 * This method returns an empty {@code Map} if no motions are currently active.
	 * @return a {@code Map} where the key is the motion ID and the value is the {@link Motion} object.
	 */
	public Map<Integer, Motion> getActiveMotions()
	{
		if (activeMotions == null)
		{
			return Collections.emptyMap();
		}
		
		return activeMotions;
	}
	
	/**
	 * Retrieves the collection of all registered motions.<br>
	 * This method returns an empty {@code Map} if no motions exist.
	 * @return A {@code Map} where the key is the motion ID and the value is the {@link Motion} object.
	 */
	public Map<Integer, Motion> getMotions()
	{
		if (motions == null)
		{
			return Collections.emptyMap();
		}
		
		return motions;
	}
	
	/**
	 * Adds a new {@link Motion} to the list.<br>
	 * This method updates the active status of existing motions.<br>
	 * It also handles database storage if requested.
	 * @param motion The {@code Motion} object to add.
	 * @param persist Set to {@code true} to save the motion to the database and start a timer.
	 */
	public void add(Motion motion, boolean persist)
	{
		if (motions == null)
		{
			motions = new HashMap<>();
		}
		
		if (motions.containsKey(motion.getId()) && (motion.getExpireTime() == 0))
		{
			remove(motion.getId());
		}
		
		motions.put(motion.getId(), motion);
		if (motion.isActive())
		{
			if (activeMotions == null)
			{
				activeMotions = new HashMap<>();
			}
			
			final Motion old = activeMotions.put(Motion.motionType.get(motion.getId()), motion);
			if (old != null)
			{
				old.setActive(false);
				DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), old);
			}
		}
		
		if (persist)
		{
			if (motion.getExpireTime() != 0)
			{
				ExpireTimerTask.getInstance().addTask(motion, owner);
			}
			
			DAOManager.getDAO(MotionDAO.class).storeMotion(owner.getObjectId(), motion);
		}
	}
	
	/**
	 * Removes a motion from the list based on its unique ID.<br>
	 * This method updates the database and sends a network packet to the owner.<br>
	 * It also removes the motion from active motions if it is currently running.
	 * @param motionId The unique identifier of the motion to remove.
	 * @return {@code true} if the motion was successfully found and removed, otherwise {@code false}.
	 */
	public boolean remove(int motionId)
	{
		final Motion motion = motions.remove(motionId);
		if (motion != null)
		{
			PacketSendUtility.sendPacket(owner, new SM_MOTION((short) motionId));
			DAOManager.getDAO(MotionDAO.class).deleteMotion(owner.getObjectId(), motionId);
			if (motion.isActive())
			{
				activeMotions.remove(Motion.motionType.get(motionId));
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sets a specific motion as the active one for the player.<br>
	 * This method updates the status of both old and new motions in the database.<br>
	 * It also sends the updated motion data to the player and other nearby clients.
	 * @param motionId The unique identifier for the motion to activate.
	 * @param motionType The type category assigned to the motion.
	 */
	public void setActive(int motionId, int motionType)
	{
		if (motionId != 0)
		{
			final Motion motion = motions.get(motionId);
			if ((motion == null) || motion.isActive())
			{
				return;
			}
			
			if (activeMotions == null)
			{
				activeMotions = new HashMap<>();
			}
			
			final Motion old = activeMotions.put(motionType, motion);
			if (old != null)
			{
				old.setActive(false);
				DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), old);
			}
			
			motion.setActive(true);
			DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), motion);
		}
		else if (activeMotions != null)
		{
			final Motion old = activeMotions.remove(motionType);
			if (old == null)
			{
				return; // TODO packet hack??
			}
			
			old.setActive(false);
			DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), old);
		}
		
		PacketSendUtility.sendPacket(owner, new SM_MOTION((short) motionId, (byte) motionType));
		PacketSendUtility.broadcastPacket(owner, new SM_MOTION(owner.getObjectId(), activeMotions), true);
	}
}
