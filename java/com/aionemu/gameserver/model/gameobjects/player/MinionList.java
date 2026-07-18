package com.aionemu.gameserver.model.gameobjects.player;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerMinionsDAO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MINIONS;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the collection of minions associated with a specific player.<br>
 * It handles loading, updating, and synchronizing minion data between the server and the client.<br>
 * Use this class to interact with {@link PlayerMinionsDAO} for persistent storage.
 */
public class MinionList
{
	private final Player player;
	private int lastUsedMinionId;
	private final Map<Integer, MinionCommonData> minions = new HashMap<>();
	private int lastUsedObjId;
	
	/**
	 * Creates a new {@code MinionList} for a specific player.<br>
	 * This constructor initializes the player reference.<br>
	 * It automatically calls {@code loadMinions} to populate the data.
	 * @param player The {@link Player} object associated with this list.
	 */
	MinionList(Player player)
	{
		this.player = player;
		loadMinions();
	}
	
	/**
	 * Loads the list of minions for the current player from the database.<br>
	 * This method populates the internal {@code minions} map.<br>
	 * It also schedules expiration tasks for active minions.<br>
	 * Finally, it identifies and stores the last used minion ID.
	 */
	public void loadMinions()
	{
		final List<MinionCommonData> playerMinions = DAOManager.getDAO(PlayerMinionsDAO.class).getPlayerMinions(player);
		MinionCommonData lastUsedMinion = null;
		for (MinionCommonData minion : playerMinions)
		{
			if (minion.getExpireTime() > 0)
			{
				ExpireTimerTask.getInstance().addTask(minion, player);
			}
			
			minions.put(minion.getObjectId(), minion);
			if ((lastUsedMinion == null) || minion.getDespawnTime().after(lastUsedMinion.getDespawnTime()))
			{
				lastUsedMinion = minion;
			}
		}
		
		if (lastUsedMinion != null)
		{
			lastUsedMinionId = lastUsedMinion.getMinionId();
		}
	}
	
	/**
	 * Retrieves all minions associated with the player.<br>
	 * This method returns a collection of {@link MinionCommonData} objects.
	 * @return A {@code Collection} containing all loaded {@code MinionCommonData}.
	 */
	public Collection<MinionCommonData> getMinions()
	{
		return minions.values();
	}
	
	/**
	 * Retrieves a specific minion based on its unique identifier.<br>
	 * This method looks up the data in the internal {@code minions} map.
	 * @param minionId The unique ID of the minion to find.
	 * @return The {@link MinionCommonData} object associated with the ID, or {@code null} if not found.
	 */
	public MinionCommonData getMinion(int minionId)
	{
		return minions.get(minionId);
	}
	
	/**
	 * Retrieves the data for the minion that was most recently used.<br>
	 * This method calls {@code getMinion} using the stored ID.
	 * @return The {@code MinionCommonData} object of the last used minion, or {@code null} if not found.
	 */
	public MinionCommonData getLastUsedMinion()
	{
		return getMinion(lastUsedMinionId);
	}
	
	/**
	 * Updates the ID of the minion that was most recently used.<br>
	 * This value is stored in the {@code lastUsedMinionId} field.<br>
	 * It helps the system track which minion should be active.
	 * @param lastUsedMinionId The unique identifier for the minion to set as the last used one.
	 */
	public void setLastUsedMinionId(int lastUsedMinionId)
	{
		this.lastUsedMinionId = lastUsedMinionId;
	}
	
	/**
	 * Adds a new minion to the player's collection.<br>
	 * This method creates and stores a {@link MinionCommonData} object.<br>
	 * It initializes the minion with the provided attributes.
	 * @param player The {@code Player} who owns the minion.
	 * @param minionId The unique identifier for the new minion.
	 * @param name The display name of the minion.
	 * @param grade The rarity or grade level of the minion.
	 * @param level The starting experience level of the minion.
	 * @param growth_points The initial growth points assigned to the minion.
	 * @return The newly created {@code MinionCommonData} object.
	 */
	public MinionCommonData addNewMinion(Player player, int minionId, String name, String grade, int level, int growth_points)
	{
		return addNewMinion(player, minionId, name, grade, level, System.currentTimeMillis(), growth_points);
	}
	
	/**
	 * Creates and saves a new minion for a specific player.<br>
	 * This method adds the minion to the database and the local list.
	 * @param player The {@link Player} who owns the new minion.
	 * @param minionId The unique identifier for the minion type.
	 * @param name The custom name given to the minion.
	 * @param minionGrade The grade level of the minion.
	 * @param level The current experience level of the minion.
	 * @param birthday The timestamp representing the minion's birth date.
	 * @param growth_points The amount of growth points assigned to the minion.
	 * @return The newly created {@link MinionCommonData} object.
	 */
	public MinionCommonData addNewMinion(Player player, int minionId, String name, String minionGrade, int level, long birthday, int growth_points)
	{
		final MinionCommonData minionCommonData = new MinionCommonData(minionId, player.getObjectId(), name, minionGrade, level, growth_points, true);
		minionCommonData.setBirthday(new Timestamp(birthday));
		minionCommonData.setDespawnTime(new Timestamp(System.currentTimeMillis()));
		DAOManager.getDAO(PlayerMinionsDAO.class).insertPlayerMinion(minionCommonData);
		minions.put(minionCommonData.getObjectId(), minionCommonData);
		return minionCommonData;
	}
	
	/**
	 * Checks if a specific minion exists in the list.<br>
	 * This method looks for the {@code minionId} within the internal collection.
	 * @param minionId The unique identifier of the minion to check.
	 * @return {@code true} if the minion is found, otherwise {@code false}.
	 */
	public boolean hasMinion(int minionId)
	{
		return minions.containsKey(minionId);
	}
	
	/**
	 * Removes a specific minion from the player's list.<br>
	 * This method checks if the minion exists before deleting it.<br>
	 * It updates both the local memory and the database.
	 * @param minionId The unique identifier of the minion to remove.
	 */
	public void deleteMinion(int minionId)
	{
		if (hasMinion(minionId))
		{
			minions.remove(minionId);
			DAOManager.getDAO(PlayerMinionsDAO.class).removePlayerMinion(player, minionId);
		}
	}
	
	/**
	 * Removes a specific minion from the active list.<br>
	 * This method checks if the {@code minionId} is not {@code 0}.<br>
	 * If valid, it removes the entry from the internal collection.
	 * @param minionId The unique identifier of the minion to remove.
	 */
	public void disMissMinion(int minionId)
	{
		if (minionId != 0)
		{
			minions.remove(minionId);
		}
	}
	
	/**
	 * Refreshes the internal list of minions for the player.<br>
	 * This method clears existing data and reloads it from the database.<br>
	 * It then sends an {@link SM_MINIONS} packet to the player to sync their client.
	 */
	public void updateMinionsList()
	{
		minions.clear();
		for (MinionCommonData minionCommonData : DAOManager.getDAO(PlayerMinionsDAO.class).getPlayerMinions(player))
		{
			minions.put(minionCommonData.getObjectId(), minionCommonData);
		}
		
		if (minions != null)
		{
			PacketSendUtility.sendPacket(player, new SM_MINIONS(1, player.getMinionList().getMinions()));
		}
	}
	
	/**
	 * Updates the ID of the last used object.<br>
	 * This method stores the {@code objId} in the {@code lastUsedObjId} field.
	 * @param objId The unique identifier of the object to save.
	 */
	public void setLastUsed(int objId)
	{
		lastUsedObjId = objId;
	}
	
	/**
	 * Retrieves the ID of the last used object.<br>
	 * This value is updated when {@code setLastUsed} is called.
	 * @return The {@code int} identifier of the last used object.
	 */
	public int getLastUsed()
	{
		return lastUsedObjId;
	}
}
