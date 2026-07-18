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

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.MinionController;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerMinionsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.minion.MinionBuff;
import com.aionemu.gameserver.model.team2.common.legacy.LootRuleType;
import com.aionemu.gameserver.model.templates.item.ItemMinionList;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.templates.item.actions.AbstractItemAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.minion.MinionSkill;
import com.aionemu.gameserver.model.templates.minion.MinionTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MINIONS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndArray;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * This service manages the lifecycle and behavior of {@link Minion} entities.<br>
 * It handles spawning, movement, and interaction logic for all minion types in the game world.
 */
public class MinionService
{
	private List<Integer> minions;
	private MinionBuff mb;
	Logger log = LoggerFactory.getLogger(MinionService.class);
	
	/**
	 * Initializes the minion data for the service.<br>
	 * It loads all minion records from {@link DataManager}.<br>
	 * This method also creates a new instance of {@code MinionBuff}.
	 */
	public void init()
	{
		minions = DataManager.MINION_DATA.getAll();
		mb = new MinionBuff();
		log.info("[MinionService] Loaded " + minions.size() + " Minions");
	}
	
	/**
	 * Creates a new minion for the specified player using an item.<br>
	 * This method handles the animation, logic, and data updates required to summon a minion.<br>
	 * It checks if the item is a valid contract and adds the new minion to the player's list.
	 * @param player The {@link Player} who will own the new minion.
	 * @param itemObjId The unique object ID of the item used to create the minion.
	 */
	public void makeMinion(Player player, int itemObjId)
	{
		final Item item = player.getInventory().getItemByObjId(itemObjId);
		final ItemTemplate it = item.getItemTemplate();
		final ItemMinionList minionList = DataManager.ITEM_MINION_LIST.getMinionList(it.getMinionList());
		final int minionRnd = RndArray.get(minionList.getMinionId());
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 1500, 0));
		player.getController().cancelTask(TaskId.ITEM_USE);
		final ItemUseObserver moveObserver = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 2));
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_MSG_CANCEL_CONTRACT);
			}
		};
		player.getObserveController().attach(moveObserver);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(() ->
		{
			player.getController().cancelTask(TaskId.ITEM_USE);
			player.getObserveController().removeObserver(moveObserver);
			int minionId = 0;
			String grade = "";
			String name = "";
			if (!it.getMinionTicket())
			{
				return;
			}
			
			if (it.isMinionCashContract())
			{
				minionId = minionRnd;
				final MinionTemplate mt = DataManager.MINION_DATA.getMinionTemplate(minionId);
				grade = mt.getGrade();
				name = mt.getName();
			}
			else
			{
				for (Integer asd : minions)
				{
					final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(asd);
					if (mt2.getLevel() == 1)
					{
						minionId = minionRnd;
						final MinionTemplate mt3 = DataManager.MINION_DATA.getMinionTemplate(minionId);
						grade = mt3.getGrade();
						name = mt3.getName();
					}
				}
			}
			
			if (!player.getInventory().decreaseByObjectId(itemObjId, 1))
			{
				return;
			}
			
			if (player.getRace() == Race.ELYOS)
			{
				final QuestState qs15545 = player.getQuestStateList().getQuestState(15545);
				if ((qs15545 != null) && (qs15545.getStatus() == QuestStatus.START) && (qs15545.getQuestVarById(0) == 0))
				{
					ClassChangeService.onUpdateQuest15545(player);
				}
			}
			else
			{
				final QuestState qs15546 = player.getQuestStateList().getQuestState(25545);
				if ((qs15546 != null) && (qs15546.getStatus() == QuestStatus.START) && (qs15546.getQuestVarById(0) == 0))
				{
					ClassChangeService.onUpdateQuest25545(player);
				}
			}
			
			final MinionCommonData mcd = player.getMinionList().addNewMinion(player, minionId, name, grade, 1, 0);
			if (mcd != null)
			{
				PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd, 0));
			}
			
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1));
		}, 1500));
	}
	
	/**
	 * Spawns a specific minion for the given {@link Player}.<br>
	 * This method initializes the {@link Minion} object and updates player data.<br>
	 * It also handles dismissing existing pets or minions before spawning the new one.
	 * @param player The {@link Player} who will own the new minion.
	 * @param minionObjId The unique identifier for the minion to be spawned.
	 */
	public void spawnMinion(Player player, int minionObjId)
	{
		final MinionCommonData minionCommonData = player.getMinionList().getMinion(minionObjId);
		final MinionTemplate minionTemplate = DataManager.MINION_DATA.getMinionTemplate(minionCommonData.getMinionId());
		final MinionController controller = new MinionController();
		final Minion minion = new Minion(minionTemplate, controller, minionCommonData, player);
		final Iterator<MinionSkill> iterator = minionTemplate.getAction().getSkillsCollections().iterator();
		while (iterator.hasNext())
		{
			player.getSkillList().addSkill(player, iterator.next().getSkillId(), 1);
		}
		
		if (player.getPet() != null)
		{
			PetSpawnService.dismissPet(player, true);
		}
		
		if (player.getMinion() != null)
		{
			despawnMinion(player, player.getMinionList().getLastUsed());
		}
		
		minion.setKnownlist(new PlayerAwareKnownList(minion));
		player.setMinion(minion);
		player.getMinionList().setLastUsed(minionObjId);
		player.getCommonData().setLastMinion(minionObjId);
		player.getCommonData().setMinionEnergy(0);
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		mb.apply(player, minionCommonData.getMinionId());
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_MINIONS(6, minionCommonData));
	}
	
	/**
	 * Removes a specific minion from the player.<br>
	 * This method clears all active skills and data for the minion.<br>
	 * It also updates the player's state and saves changes to the database.
	 * @param player The {@link Player} who owns the minion.
	 * @param minionObjId The unique identifier of the minion to remove.
	 */
	public void despawnMinion(Player player, int minionObjId)
	{
		final MinionCommonData minionCommonData = player.getMinionList().getMinion(minionObjId);
		final Iterator<MinionSkill> iterator = DataManager.MINION_DATA.getMinionTemplate(minionCommonData.getMinionId()).getAction().getSkillsCollections().iterator();
		while (iterator.hasNext())
		{
			SkillLearnService.removeSkill(player, iterator.next().getSkillId());
		}
		
		minionCommonData.setIsLooting(false);
		minionCommonData.setIsBuffing(false);
		player.getMinion().getController().delete();
		player.setMinion(null);
		player.getMinionList().setLastUsed(0);
		player.getCommonData().setLastMinion(0);
		player.getCommonData().setMinionEnergy(0);
		player.getMinionList().updateMinionsList();
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		mb.end(player);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_MINIONS(7, minionCommonData));
	}
	
	/**
	 * Handles the logic when a {@link Player} is teleported.<br>
	 * This method clears the player's active minion data if one exists.<br>
	 * It resets minion energy and updates the database via {@link PlayerDAO}.
	 * @param player The {@code Player} object that was teleported.
	 */
	public void onTeleportPlayer(Player player)
	{
		final PlayerCommonData pcd = player.getCommonData();
		final int minionObjId = pcd.getLastMinion();
		if (minionObjId != 0)
		{
			final MinionCommonData minionCommonData = player.getMinionList().getMinion(minionObjId);
			final Iterator<MinionSkill> iterator = DataManager.MINION_DATA.getMinionTemplate(minionCommonData.getMinionId()).getAction().getSkillsCollections().iterator();
			while (iterator.hasNext())
			{
				SkillLearnService.removeSkill(player, iterator.next().getSkillId());
			}
			
			minionCommonData.setIsLooting(false);
			minionCommonData.setIsBuffing(false);
			player.getMinion().getController().delete();
			player.setMinion(null);
			player.getMinionList().setLastUsed(0);
			player.getCommonData().setLastMinion(0);
			player.getCommonData().setMinionEnergy(0);
			player.getMinionList().updateMinionsList();
			DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
			mb.end(player);
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_MINIONS(7, minionCommonData));
		}
	}
	
	/**
	 * Handles the initialization of minion data when a player logs in.<br>
	 * This method sends necessary packets to the client and spawns the last active minion.<br>
	 * It also resets energy levels if a previous minion was active.
	 * @param player The {@code Player} object that just logged into the server.
	 */
	public void onLoggedIn(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_MINIONS(0));
		PacketSendUtility.sendPacket(player, new SM_MINIONS(1, player.getMinionList().getMinions()));
		PacketSendUtility.sendPacket(player, new SM_MINIONS(11, player));
		PacketSendUtility.sendPacket(player, new SM_MINIONS(13));
		PacketSendUtility.sendPacket(player, new SM_MINIONS(14));
		if (player.getCommonData().getLastMinion() != 0)
		{
			player.getCommonData().setMinionEnergy(0);
			DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
			spawnMinion(player, player.getCommonData().getLastMinion());
		}
		
		if (player.getCommonData().getMinionFunctionTime() != null)
		{
			PacketSendUtility.sendPacket(player, new SM_MINIONS(11, -1702967296));
		}
		
		player.getMinionList().updateMinionsList();
	}
	
	/**
	 * Updates the minion energy for a specific {@link Player}.<br>
	 * This method adds the specified amount to the current energy.<br>
	 * The total energy is capped at a maximum value of 5000.<br>
	 * It saves the new data and sends a packet to the client.
	 * @param player The {@code Player} object to update.
	 * @param count The amount of energy to add or subtract.
	 */
	public void onUpdateEnergy(Player player, int count)
	{
		final int energy = player.getCommonData().getMinionEnergy() + count;
		if (energy >= 5000)
		{
			player.getCommonData().setMinionEnergy(5000);
		}
		else
		{
			player.getCommonData().setMinionEnergy(player.getCommonData().getMinionEnergy() + count);
		}
		
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		PacketSendUtility.sendPacket(player, new SM_MINIONS(13));
	}
	
	/**
	 * Increases the growth points of a player's minion.<br>
	 * This method updates the current growth based on the provided {@code exp}.<br>
	 * It ensures that the growth does not exceed the maximum limit defined in the {@link MinionTemplate}.<br>
	 * The updated data is saved to the database and synced to the player.
	 * @param player The {@link Player} who owns the minion.
	 * @param exp The amount of experience points to add to the growth.
	 */
	public void addMinionGrowth(Player player, int exp)
	{
		int finalExp = 0;
		final MinionCommonData mcd = player.getMinionList().getMinion(player.getMinion().getObjectId());
		final MinionTemplate mt = DataManager.MINION_DATA.getMinionTemplate(mcd.getMinionId());
		final int growth = mcd.getGrowthPoints();
		finalExp = growth + exp;
		if (finalExp >= mt.getGrowthMax())
		{
			mcd.setGrowthPoints(mt.getGrowthMax());
		}
		else
		{
			mcd.setGrowthPoints(growth + finalExp);
		}
		
		PacketSendUtility.sendPacket(player, new SM_MINIONS(8, mcd, 0));
		DAOManager.getDAO(PlayerMinionsDAO.class).updateMinionGrowth(mcd);
		player.getMinionList().updateMinionsList();
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out.<br>
	 * It despawns the player's last used minion if one exists.<br>
	 * It resets the player's minion energy to {@code 0}.<br>
	 * Finally, it saves the player data using {@link PlayerDAO}.
	 * @param player The {@code Player} who is logging out.
	 */
	public void onLogout(Player player)
	{
		if (player.getMinion() != null)
		{
			despawnMinion(player, player.getMinionList().getLastUsed());
		}
		
		player.getCommonData().setMinionEnergy(0);
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
	}
	
	/**
	 * Evolves a specific minion by sacrificing other minions.<br>
	 * This method calculates the total experience and kinah cost from the {@code sacrificeMinions} list.<br>
	 * It removes the sacrificed minions and updates the growth points of the target minion.<br>
	 * If the player has insufficient kinah, the process is cancelled.
	 * @param player The {@link Player} who owns the minions.
	 * @param objId The unique object ID of the minion to be grown.
	 * @param sacrificeMinions A list of object IDs for the minions to be sacrificed.
	 */
	public void growthUpMinion(Player player, int objId, List<Integer> sacrificeMinions)
	{
		final MinionCommonData mcd = player.getMinionList().getMinion(objId);
		final MinionTemplate mt = DataManager.MINION_DATA.getMinionTemplate(mcd.getMinionId());
		int finalExp = 0;
		int kinahCost = 0;
		final int growth = mcd.getGrowthPoints();
		for (MinionCommonData list : player.getMinionList().getMinions())
		{
			for (int sacrifices : sacrificeMinions)
			{
				if (list.getObjectId() == sacrifices)
				{
					final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(list.getMinionId());
					kinahCost += mt2.getGrowthCost();
					if (player.getInventory().getKinah() < kinahCost)
					{
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_GROWTH_MSG_NOGOLD);
						return;
					}
					
					finalExp += mt2.getGrowthPoints();
					player.getMinionList().disMissMinion(list.getMinionId());
					PacketSendUtility.sendPacket(player, new SM_MINIONS(3, list, 0));
					dismissMinion(player, list.getObjectId());
				}
			}
		}
		
		if (finalExp <= 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_GROWTH_MSG_NOTSELECT);
			return;
		}
		
		if (player.getInventory().getKinah() < kinahCost)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_GROWTH_MSG_NOGOLD);
			return;
		}
		
		if ((growth + finalExp) > mt.getGrowthMax())
		{
			mcd.setGrowthPoints(mt.getGrowthMax());
		}
		else
		{
			mcd.setGrowthPoints(growth + finalExp);
		}
		
		player.getInventory().decreaseKinah(kinahCost);
		PacketSendUtility.sendPacket(player, new SM_MINIONS(8, mcd, 0));
		DAOManager.getDAO(PlayerMinionsDAO.class).updateMinionGrowth(mcd);
		player.getMinionList().updateMinionsList();
	}
	
	/**
	 * Recharges the minion energy for a specific player.<br>
	 * This method checks if the player has enough {@code Kinah} to pay the cost.<br>
	 * It updates the player's energy and saves the data to the database.
	 * @param player The {@link Player} object who will receive the energy recharge.
	 * @param isAuto A flag indicating whether the recharge process should be automated.
	 */
	public void EnergyRecharge(Player player, int isAuto)
	{
		// boolean auto = false;
		if (player.getCommonData().getMinionEnergy() >= 5000)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_MSG_FENERGY_CHARGE);
			return;
		}
		
		final int rechargeCount = 5000 - player.getCommonData().getMinionEnergy();
		final int price = 20 * rechargeCount;
		if (player.getInventory().getKinah() < price)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_MSG_FENERGY_CHARGE_FAIL_BY_GOLD);
			return;
		}
		
		// if (isAuto == 0)
		// {
		// auto = false;
		// }
		// else
		// {
		// auto = true;
		// }
		
		player.getCommonData().setMinionEnergy(player.getCommonData().getMinionEnergy() + rechargeCount);
		player.getInventory().decreaseKinah(price);
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		PacketSendUtility.sendPacket(player, new SM_MINIONS(13));
	}
	
	/**
	 * Toggles the lock status of a specific minion for a player.<br>
	 * This method updates the {@code MinionCommonData} and saves it to the database.<br>
	 * It also sends an {@link com.aionemu.gameserver.network.aion.serverpackets.SM_MINIONS} packet to the player.
	 * @param player The {@link Player} who owns the minion.
	 * @param objId The unique identifier of the minion object.
	 * @param lock The value used to determine the new lock state.
	 */
	public void lockMinion(Player player, int objId, int lock)
	{
		final MinionCommonData mcd = player.getMinionList().getMinion(objId);
		if (mcd.isLocked())
		{
			mcd.setLocked(false);
		}
		else
		{
			mcd.setLocked(true);
		}
		
		PacketSendUtility.sendPacket(player, new SM_MINIONS(5, mcd, 0));
		DAOManager.getDAO(PlayerMinionsDAO.class).updateMinionLock(mcd);
	}
	
	/**
	 * Evolves a specific minion for the given player.<br>
	 * This method checks if the player has enough items and Kinah to perform the evolution.<br>
	 * If successful, it removes the old minion and adds the new evolved version to the player's list.
	 * @param player The {@link Player} who owns the minion.
	 * @param objId The unique object ID of the minion to evolve.
	 */
	public void EvolveMinion(Player player, int objId)
	{
		String grade = "";
		final MinionCommonData mcd = player.getMinionList().getMinion(objId);
		final MinionTemplate mt = DataManager.MINION_DATA.getMinionTemplate(mcd.getMinionId());
		if (player.getInventory().getItemCountByItemId(mt.getEvolved().getItemId()) < mt.getEvolved().getEvolvedNum())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_EVOLVE_MSG_LACK_ITEM);
			return;
		}
		
		if (player.getInventory().getKinah() < mt.getEvolved().getEvolvedCost())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_EVOLVE_MSG_NOGOLD);
			return;
		}
		
		final String name = mcd.getName();
		final int minionId = mt.getId() + 1;
		player.getInventory().decreaseKinah(mt.getEvolved().getEvolvedCost());
		player.getInventory().decreaseByItemId(mt.getEvolved().getItemId(), mt.getEvolved().getEvolvedNum());
		player.getMinionList().disMissMinion(mt.getId());
		PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd, 0));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1404350, new Object[]
		{
			mcd.getName(),
			mt.getLevel() + 1
		}));
		dismissMinion(player, mcd.getObjectId());
		final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId);
		grade = mt2.getGrade();
		final MinionCommonData mcd2 = player.getMinionList().addNewMinion(player, minionId, name, grade, 1, 0);
		if (mcd2 != null)
		{
			player.getMinionList().updateMinionsList();
			PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd2, 0));
		}
	}
	
	/**
	 * Removes a specific minion from the player's active list.<br>
	 * This method updates the database and resets the player's energy.<br>
	 * It also sends a synchronization packet to the client.
	 * @param player The {@link Player} who owns the minion.
	 * @param objId The unique identifier of the minion to be dismissed.
	 */
	public void dismissMinion(Player player, int objId)
	{
		final MinionCommonData mcd = player.getMinionList().getMinion(objId);
		PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd, 0));
		player.getMinionList().disMissMinion(mcd.getMinionId());
		DAOManager.getDAO(PlayerMinionsDAO.class).removePlayerMinion(player, objId);
		player.getMinionList().getMinions().remove(mcd);
		player.getCommonData().setMinionEnergy(0);
		player.getMinionList().updateMinionsList();
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
	}
	
	/**
	 * Combines multiple minions into a new one based on success chance.<br>
	 * This method removes the input minions and adds a new minion if successful.<br>
	 * It handles different logic for Kerub, Rank A, and standard minion types.
	 * @param player The {@link Player} performing the combination.
	 * @param conbine1 The ID of the first minion to combine.
	 * @param conbine2 The ID of the second minion to combine.
	 * @param conbine3 The ID of the third minion to combine.
	 * @param conbine4 The ID of the fourth minion to combine.
	 */
	public void CombineMinion(Player player, int conbine1, int conbine2, int conbine3, int conbine4)
	{
		final boolean combineSuccess = Rnd.chance(CustomConfig.COMBINE_MINION);
		final MinionCommonData mcd1 = player.getMinionList().getMinion(conbine1);
		final MinionCommonData mcd2 = player.getMinionList().getMinion(conbine2);
		final MinionCommonData mcd3 = player.getMinionList().getMinion(conbine3);
		final MinionCommonData mcd4 = player.getMinionList().getMinion(conbine4);
		final MinionTemplate mt = DataManager.MINION_DATA.getMinionTemplate(mcd1.getMinionId());
		if (combineSuccess)
		{
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd1, 3));
			player.getMinionList().disMissMinion(mcd1.getMinionId());
			dismissMinion(player, mcd1.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd2, 3));
			player.getMinionList().disMissMinion(mcd2.getMinionId());
			dismissMinion(player, mcd2.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd3, 3));
			player.getMinionList().disMissMinion(mcd3.getMinionId());
			dismissMinion(player, mcd3.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd4, 3));
			player.getMinionList().disMissMinion(mcd4.getMinionId());
			dismissMinion(player, mcd4.getObjectId());
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd1, 2));
			player.getMinionList().disMissMinion(mcd1.getMinionId());
			dismissMinion(player, mcd1.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd2, 2));
			player.getMinionList().disMissMinion(mcd2.getMinionId());
			dismissMinion(player, mcd2.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd3, 2));
			player.getMinionList().disMissMinion(mcd3.getMinionId());
			dismissMinion(player, mcd3.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_MINIONS(3, mcd4, 2));
			player.getMinionList().disMissMinion(mcd4.getMinionId());
			dismissMinion(player, mcd4.getObjectId());
		}
		
		if (combineSuccess && !isKerub(mt.getId()) && !isRankA(mt.getId()))
		{
			if (mt.getGradeId() == 3)
			{
				final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(31);
				final int minionId = RndArray.get(minionList1.getMinionId());
				final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId);
				final String grade = mt2.getGrade();
				final String name = mt2.getName();
				final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId, name, grade, 1, 0);
				if (mcd5 != null)
				{
					PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 2));
				}
			}
			else if (mt.getGradeId() == 2)
			{
				final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(32);
				final int minionId2 = RndArray.get(minionList1.getMinionId());
				final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId2);
				final String grade2 = mt2.getGrade();
				final String name2 = mt2.getName();
				final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId2, name2, grade2, 1, 0);
				if (mcd5 != null)
				{
					PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 2));
				}
			}
		}
		else if (mt.getGradeId() == 3)
		{
			final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(30);
			final int minionId = RndArray.get(minionList1.getMinionId());
			final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId);
			final String grade = mt2.getGrade();
			final String name = mt2.getName();
			final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId, name, grade, 1, 0);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
		}
		else if (mt.getGradeId() == 2)
		{
			final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(31);
			final int minionId2 = RndArray.get(minionList1.getMinionId());
			final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId2);
			final String grade2 = mt2.getGrade();
			final String name2 = mt2.getName();
			final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId2, name2, grade2, 1, 0);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
		}
		
		if (combineSuccess && isKerub(mt.getId()))
		{
			if (mt.getGradeId() == 4)
			{
				final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(34);
				final int minionId3 = RndArray.get(minionList1.getMinionId());
				final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId3);
				final String grade3 = mt2.getGrade();
				final String name3 = mt2.getName();
				final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId3, name3, grade3, 1, 0);
				PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
			}
			else if (mt.getGradeId() == 3)
			{
				final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(35);
				final int minionId4 = RndArray.get(minionList1.getMinionId());
				final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId4);
				final String grade4 = mt2.getGrade();
				final String name4 = mt2.getName();
				final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId4, name4, grade4, 1, 0);
				PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
			}
			else if (mt.getGradeId() == 2)
			{
				final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(36);
				final int minionId5 = RndArray.get(minionList1.getMinionId());
				final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId5);
				final String grade5 = mt2.getGrade();
				final String name5 = mt2.getName();
				final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId5, name5, grade5, 1, 0);
				PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
			}
		}
		else if (mt.getGradeId() == 4)
		{
			final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(33);
			final int minionId3 = RndArray.get(minionList1.getMinionId());
			final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId3);
			final String grade3 = mt2.getGrade();
			final String name3 = mt2.getName();
			final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId3, name3, grade3, 1, 0);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
		}
		else if (mt.getGradeId() == 3)
		{
			final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(34);
			final int minionId4 = RndArray.get(minionList1.getMinionId());
			final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId4);
			final String grade4 = mt2.getGrade();
			final String name4 = mt2.getName();
			final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId4, name4, grade4, 1, 0);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
		}
		else if (mt.getGradeId() == 2)
		{
			final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(35);
			final int minionId5 = RndArray.get(minionList1.getMinionId());
			final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId5);
			final String grade5 = mt2.getGrade();
			final String name5 = mt2.getName();
			final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId5, name5, grade5, 1, 0);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
		}
		
		if (combineSuccess && isRankA(mt.getId()))
		{
			if (mt.getGradeId() == 4)
			{
				final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(37);
				final int minionId6 = RndArray.get(minionList1.getMinionId());
				final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId6);
				final String grade6 = mt2.getGrade();
				final String name6 = mt2.getName();
				final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId6, name6, grade6, 1, 0);
				if (mcd5 != null)
				{
					PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 2));
				}
			}
		}
		else if (mt.getGradeId() == 4)
		{
			final ItemMinionList minionList1 = DataManager.ITEM_MINION_LIST.getMinionList(32);
			final int minionId6 = RndArray.get(minionList1.getMinionId());
			final MinionTemplate mt2 = DataManager.MINION_DATA.getMinionTemplate(minionId6);
			final String grade6 = mt2.getGrade();
			final String name6 = mt2.getName();
			final MinionCommonData mcd5 = player.getMinionList().addNewMinion(player, minionId6, name6, grade6, 1, 0);
			if (mcd5 != null)
			{
				PacketSendUtility.sendPacket(player, new SM_MINIONS(2, mcd5, 3));
			}
		}
	}
	
	/**
	 * Checks if a specific minion belongs to the Kerub type.<br>
	 * This method verifies the {@code minionId} against known Kerub IDs.
	 * @param minionId The unique identifier of the minion to check.
	 * @return {@code true} if the minion is a Kerub, otherwise {@code false}.
	 */
	public boolean isKerub(int minionId)
	{
		return (minionId == 980010) || (minionId == 980011) || (minionId == 980012) || (minionId == 980013);
	}
	
	/**
	 * Checks if a specific minion belongs to Rank A.<br>
	 * This method verifies the {@code minionId} against known Rank A IDs.
	 * @param minionId The unique identifier of the minion to check.
	 * @return {@code true} if the minion is Rank A, otherwise {@code false}.
	 */
	public boolean isRankA(int minionId)
	{
		return (minionId == 980063) || (minionId == 980073) || (minionId == 980085) || (minionId == 980089) || (minionId == 980093);
	}
	
	/**
	 * Changes the name of a specific minion for a player.<br>
	 * The new name must not exceed 9 characters in length.<br>
	 * This method updates the database and broadcasts the change to other players.
	 * @param player The {@link Player} who owns the minion.
	 * @param objId The unique identifier of the minion to rename.
	 * @param name The new name to assign to the minion.
	 */
	public void renameMinion(Player player, int objId, String name)
	{
		final MinionCommonData mcd = player.getMinionList().getMinion(objId);
		if (name.length() > 9)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_MSG_FAIL_CHANGE_NAME_OVERLENGTH);
			return;
		}
		
		mcd.setName(name);
		DAOManager.getDAO(PlayerMinionsDAO.class).updateName(mcd);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_MINIONS(4, mcd, 0));
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAMILIAR_MSG_FAIL_CHANGE_NAME_CONFIRM);
	}
	
	/**
	 * Activates the minion function for a specific player.<br>
	 * This method checks if the player has the required item to proceed.<br>
	 * It updates the {@code MinionFunctionTime} and sends necessary packets.
	 * @param player The {@link Player} who is activating the function.
	 */
	public void activateMinionFunction(Player player)
	{
		final long leftTime = System.currentTimeMillis() - 1702967296;
		if (player.getInventory().decreaseByObjectId(190200100, 1))
		{
			player.getCommonData().setMinionFunctionTime(new Timestamp(leftTime));
			PacketSendUtility.sendPacket(player, new SM_MINIONS(11, player));
			PacketSendUtility.sendPacket(player, new SM_MINIONS(14));
		}
	}
	
	/**
	 * Toggles the looting status for a specific minion.<br>
	 * This method updates the {@code isLooting} state and sends the necessary packets to the player.<br>
	 * It also checks if the player is in a team with {@code FREEFORALL} rules before proceeding.
	 * @param player The {@link Player} who owns the minion.
	 * @param minionObj The unique identifier of the minion object.
	 * @param activate The boolean value to set for the looting status.
	 */
	public void activateLoot(Player player, int minionObj, boolean activate)
	{
		if (activate)
		{
			if (player.isInTeam())
			{
				final LootRuleType lootType = player.getLootGroupRules().getLootRule();
				if (lootType == LootRuleType.FREEFORALL)
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE03);
					return;
				}
			}
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE01);
		}
		
		final MinionCommonData mcd = player.getMinionList().getMinion(minionObj);
		mcd.setIsLooting(activate);
		PacketSendUtility.sendPacket(player, new SM_MINIONS(activate));
	}
	
	/**
	 * Moves a doping item within a minion's doping bag.<br>
	 * This method swaps the positions of two items based on the provided slot indices.<br>
	 * It updates the internal data and sends the necessary packets to the {@link Player}.
	 * @param player The {@link Player} who owns the minion.
	 * @param minionObjectId The unique identifier for the specific minion.
	 * @param targetSlot The index of the first item to move.
	 * @param destinationSlot The index of the second item to swap with.
	 */
	public void relocateDoping(Player player, int minionObjectId, int targetSlot, int destinationSlot)
	{
		final MinionCommonData minions = player.getMinionList().getMinion(minionObjectId);
		if ((minions == null) || (minions.getDopingBag() == null))
		{
			return;
		}
		
		final int[] scrollBag = minions.getDopingBag().getScrollsUsed();
		final int targetItem = scrollBag[targetSlot - 2];
		if ((destinationSlot - 2) > (scrollBag.length - 1))
		{
			minions.getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 2, targetItem, destinationSlot));
			minions.getDopingBag().setItem(0, targetSlot);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 2, targetItem, targetSlot));
		}
		else
		{
			minions.getDopingBag().setItem(scrollBag[destinationSlot - 2], targetSlot);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 2, scrollBag[destinationSlot - 2], targetSlot));
			minions.getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 2, targetItem, destinationSlot));
		}
	}
	
	/**
	 * Adds a specific item to the doping bag of a player's minion.<br>
	 * This method updates the {@code MinionCommonData} and sends a packet to the client.
	 * @param player The {@link Player} who owns the minion.
	 * @param action An integer representing the type of action performed.
	 * @param minionObjectId The unique identifier for the minion.
	 * @param itemId The unique identifier of the item to add.
	 * @param targetSlot The specific slot index in the doping bag.
	 */
	public void addItemToDopingBag(Player player, int action, int minionObjectId, int itemId, int targetSlot)
	{
		if (player.getMinion() == null)
		{
			return;
		}
		
		final Minion minions = player.getMinion();
		minions.getCommonData().getDopingBag().setItem(itemId, targetSlot);
		if (minions.getCommonData().getDopingBag().getFoodItem() != 0)
		{
		}
		
		if (minions.getCommonData().getDopingBag().getDrinkItem() != 0)
		{
		}
		
		// for (int n : minions.getCommonData().getDopingBag().getScrollsUsed())
		// {
		// }
		
		PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 0, itemId, targetSlot));
	}
	
	/**
	 * Applies a buff to the player's minion using an item from their inventory.<br>
	 * This method checks for required items and handles cooldowns before executing actions.<br>
	 * It also updates the buff status based on available doping supplies.
	 * @param player The {@link Player} who owns the minion.
	 * @param minionObjectId The unique identifier of the minion to be buffed.
	 * @param itemId The item ID used to provide the buff.
	 * @param slot The inventory slot where the item is located.
	 */
	public void buffPlayer(Player player, int minionObjectId, int itemId, int slot)
	{
		final Minion minion = player.getMinion();
		if ((minion == null) || (minion.getCommonData().getDopingBag() == null))
		{
			return;
		}
		
		final List<Item> items = player.getInventory().getItemsByItemId(itemId);
		final Item useItem = items.get(0);
		final ItemActions itemActions = useItem.getItemTemplate().getActions();
		final ItemUseLimits limit = new ItemUseLimits();
		int useDelay = player.getItemCooldown(useItem.getItemTemplate()) / 3;
		if (useDelay < 3000)
		{
			useDelay = 3000;
		}
		
		limit.setDelayId(useItem.getItemTemplate().getUseLimits().getDelayId());
		limit.setDelayTime(useDelay);
		if (player.isItemUseDisabled(limit))
		{
			final int useItemId = itemId;
			ThreadPoolManager.getInstance().schedule(() -> PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 3, useItemId, slot)), useDelay);
			return;
		}
		
		if (!RestrictionsManager.canUseItem(player, useItem) || player.isProtectionActive())
		{
			player.addItemCoolDown(limit.getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
		}
		else
		{
			player.getController().cancelCurrentSkill();
			for (AbstractItemAction itemAction : itemActions.getItemActions())
			{
				if (itemAction.canAct(player, useItem, null))
				{
					itemAction.act(player, useItem, null);
				}
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 3, itemId, slot));
		itemId = minion.getCommonData().getDopingBag().getFoodItem();
		long totalDopes = player.getInventory().getItemCountByItemId(itemId);
		itemId = minion.getCommonData().getDopingBag().getDrinkItem();
		totalDopes += player.getInventory().getItemCountByItemId(itemId);
		final int[] scrollBag = minion.getCommonData().getDopingBag().getScrollsUsed();
		for (int i = 0; i < scrollBag.length; ++i)
		{
			if (scrollBag[i] != 0)
			{
				totalDopes += player.getInventory().getItemCountByItemId(scrollBag[i]);
			}
		}
		
		if (totalDopes == 0)
		{
			minion.getCommonData().setIsBuffing(false);
			PacketSendUtility.sendPacket(player, new SM_MINIONS(9, 3, itemId, slot));
		}
	}
	
	/**
	 * Provides the global instance of the {@link MinionService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The active {@code MinionService} instance.
	 */
	public static MinionService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final MinionService instance = new MinionService();
	}
}
