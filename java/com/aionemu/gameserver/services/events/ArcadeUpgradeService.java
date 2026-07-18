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
package com.aionemu.gameserver.services.events;

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerUpgradeArcade;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTab;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTabItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPGRADE_ARCADE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for upgrading arcade items within the game.<br>
 * This service manages {@link PlayerUpgradeArcade} actions and processes rewards.<br>
 * It interacts with {@link ItemService} to manage inventory changes.
 * @author Lyras, CoolyT
 */
public class ArcadeUpgradeService
{
	private final int frenzyTime = EventsConfig.EVENT_ARCADE_FRENZY_TIME; // 90 seconds on offi
	
	/**
	 * Initializes a new instance of the {@link ArcadeUpgradeService}.<br>
	 * This service handles all logic related to arcade upgrades.
	 */
	public ArcadeUpgradeService()
	{
	}
	
	/**
	 * Determines the random reward item for a player based on their arcade progress.<br>
	 * This method checks the {@code frenzyLevel} of the player's {@link PlayerUpgradeArcade}.<br>
	 * It selects an item from the corresponding {@code ArcadeTabItem} list.
	 * @param player The {@code Player} object to check for rewards.
	 * @return The randomly selected {@code ArcadeTabItem} reward.
	 */
	public static ArcadeTabItem getRewardItem(Player player)
	{
		final PlayerUpgradeArcade arcade = player.getUpgradeArcade();
		final int frenzyLevel = arcade.getFrenzyLevel();
		final boolean isFrenzy = arcade.isFrenzy();
		int rewardLevel = 0;
		
		if (frenzyLevel < 3)
		{
			// 1-3 RewardLevel 1
			rewardLevel = 1;
		}
		else if ((frenzyLevel >= 4) && (frenzyLevel < 6))
		{
			// 4-5 RewardLevel 2
			rewardLevel = 2;
		}
		else if ((frenzyLevel >= 6) && (frenzyLevel < 8))
		{
			// 6-7 RewardLevel 3
			rewardLevel = 3;
		}
		else if (frenzyLevel >= 8)
		{
			// 8 RewardLevel 4
			rewardLevel = 4;
		}
		
		final List<ArcadeTabItem> items = DataManager.ARCADE_UPGRADE_DATA.getArcadeTabById(rewardLevel);
		final int count = (items.size() - 1) - (isFrenzy ? 0 : 2); // only provide full itemlist if isFrenzy else don't provide the last two items of the list
		
		// ArcadeTabItem itemReward = null;
		// If there is an extra chance to get only frenzy items, the reward is retrieved from the list.
		// else itemReward = items.get(Rnd.get(0, count));
		
		return items.get(Rnd.get(0, count));
	}
	
	/**
	 * Grants a special reward item to the player based on arcade data.<br>
	 * This method handles random selection and sends the appropriate system messages.<br>
	 * It also resets the frenzy status of the {@code PlayerUpgradeArcade}.
	 * @param player The {@code Player} who will receive the reward.
	 */
	public static void getSpecialRewardItem(Player player)
	{
		final PlayerUpgradeArcade arcade = player.getUpgradeArcade();
		final List<ArcadeTabItem> items = DataManager.ARCADE_UPGRADE_DATA.getArcadeTabById(4);
		final ArcadeTabItem item = items.get(Rnd.get(0, items.size()));
		int itemCount = arcade.isFrenzy() ? item.getNormalCount() : item.getFrenzyCount();
		if (itemCount == 0)
		{
			itemCount = item.getFrenzyCount();
		}
		
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(11));
		PacketSendUtility.sendPacket(player, itemCount > 1 ? SM_SYSTEM_MESSAGE.STR_MSG_GACHA_FEVER_ITEM_REWARD_MULTI(item.getItemId(), itemCount) : SM_SYSTEM_MESSAGE.STR_MSG_GACHA_FEVER_ITEM_REWARD(item.getItemId()));
		ItemService.addItem(player, item.getItemId(), itemCount);
		arcade.setFrenzy(false);
	}
	
	/**
	 * Retrieves the singleton instance of the {@link ArcadeUpgradeService}.<br>
	 * Use this method to access the global service for arcade upgrades.
	 * @return The single shared instance of {@code ArcadeUpgradeService}.
	 */
	public static ArcadeUpgradeService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Closes the upgrade arcade window for a specific player.<br>
	 * This method sends the {@code SM_UPGRADE_ARCADE} packet with the value {@code 2}.
	 * @param player The {@link Player} who will have their window closed.
	 */
	public void closeWindow(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(2));
	}
	
	/**
	 * Starts the arcade upgrade process for a specific player.<br>
	 * This method resets the {@code PlayerUpgradeArcade} data.<br>
	 * It then sends an {@code SM_UPGRADE_ARCADE} packet to the player.
	 * @param player The {@link Player} who is starting the upgrade.
	 */
	public void startArcadeUpgrade(Player player)
	{
		PlayerUpgradeArcade arcade = player.getUpgradeArcade();
		
		if (arcade == null)
		{
			arcade = new PlayerUpgradeArcade();
		}
		
		arcade.reset();
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(arcade.getFrenzyPoints(), arcade.getFrenzyCount()));
	}
	
	/**
	 * Displays the list of available rewards to the player.<br>
	 * This method sends a {@code SM_UPGRADE_ARCADE} packet with an index of {@code 10}.
	 * @param player The {@link Player} who will view the reward list.
	 */
	public void showRewardList(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(10));
	}
	
	/**
	 * Retrieves the list of all available arcade tabs.<br>
	 * This method fetches data from {@link DataManager}.
	 * @return a {@code List} containing all {@link ArcadeTab} objects.
	 */
	public List<ArcadeTab> getTabs()
	{
		return DataManager.ARCADE_UPGRADE_DATA.getArcadeTabs();
	}
	
	/**
	 * Attempts to process an upgrade for the player in the Arcade event.<br>
	 * This method checks if the event is enabled and if the player has enough inventory space.<br>
	 * It also verifies if the player has the required items to proceed with the upgrade.
	 * @param player The {@link Player} attempting to perform the arcade upgrade.
	 */
	public void tryArcadeUpgrade(Player player)
	{
		if (!EventsConfig.ENABLE_EVENT_ARCADE)
		{
			return;
		}
		
		final PlayerUpgradeArcade arcade = player.getUpgradeArcade();
		final Storage localStorage = player.getInventory();
		
		if (localStorage.getFreeSlots() < 1)
		{
			PacketSendUtility.sendMessage(player, "Your Inventory is full. You need at least 1 free Slot to play Upgrade Arcade..");
			return;
		}
		
		if (((arcade.getFrenzyLevel() == 1) && (!localStorage.decreaseByItemId(186000389, 1L))) || (arcade.isReTry() && (!localStorage.decreaseByItemId(186000389, 2L))))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GACHA_ITEM_CHECK);
			return;
		}
		
		if (arcade.isFailed() || (arcade.getFrenzyLevel() == 1))
		{
			arcade.setFrenzyPoints(arcade.getFrenzyPoints() + 8);
			arcade.setFailed(false);
		}
		
		if ((arcade.getFrenzyPoints() >= 100) && !arcade.isFrenzy())
		{
			getFrenzyArcade(player, arcade);
		}
		
		if (Rnd.chance(EventsConfig.EVENT_ARCADE_CHANCE))
		{
			getPlaySuccesArcade(player, arcade);
		}
		else
		{
			getPlayFailedArcade(player, arcade);
		}
		
	}
	
	/**
	 * Handles the logic for a successful arcade upgrade.<br>
	 * This method updates the {@code frenzyLevel} of the arcade.<br>
	 * It sends success packets to the {@link Player}.<br>
	 * It schedules a follow-up packet after 3000 milliseconds.
	 * @param player The {@link Player} who performed the action.
	 * @param arcade The {@link PlayerUpgradeArcade} object associated with the attempt.
	 */
	public void getPlaySuccesArcade(Player player, PlayerUpgradeArcade arcade)
	{
		arcade.setFrenzyLevel(arcade.getFrenzyLevel() + 1);
		
		// GameServer.log.info("[ArcadeUpgrade] Sucess ! Player "+player.getName()+" tries ArcadeUpgrade Points: " +arcade.getFrenzyPoints()+ " FrenzyLevel: " +arcade.getFrenzyLevel()+ " isReTry:
		// "+arcade.isReTry() + " frenzyCount : "+arcade.getFrenzyCount());
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(3, true, arcade.getFrenzyPoints()));
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(player, 4, arcade.getFrenzyLevel()));
			}
		}, 3000);
	}
	
	/**
	 * Handles the logic when a player fails an arcade upgrade.<br>
	 * It sends failure packets and schedules a delayed update for the {@code PlayerUpgradeArcade}.<br>
	 * This method updates the frenzy level and retry status based on specific game rules.
	 * @param player The {@link Player} who attempted the upgrade.
	 * @param arcade The {@link PlayerUpgradeArcade} object containing the current progress.
	 */
	public void getPlayFailedArcade(Player player, PlayerUpgradeArcade arcade)
	{
		// when Level is under 6, player can't resume and gets a reward of the lowest level.
		// GameServer.log.info("[ArcadeUpgrade] Failed ! Player "+player.getName()+" tries ArcadeUpgrade Points: " +arcade.getFrenzyPoints()+ " FrenzyLevel: " +arcade.getFrenzyLevel()+ " isReTry:
		// "+arcade.isReTry() + " frenzyCount : "+arcade.getFrenzyCount());
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(3, false, arcade.getFrenzyPoints()));
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(player, 5, arcade.isReTry() ? arcade.getFailedLevel() : 1));
				if ((arcade.getFrenzyLevel() < 6) && !arcade.isReTry())
				{
					arcade.setFrenzyLevel(1);
				}
				else
				{
					arcade.setReTry(true);
					arcade.setFailedLevel(arcade.getFrenzyLevel());
				}
				
				PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(player, 5, arcade.isReTry() ? arcade.getFailedLevel() : 1));
				arcade.setFailed(true);
			}
		}, 3000);
	}
	
	/**
	 * Handles the frenzy logic for a player's arcade upgrade.<br>
	 * It increments the {@code frenzyCount} and triggers special rewards when it reaches 4.<br>
	 * This method also manages the temporary frenzy state and sends the necessary network packets.
	 * @param player The {@link Player} who is performing the action.
	 * @param arcade The {@link PlayerUpgradeArcade} object associated with the player.
	 */
	public void getFrenzyArcade(Player player, PlayerUpgradeArcade arcade)
	{
		if (arcade.getFrenzyCount() < 4)
		{
			arcade.setFrenzyCount(arcade.getFrenzyCount() + 1);
		}
		
		// User gets a random rewardItem of highest rewardLevel for 4 times Frenzy
		if (arcade.getFrenzyCount() == 4)
		{
			arcade.setFrenzy(true);
			getSpecialRewardItem(player); // if this is called before arcade.setFrenzy(true) Player didn't have the chance to get a frenzy_item of highest level.
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GACHA_FEVERTIME_START);
		
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(7, frenzyTime, arcade.getFrenzyCount()));
		
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				final PlayerUpgradeArcade arcade = player.getUpgradeArcade();
				PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(7, 0, arcade.getFrenzyCount()));
				player.getUpgradeArcade().setFrenzy(false);
				
				if (arcade.getFrenzyCount() >= 4)
				{
					arcade.setFrenzyCount(0);
				}
				
			}
		}, frenzyTime * 1000); // offi 90 seconds timer
		player.getUpgradeArcade().setFrenzyPoints(0);
		
		if (arcade.getFrenzyCount() == 4)
		{
			arcade.setFrenzyCount(0);
		}
	}
	
	/**
	 * Grants a reward to the specified {@link Player}.<br>
	 * This method checks if the arcade event is enabled.<br>
	 * It determines the reward amount based on whether the player is in frenzy mode.<br>
	 * The item is added to the inventory and the arcade state is reset.
	 * @param player The {@code Player} who will receive the reward.
	 */
	public void getReward(Player player)
	{
		if (!EventsConfig.ENABLE_EVENT_ARCADE)
		{
			return;
		}
		
		final PlayerUpgradeArcade arcade = player.getUpgradeArcade();
		
		final ArcadeTabItem item = getRewardItem(player);
		final int itemCount = arcade.isFrenzy() ? item.getNormalCount() : item.getFrenzyCount();
		
		if (arcade.isFrenzy())
		{
			PacketSendUtility.sendPacket(player, itemCount >= 1 ? SM_SYSTEM_MESSAGE.STR_MSG_GACHA_FEVER_ITEM_REWARD_MULTI(item.getItemId(), itemCount) : SM_SYSTEM_MESSAGE.STR_MSG_GACHA_FEVER_ITEM_REWARD(item.getItemId()));
		}
		else
		{
			PacketSendUtility.sendPacket(player, itemCount >= 1 ? SM_SYSTEM_MESSAGE.STR_MSG_GACHA_ITEM_REWARD_MULTI(item.getItemId(), itemCount) : SM_SYSTEM_MESSAGE.STR_MSG_GACHA_ITEM_REWARD(item.getItemId()));
		}
		
		if (itemCount == 0)
		{
			ItemService.addItem(player, item.getItemId(), item.getFrenzyCount());
		}
		else
		{
			ItemService.addItem(player, item.getItemId(), itemCount);
		}
		
		PacketSendUtility.sendPacket(player, new SM_UPGRADE_ARCADE(6, item));
		arcade.reset();
	}
	
	private static class SingletonHolder
	{
		protected static final ArcadeUpgradeService instance = new ArcadeUpgradeService();
	}
}
