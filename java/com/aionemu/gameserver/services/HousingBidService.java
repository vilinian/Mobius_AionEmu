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
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.HouseBidsDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PlayerHouseOwnerFlags;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.HouseBidEntry;
import com.aionemu.gameserver.model.house.HouseStatus;
import com.aionemu.gameserver.model.house.PlayerHouseBid;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.housing.HousingLand;
import com.aionemu.gameserver.model.templates.housing.Sale;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_ACQUIRE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECEIVE_BIDS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.mail.AuctionResult;
import com.aionemu.gameserver.services.mail.MailFormatter;
import com.aionemu.gameserver.taskmanager.AbstractCronTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * Manages the bidding process for housing properties within the game world.<br>
 * This service handles bid submissions, updates {@link HouseBidEntry} records, and processes auction results.<br>
 * It uses a scheduled task to periodically check and manage active bids.
 * @author Rolandas
 */
public class HousingBidService extends AbstractCronTask
{
	private static final Logger log = LoggerFactory.getLogger("HOUSE_AUCTION_LOG");
	private static final String registerEndExpression = HousingConfig.HOUSE_REGISTER_END;
	private static CronExpression registerDateExpr;
	private static final Map<Integer, HouseBidEntry> houseBids;
	private static final Map<Integer, HouseBidEntry> playerBids;
	private static final Map<Integer, HouseBidEntry> bidsByIndex;
	private static int timeProlonged = 0;
	private static boolean isDataLoaded = false;
	private static HousingBidService instance;
	
	static
	{
		houseBids = new ConcurrentHashMap<>();
		playerBids = new ConcurrentHashMap<>();
		bidsByIndex = new ConcurrentHashMap<>();
		try
		{
			instance = new HousingBidService(HousingConfig.HOUSE_AUCTION_TIME);
		}
		catch (ParseException pe)
		{
			log.error("[HousingBidService] Error at parsing housing bids: " + pe.getMessage());
		}
	}
	
	/**
	 * Initializes a new instance of the {@link HousingBidService}.<br>
	 * This constructor sets up the service using a specific auction time.
	 * @param auctionTime The string representation of the auction start time.
	 * @throws ParseException If the provided {@code auctionTime} is not in a valid format.
	 */
	private HousingBidService(String auctionTime) throws ParseException
	{
		super(auctionTime);
	}
	
	/**
	 * Retrieves the singleton instance of the {@link HousingBidService}.<br>
	 * This method provides a global access point to the housing bidding system.
	 * @return The active {@code HousingBidService} instance.
	 */
	public static HousingBidService getInstance()
	{
		return instance;
	}
	
	/**
	 * Calculates the time remaining until the next auction starts.<br>
	 * This method returns the delay in milliseconds.<br>
	 * It converts the value from {@code getSecondsTillAuction} to a millisecond format.
	 * @return The number of milliseconds until the auction begins.
	 */
	@Override
	protected long getRunDelay()
	{
		return (long) getSecondsTillAuction() * 1000;
	}
	
	/**
	 * Returns the configuration key for the server time.<br>
	 * This value is used to identify the maintenance period.
	 * @return a {@code String} representing the variable name.
	 */
	@Override
	protected String getServerTimeVariable()
	{
		return "auctionTime";
	}
	
	/**
	 * Checks if this task is allowed to run during the initial server startup.<br>
	 * This method always returns {@code true}.
	 * @return {@code true} if the task can run on init, otherwise {@code false}.
	 */
	@Override
	protected boolean canRunOnInit()
	{
		return true;
	}
	
	/**
	 * Performs initialization tasks after the service is created.<br>
	 * It parses the registration end expression into a {@code CronExpression}.<br>
	 * It also loads the {@code auctionProlonged} value from the database.
	 */
	@Override
	protected void postInit()
	{
		try
		{
			registerDateExpr = new CronExpression(registerEndExpression);
		}
		catch (ParseException e)
		{
			log.error("[HousingBidService] Error with CronExpression: " + e.getMessage());
		}
		
		final ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		timeProlonged = dao.load("auctionProlonged");
	}
	
	/**
	 * Private constructor for the {@link HousingBidService} class.<br>
	 * It initializes the service using the default auction time from {@code HousingConfig}.<br>
	 * This method is not intended to be called directly by other classes.
	 * @throws ParseException
	 */
	private HousingBidService() throws ParseException
	{
		super(HousingConfig.HOUSE_AUCTION_TIME);
	}
	
	/**
	 * Initializes the housing auction service.<br>
	 * This method loads all bid data from the database.<br>
	 * It also handles automatic bid filling if enabled in {@link HousingConfig}.<br>
	 * Finally, it sets the internal data loaded flag to {@code true}.
	 */
	public void start()
	{
		loadBidData();
		if (HousingConfig.FILL_HOUSE_BIDS_AUTO)
		{
			log.info("[HousingBidService] Auction auto filling is enabled.");
			final int added = fillBidData();
			log.info("[HousingBidService] added " + added + " new house bids.");
		}
		
		final int minutes = getMinutesTillAuction();
		final String timeString = (minutes / 24 / 60) + " days " + ((minutes / 60) % 24) + " hours and " + (minutes % 60) + " minutes";
		log.info("[HousingBidService] Starting Auction in " + timeString);
		isDataLoaded = true;
	}
	
	/**
	 * Populates the auction with houses that meet specific criteria.<br>
	 * It selects random houses from the custom list and adds them to the auction.<br>
	 * The method skips houses that already have owners or exceed auto-filling limits.
	 * @return The total number of houses successfully added to the auction.
	 */
	private int fillBidData()
	{
		int count = 0;
		final List<House> houses = HousingService.getInstance().getCustomHouses();
		while (!houses.isEmpty())
		{
			final House house = houses.get(Rnd.get(houses.size()));
			houses.remove(house);
			if ((house.getOwnerId() != 0) || houseBids.containsKey(house.getObjectId()) || !checkAutoFillingLimits(house.getPlayerRace(), house.getHouseType()))
			{
				continue;
			}
			
			addHouseToAuction(house, house.getDefaultAuctionPrice());
			count++;
		}
		
		return count;
	}
	
	/**
	 * Checks if the auto-filling limit for a specific house type has been reached.<br>
	 * It compares the current bid count against values defined in {@link HousingConfig}.
	 * @param race The {@code Race} of the player involved.
	 * @param type The {@code HouseType} being checked.
	 * @return {@code true} if more auto-fills are allowed, or {@code false} otherwise.
	 */
	private boolean checkAutoFillingLimits(Race race, HouseType type)
	{
		final int bidsCount = getBidsCountByType(race, type);
		switch (type)
		{
			case HOUSE:
				if (bidsCount >= HousingConfig.FILL_AUTO_HOUSES_COUNT)
				{
					return false;
				}
				break;
			case MANSION:
				if (bidsCount >= HousingConfig.FILL_AUTO_MANSION_COUNT)
				{
					return false;
				}
				break;
			case ESTATE:
				if (bidsCount >= HousingConfig.FILL_AUTO_ESTATE_COUNT)
				{
					return false;
				}
				break;
			case PALACE:
				if (bidsCount >= HousingConfig.FILL_AUTO_PALACE_COUNT)
				{
					return false;
				}
				break;
			default:
				break;
		}
		
		return true;
	}
	
	/**
	 * Calculates the total number of bids for a specific house type and race.<br>
	 * It iterates through all current {@link HouseBidEntry} objects to find matches.
	 * @param race The {@link Race} category to filter by.
	 * @param type The {@link HouseType} category to filter by.
	 * @return The total count of matching bids.
	 */
	private int getBidsCountByType(Race race, HouseType type)
	{
		int count = 0;
		for (HouseBidEntry entry : houseBids.values())
		{
			final HousingLand land = DataManager.HOUSE_DATA.getLand(entry.getLandId());
			final Race entryRace = DataManager.NPC_DATA.getNpcTemplate(land.getManagerNpcId()).getTribe() == TribeClass.GENERAL ? Race.ELYOS : Race.ASMODIANS;
			if ((entryRace == race) && (entry.getHouseType() == type))
			{
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Loads all house bidding data from the database.<br>
	 * This method populates the internal bid maps and sorts them by price.<br>
	 * It also ensures that houses with a {@code SELL_WAIT} status are activated if no bids exist.
	 */
	private void loadBidData()
	{
		final Set<PlayerHouseBid> playerBidData = DAOManager.getDAO(HouseBidsDAO.class).loadBids();
		
		final List<PlayerHouseBid> sortedBids = new ArrayList<>(playerBidData);
		log.info("[HousingBidService] Loaded " + playerBidData.size() + " House Bids");
		Collections.sort(sortedBids);
		
		final Map<Integer, House> housesById = new HashMap<>();
		for (House house : HousingService.getInstance().getCustomHouses())
		{
			housesById.put(house.getObjectId(), house);
		}
		
		int entryIndex = 1;
		for (PlayerHouseBid playerBid : sortedBids)
		{
			final House house = housesById.get(playerBid.getHouseId());
			if (house == null)
			{
				log.warn("[HousingBidService] Missing house " + playerBid.getHouseId() + " player " + playerBid.getPlayerId() + " bid.");
				continue;
			}
			
			HouseBidEntry entry = houseBids.get(house.getObjectId());
			if (entry == null)
			{
				entry = new HouseBidEntry(house, entryIndex, playerBid.getBidOffer());
				houseBids.put(house.getObjectId(), entry);
				bidsByIndex.put(entryIndex++, entry);
			}
			else if (entry.getBidPrice() < playerBid.getBidOffer())
			{
				// find max price
				entry.setBidPrice(playerBid.getBidOffer());
			}
			
			if (playerBid.getPlayerId() != 0)
			{
				final HouseBidEntry playerEntry = (HouseBidEntry) entry.Clone();
				playerEntry.setBidPrice(playerBid.getBidOffer());
				playerBids.put(playerBid.getPlayerId(), playerEntry);
				
				entry.setLastBiddingPlayer(playerBid.getPlayerId());
				entry.setLastBidTime(playerBid.getTime().getTime());
				entry.incrementBidCount();
			}
		}
		
		// check to see if bids were not removed from DB manually
		for (House house : housesById.values())
		{
			if (house.getOwnerId() == 0)
			{
				continue;
			}
			
			if ((house.getStatus() == HouseStatus.SELL_WAIT) && !houseBids.containsKey(house.getObjectId()))
			{
				log.warn("[HousingBidService] House address=" + house.getAddress().getId() + " has status SELL_WAIT but no bid exists. Activated.");
				house.setStatus(HouseStatus.ACTIVE);
				house.setSellStarted(null);
				house.save();
			}
		}
	}
	
	/**
	 * Processes the results of the housing auction.<br>
	 * This method determines winners, handles successful sales, and manages failed auctions.<br>
	 * It also sends notifications to players and updates house statuses in the database.
	 */
	@Override
	protected void executeTask()
	{
		if (!HousingConfig.ENABLE_HOUSE_AUCTIONS)
		{
			return;
		}
		
		while (!isDataLoaded)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
		
		final Map<HouseBidEntry, Integer> winners = new HashMap<>();
		final Map<HouseBidEntry, Integer> successSell = new HashMap<>();
		final Map<HouseBidEntry, Integer> failedSell = new HashMap<>();
		
		for (Entry<Integer, HouseBidEntry> playerBid : playerBids.entrySet())
		{
			final int playerId = playerBid.getKey();
			final HouseBidEntry houseBid = getBidByEntryIndex(playerBid.getValue().getEntryIndex());
			final House house = HousingService.getInstance().getHouseByAddress(houseBid.getAddress());
			if (playerBid.getValue().getBidPrice() == houseBid.getBidPrice())
			{
				// The player can not be top 1 for both bids, no need to check other
				if (house.getOwnerId() == 0)
				{
					winners.put(houseBid, playerId); // our sold house
				}
				else
				{
					successSell.put(houseBid, playerId); // player sold house
				}
			}
		}
		
		for (HouseBidEntry houseBid : houseBids.values())
		{
			final House house = HousingService.getInstance().getHouseByAddress(houseBid.getAddress());
			if (houseBid.getBidCount() > 0)
			{
				continue;
			}
			
			if (house.getOwnerId() != 0)
			{
				// add only player sold houses
				failedSell.put(houseBid, house.getOwnerId());
			}
		}
		
		// send mails + messages if players are online
		if (LoggingConfig.LOG_HOUSE_AUCTION)
		{
			log.info("[HousingBidService] ##### " + winners.size() + " Houses sold by Auction System #####");
		}
		
		// check houses sold by administrators
		for (Entry<HouseBidEntry, Integer> winData : winners.entrySet())
		{
			final House wonHouse = HousingService.getInstance().getHouseByAddress(winData.getKey().getAddress());
			if (getPlayerData(winData.getValue()) == null)
			{
				log.warn("[HousingBidService] Missing Player with ID:" + winData.getValue() + " for Housebid on address:" + winData.getKey().getAddress());
				continue;
			}
			
			final AuctionResult result = completeHouseSell(getPlayerData(winData.getValue()), wonHouse);
			
			if (LoggingConfig.LOG_HOUSE_AUCTION)
			{
				log.info("[HousingBidService] Address " + wonHouse.getAddress().getId() + " sold for price " + winData.getKey().getBidPrice() + " (bid count: " + winData.getKey().getBidCount() + "; result: " + result + ") to player " + winData.getKey().getLastBiddingPlayer());
			}
		}
		
		// check houses sold by players
		long time = System.currentTimeMillis();
		
		if (LoggingConfig.LOG_HOUSE_AUCTION)
		{
			log.info("[HousingBidService] ##### " + successSell.size() + " Houses auctioned by players #####");
		}
		
		for (Entry<HouseBidEntry, Integer> sellData : successSell.entrySet())
		{
			final House soldHouse = HousingService.getInstance().getHouseByAddress(sellData.getKey().getAddress());
			final PlayerCommonData buyerPcd = getPlayerData(sellData.getValue());
			final PlayerCommonData sellerPcd = getPlayerData(soldHouse.getOwnerId());
			
			if (buyerPcd.getPlayerObjId() == soldHouse.getOwnerId())
			{
				log.warn("[HousingBidService] Selling house to its own owner, cancelling!");
				continue;
			}
			
			if (sellerPcd.isOnline())
			{
				PacketSendUtility.sendPacket(sellerPcd.getPlayer(), SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_AUCTION_SUCCESS(sellData.getKey().getAddress()));
			}
			
			if (buyerPcd.isOnline())
			{
				PacketSendUtility.sendPacket(buyerPcd.getPlayer(), SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_BID_WIN(sellData.getKey().getAddress()));
			}
			
			final long returnKinah = sellData.getKey().getBidPrice() + sellData.getKey().getRefundKinah();
			if (soldHouse.isInGracePeriod())
			{
				soldHouse.revokeOwner();
				
				// activate previously obtained house
				final House newHouse = HousingService.getInstance().activateBoughtHouse(sellerPcd.getPlayerObjId());
				if (sellerPcd.isOnline())
				{
					PacketSendUtility.sendPacket(sellerPcd.getPlayer(), new SM_HOUSE_ACQUIRE(sellerPcd.getPlayerObjId(), newHouse.getAddress().getId(), true));
					PacketSendUtility.sendPacket(sellerPcd.getPlayer(), new SM_HOUSE_OWNER_INFO(sellerPcd.getPlayer(), newHouse));
					sellerPcd.getPlayer().setHouseRegistry(newHouse.getRegistry());
				}
				
				MailFormatter.sendHouseAuctionMail(newHouse, sellerPcd, AuctionResult.GRACE_SUCCESS, time, returnKinah);
			}
			else
			{
				MailFormatter.sendHouseAuctionMail(soldHouse, sellerPcd, AuctionResult.SUCCESS_SALE, time, returnKinah);
				soldHouse.revokeOwner();
			}
			
			final AuctionResult result = completeHouseSell(buyerPcd, soldHouse);
			
			if (LoggingConfig.LOG_HOUSE_AUCTION)
			{
				log.info("[HousingBidService] Address " + soldHouse.getAddress().getId() + " sold by player " + sellerPcd.getPlayerObjId() + " for price " + sellData.getKey().getBidPrice() + " (bid count: " + sellData.getKey().getBidCount() + "; result: " + result + ") to player " + sellData.getKey().getLastBiddingPlayer());
			}
		}
		
		// not sold houses
		for (Entry<HouseBidEntry, Integer> notSoldData : failedSell.entrySet())
		{
			final HouseBidEntry bidEntry = notSoldData.getKey();
			final PlayerCommonData sellerPcd = getPlayerData(notSoldData.getValue());
			final House bidHouse = HousingService.getInstance().getHouseByAddress(bidEntry.getAddress());
			
			if (sellerPcd.isOnline())
			{
				PacketSendUtility.sendPacket(sellerPcd.getPlayer(), SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_AUCTION_FAIL(bidHouse.getAddress().getId()));
			}
			
			AuctionResult result = AuctionResult.FAILED_SALE;
			long compensation = 0;
			
			if (bidHouse.isInGracePeriod())
			{
				final long timePassed = (getAuctionStartTime() - bidHouse.getSellStarted().getTime()) / 1000;
				if (timePassed > (7 * 24 * 3600))
				{
					// more than one week, i.e. 2 weeks passed
					bidHouse.revokeOwner();
					final House activatedHouse = HousingService.getInstance().activateBoughtHouse(sellerPcd.getPlayerObjId());
					if (sellerPcd.isOnline())
					{
						if (activatedHouse != null)
						{
							PacketSendUtility.sendPacket(sellerPcd.getPlayer(), new SM_HOUSE_ACQUIRE(sellerPcd.getPlayerObjId(), activatedHouse.getAddress().getId(), true));
							sellerPcd.getPlayer().setHouseRegistry(activatedHouse.getRegistry());
						}
						
						PacketSendUtility.sendPacket(sellerPcd.getPlayer(), new SM_HOUSE_OWNER_INFO(sellerPcd.getPlayer(), activatedHouse));
					}
					
					result = AuctionResult.GRACE_FAIL;
					time = System.currentTimeMillis();
					compensation = bidHouse.getDefaultAuctionPrice();
				}
			}
			else
			{
				bidHouse.setStatus(HouseStatus.ACTIVE);
				time = bidHouse.getSellStarted().getTime();
			}
			
			bidHouse.save();
			MailFormatter.sendHouseAuctionMail(bidHouse, sellerPcd, result, time, compensation);
			if (LoggingConfig.LOG_HOUSE_AUCTION)
			{
				log.info("[HousingBidService] Address " + bidHouse.getAddress().getId() + " not sold for price " + bidEntry.getBidPrice() + " (result: " + result + "; return: " + compensation + " kinah) by player " + sellerPcd.getPlayerObjId());
			}
		}
		
		final List<HouseBidEntry> copy = new ArrayList<>();
		copy.addAll(houseBids.values());
		
		houseBids.clear();
		playerBids.clear();
		bidsByIndex.clear();
		
		// add back auto auctioned houses (with grace period ended) + admin houses not sold
		if (LoggingConfig.LOG_HOUSE_AUCTION)
		{
			log.info("[HousingBidService] ##### " + (copy.size() - winners.size()) + " Houses added back to auction #####");
		}
		
		for (HouseBidEntry houseBid : copy)
		{
			final House house = HousingService.getInstance().getHouseByAddress(houseBid.getAddress());
			DAOManager.getDAO(HouseBidsDAO.class).deleteHouseBids(house.getObjectId());
			if (house.getOwnerId() == 0)
			{
				house.setStatus(HouseStatus.NOSALE);
				addHouseToAuction(house);
				if (LoggingConfig.LOG_HOUSE_AUCTION)
				{
					log.info("[HousingBidService] Address " + houseBid.getAddress() + " not sold for price " + houseBid.getBidPrice());
				}
			}
		}
	}
	
	/**
	 * Performs cleanup actions after a task execution.<br>
	 * This method resets the {@code timeProlonged} variable to {@code 0}.<br>
	 * It then saves this value to the database using {@link ServerVariablesDAO}.
	 */
	@Override
	protected void postRun()
	{
		final ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		timeProlonged = 0;
		dao.store("auctionProlonged", timeProlonged);
	}
	
	/**
	 * Retrieves the start time of the auction.<br>
	 * This value is calculated based on the current run time.
	 * @return The auction start time as a {@code long}.
	 */
	public long getAuctionStartTime()
	{
		return (long) (getRunTime() - (7 * 24 * 3600)) * 1000;
	}
	
	/**
	 * Calculates the remaining time until the auction starts.<br>
	 * This method returns the difference in seconds between the current time and the scheduled start.<br>
	 * If the auction has already started, it returns {@code 0}.
	 * @return The number of seconds remaining until the auction begins.
	 */
	public int getSecondsTillAuction()
	{
		int left = (int) (getRunTime() - (System.currentTimeMillis() / 1000));
		left += timeProlonged * 60;
		if (left < 0)
		{
			return 0;
		}
		
		return left;
	}
	
	/**
	 * Calculates the remaining time until the next auction starts.<br>
	 * This method converts the total seconds into minutes.
	 * @return The number of minutes left before the auction begins.
	 */
	public int getMinutesTillAuction()
	{
		return getSecondsTillAuction() / 60;
	}
	
	/**
	 * Checks if players are currently allowed to place bids.<br>
	 * This method compares the current time against the auction end time.<br>
	 * It ensures bidding is enabled based on the scheduled run period.
	 * @return {@code true} if bidding is permitted, {@code false} otherwise.
	 */
	public boolean isBiddingAllowed()
	{
		final ZonedDateTime now = ZonedDateTime.now();
		final ZonedDateTime auctionEnd = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(((long) getRunTime() + (timeProlonged * 60)) * 1000), java.time.ZoneId.systemDefault());
		return !((now.getDayOfWeek().getValue() == auctionEnd.getDayOfWeek().getValue()) && auctionEnd.minusDays(1).isAfter(ZonedDateTime.now()));
	}
	
	/**
	 * Checks if players are currently allowed to register for the house auction.<br>
	 * This method compares the current time against the registration and auction schedules.
	 * @return {@code true} if registration is open, or {@code false} otherwise.
	 */
	public boolean isRegisteringAllowed()
	{
		final ZonedDateTime now = ZonedDateTime.now();
		final ZonedDateTime registerEnd = ZonedDateTime.ofInstant(registerDateExpr.getTimeAfter(java.util.Date.from(now.toInstant())).toInstant(), java.time.ZoneId.systemDefault());
		final ZonedDateTime auctionEnd = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(((long) getRunTime() + (timeProlonged * 60)) * 1000), java.time.ZoneId.systemDefault());
		return !(((now.getDayOfWeek().getValue() == registerEnd.getDayOfWeek().getValue()) && (now.getHour() >= registerEnd.getHour())) || ((now.getDayOfWeek().getValue() == auctionEnd.getDayOfWeek().getValue()) && (now.getHour() <= auctionEnd.getHour())));
	}
	
	/**
	 * Retrieves the common data for a specific player.<br>
	 * It first tries to find an active {@link Player} in the world.<br>
	 * If no active player is found, it loads the data from the database.
	 * @param objectId The unique identifier of the player.
	 * @return The {@code PlayerCommonData} associated with the given ID.
	 */
	private PlayerCommonData getPlayerData(int objectId)
	{
		final Player player = World.getInstance().findPlayer(objectId);
		if (player == null)
		{
			return DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(objectId);
		}
		
		return player.getCommonData();
	}
	
	/**
	 * Finalizes the sale of a house to a winning player.<br>
	 * This method updates ownership, handles grace periods for existing houses, and sends notifications.<br>
	 * It also kicks any current visitors from the property.
	 * @param winner The {@link PlayerCommonData} of the player who won the auction.
	 * @param obtainedHouse The {@link House} object that is being sold to the winner.
	 * @return The {@link AuctionResult} indicating if it was a direct win or a grace period start.
	 */
	public AuctionResult completeHouseSell(PlayerCommonData winner, House obtainedHouse)
	{
		House winnerHouse = HousingService.getInstance().getPlayerStudio(winner.getPlayerObjId());
		AuctionResult result = AuctionResult.WIN_BID;
		long time = System.currentTimeMillis();
		if (winnerHouse != null)
		{
			winnerHouse.revokeOwner();
		}
		else
		{
			final int address = HousingService.getInstance().getPlayerAddress(winner.getPlayerObjId());
			if (address > 0)
			{
				// old house exists
				winnerHouse = HousingService.getInstance().getHouseByAddress(address);
				
				// grace period start, sell time remains from this auction
				winnerHouse.setSellStarted(new Timestamp(getAuctionStartTime()));
				
				// make the new house inactive until the old one is sold
				obtainedHouse.setStatus(HouseStatus.INACTIVE);
				result = AuctionResult.GRACE_START;
				
				// (legacy alternative removed during joda-time migration)
				time = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(getRunTime() * 1000L), java.time.ZoneId.systemDefault()).plusWeeks(2).toInstant().toEpochMilli();
			}
		}
		
		obtainedHouse.setOwnerId(winner.getPlayerObjId());
		if (result == AuctionResult.WIN_BID)
		{
			obtainedHouse.setAcquiredTime(new Timestamp(System.currentTimeMillis()));
			obtainedHouse.setStatus(HouseStatus.ACTIVE);
			obtainedHouse.setFeePaid(true);
			obtainedHouse.setNextPay(null);
			obtainedHouse.setSellStarted(null);
			obtainedHouse.reloadHouseRegistry();
			obtainedHouse.save();
		}
		
		if (winner.isOnline())
		{
			if (result == AuctionResult.WIN_BID)
			{
				winner.getPlayer().setHouseRegistry(obtainedHouse.getRegistry());
				winner.getPlayer().setBuildingOwnerState(PlayerHouseOwnerFlags.HOUSE_OWNER.getId());
				PacketSendUtility.sendPacket(winner.getPlayer(), new SM_HOUSE_ACQUIRE(winner.getPlayerObjId(), obtainedHouse.getAddress().getId(), true));
				PacketSendUtility.sendPacket(winner.getPlayer(), new SM_HOUSE_OWNER_INFO(winner.getPlayer(), obtainedHouse));
			}
			
			PacketSendUtility.sendPacket(winner.getPlayer(), SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_BID_WIN(obtainedHouse.getAddress().getId()));
		}
		
		MailFormatter.sendHouseAuctionMail(obtainedHouse, winner, result, time, 0);
		obtainedHouse.getController().kickVisitors(null, true, true);
		return result;
	}
	
	/**
	 * Adds a specific {@link House} to the current auction list.<br>
	 * This method uses the default price from the {@code House} object.<br>
	 * It returns {@code true} if the house was added successfully.<br>
	 * It returns {@code false} if the addition failed.
	 * @param house The {@code House} object to be included in the auction.
	 * @return {@code true} if successful, otherwise {@code false}.
	 */
	public boolean addHouseToAuction(House house)
	{
		return addHouseToAuction(house, house.getDefaultAuctionPrice());
	}
	
	/**
	 * Adds a {@code House} to the auction system with a starting price.<br>
	 * This method updates the house status and registers it in the bid tracking maps.<br>
	 * It saves the changes to the database via {@link HouseBidsDAO}.
	 * @param house The {@code House} object to be added to the auction.
	 * @param initialPrice The starting price for the auction.
	 * @return {@code true} if the house was successfully added, otherwise {@code false}.
	 */
	public boolean addHouseToAuction(House house, long initialPrice)
	{
		if (house.getStatus() == HouseStatus.SELL_WAIT)
		{
			return false;
		}
		
		house.setStatus(HouseStatus.SELL_WAIT);
		int maxIndex = 1;
		HouseBidEntry bidEntry = null;
		
		synchronized (bidsByIndex)
		{
			for (Integer index : bidsByIndex.keySet())
			{
				if (index > maxIndex)
				{
					maxIndex = index;
				}
			}
			
			bidEntry = new HouseBidEntry(house, ++maxIndex, initialPrice);
			bidsByIndex.put(maxIndex, bidEntry);
		}
		
		synchronized (houseBids)
		{
			houseBids.put(house.getObjectId(), bidEntry);
		}
		
		final Timestamp time = new Timestamp(System.currentTimeMillis());
		if (house.getSellStarted() == null)
		{
			// dont overwrite grace start time
			house.setSellStarted(time);
		}
		
		house.save();
		return DAOManager.getDAO(HouseBidsDAO.class).addBid(0, house.getObjectId(), initialPrice, time);
	}
	
	/**
	 * Removes a house from the current auction process.<br>
	 * This method updates the {@code HouseStatus} and handles bid refunds.<br>
	 * It also removes all associated bids from the internal tracking systems.
	 * @param house The {@code House} object to be removed from the auction.
	 * @param noSale A boolean flag that determines if the status becomes {@code HouseStatus.NOSALE} or {@code HouseStatus.ACTIVE}.
	 * @return {@code true} if the house was successfully removed, otherwise {@code false}.
	 */
	public boolean removeHouseFromAuction(House house, boolean noSale)
	{
		if (house.getStatus() != HouseStatus.SELL_WAIT)
		{
			return false;
		}
		
		HouseBidEntry bidEntry = null;
		HouseBidEntry playerBid = null;
		Integer lastPlayer = null;
		
		synchronized (houseBids)
		{
			bidEntry = houseBids.remove(house.getObjectId());
			if (bidEntry == null)
			{
				return false;
			}
			
			lastPlayer = bidEntry.getLastBiddingPlayer();
			playerBid = playerBids.remove(lastPlayer);
		}
		
		synchronized (bidsByIndex)
		{
			bidsByIndex.remove(bidEntry.getEntryIndex());
		}
		
		PlayerCommonData pcd = null;
		if (house.getOwnerId() != 0)
		{
			// player put this house himself, refund everything
			if (house.isInGracePeriod())
			{
				house.setSellStarted(null);
			}
			
			pcd = getPlayerData(house.getOwnerId());
			MailFormatter.sendHouseAuctionMail(house, pcd, AuctionResult.CANCELED_BID, System.currentTimeMillis(), bidEntry.getBidPrice() + bidEntry.getRefundKinah());
			house.setStatus(HouseStatus.ACTIVE);
		}
		else
		{
			house.setStatus(noSale ? HouseStatus.NOSALE : HouseStatus.ACTIVE);
		}
		
		// return bid price only to the last bidder
		pcd = getPlayerData(lastPlayer);
		MailFormatter.sendHouseAuctionMail(house, pcd, AuctionResult.CANCELED_BID, System.currentTimeMillis(), playerBid.getBidPrice());
		
		DAOManager.getDAO(HouseBidsDAO.class).deleteHouseBids(house.getObjectId());
		house.save();
		
		return true;
	}
	
	/**
	 * Places a bid for a house in an auction.<br>
	 * This method validates the player's eligibility and funds before updating the bid.<br>
	 * It also handles time prolongation and notifies previous bidders if they are outbid.
	 * @param player The {@code Player} attempting to place the bid.
	 * @param entryIndex The index of the auction entry being bid on.
	 * @param bidOffer The amount of Kinah offered for the house.
	 */
	public synchronized void placeBid(Player player, int entryIndex, long bidOffer)
	{
		// prevent this earlier (problem are house signs which allow bidding)
		if ((player.getBuildingOwnerStates() & PlayerHouseOwnerFlags.BIDDING_ALLOWED.getId()) == 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(player.getRace() == Race.ELYOS ? 18802 : 28802));
			return;
		}
		
		final int minutesLeft = getMinutesTillAuction();
		if (minutesLeft == 0)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_TIMEOUT);
			return;
		}
		
		final HouseBidEntry entry = getBidByEntryIndex(entryIndex);
		if (entry == null)
		{
			return;
		}
		
		if (player.getInventory().getKinah() < bidOffer)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
			return;
		}
		
		// Server side check for own house. Client side exists, but to be sure
		final House bidHouse = HousingService.getInstance().getHouseByAddress(entry.getAddress());
		if (player.getObjectId() == bidHouse.getOwnerId())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_MY_HOUSE);
			return;
		}
		
		final int playerAddress = HousingService.getInstance().getPlayerAddress(player.getObjectId());
		final House playerHouse = HousingService.getInstance().getHouseByAddress(playerAddress);
		if ((playerHouse != null) && playerHouse.isInGracePeriod())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_GRACE_HOUSE);
			return;
		}
		
		final int minLevel = getMinBidLevel(player, entry.getMapId(), entry.getLandId());
		if (minLevel > player.getLevel())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_LOW_LEVEL(minLevel));
			return;
		}
		
		if ((playerHouse != null) && !playerHouse.isFeePaid() && HousingConfig.ENABLE_HOUSE_PAY)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_OVERDUE);
			return;
		}
		
		if ((bidOffer - entry.getBidPrice()) >= ((entry.getBidPrice() * HousingConfig.HOUSE_AUCTION_BID_LIMIT) / 100f))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_EXCESS_ACCOUNT);
			return;
		}
		
		final HouseBidEntry currentBid = playerBids.get(player.getObjectId());
		if (currentBid != null)
		{
			if (entry.getLastBiddingPlayer() == player.getObjectId())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_SUCC_BID_HOUSE);
				return;
			}
			
			final HouseBidEntry houseBid = getBidByEntryIndex(currentBid.getEntryIndex());
			if (houseBid.getBidPrice() == currentBid.getBidPrice())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_OTHER_HOUSE);
				return;
			}
		}
		
		if ((minutesLeft < 5) && (timeProlonged < 30))
		{
			timeProlonged += 5;
			
			// save time
			ThreadPoolManager.getInstance().execute(() -> DAOManager.getDAO(ServerVariablesDAO.class).store("auctionProlonged", timeProlonged));
		}
		else if (!isBiddingAllowed())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_BID_TIMEOUT);
			return;
		}
		
		if ((bidOffer > entry.getBidPrice()) || (entry.getBidCount() == 0))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_BID_SUCCESS(entry.getAddress()));
			
			final Timestamp time = new Timestamp(System.currentTimeMillis());
			
			player.getInventory().decreaseKinah(bidOffer);
			
			final int previousPlayer = entry.getLastBiddingPlayer();
			
			if (previousPlayer > 0)
			{
				final PlayerCommonData prevPcd = getPlayerData(previousPlayer);
				if (prevPcd.isOnline())
				{
					PacketSendUtility.sendPacket(prevPcd.getPlayer(), SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_BID_CANCEL);
				}
				
				MailFormatter.sendHouseAuctionMail(bidHouse, prevPcd, AuctionResult.FAILED_BID, time.getTime(), entry.getBidPrice());
			}
			
			entry.incrementBidCount();
			entry.setLastBiddingPlayer(player.getObjectId());
			entry.setLastBidTime(time.getTime());
			entry.setBidPrice(bidOffer);
			
			final HouseBidEntry playerBid = (HouseBidEntry) entry.Clone();
			playerBids.put(player.getObjectId(), playerBid);
			
			DAOManager.getDAO(HouseBidsDAO.class).addBid(player.getObjectId(), bidHouse.getObjectId(), bidOffer, time);
			
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_PRICE_CHANGE(bidOffer));
			PacketSendUtility.sendPacket(player, new SM_RECEIVE_BIDS(0));
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It checks for new system letters related to housing auctions and sends relevant notifications.<br>
	 * If any auction results are found, it refreshes the player's bid data.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		if (player.getMailbox() == null)
		{
			return;
		}
		
		List<Letter> letters = player.getMailbox().getNewSystemLetters("$$HS_AUCTION_MAIL");
		boolean needsRefresh = false;
		
		for (Letter letter : letters)
		{
			final String[] titleParts = letter.getTitle().split(",");
			final String[] bodyParts = letter.getMessage().split(",");
			final AuctionResult result = AuctionResult.getResultFromId(Integer.parseInt(titleParts[0]));
			if (result == AuctionResult.FAILED_BID)
			{
				needsRefresh = true;
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_BID_CANCEL);
			}
			else if ((result == AuctionResult.WIN_BID) || (result == AuctionResult.GRACE_START))
			{
				needsRefresh = true;
				final int address = Integer.parseInt(bodyParts[1]);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_BID_WIN(address));
			}
			else if (result == AuctionResult.FAILED_SALE)
			{
				needsRefresh = true;
				final int address = Integer.parseInt(bodyParts[1]);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_AUCTION_FAIL(address));
			}
			else if ((result == AuctionResult.SUCCESS_SALE) || (result == AuctionResult.GRACE_SUCCESS))
			{
				needsRefresh = true;
				final int address = Integer.parseInt(bodyParts[1]);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_AUCTION_SUCCESS(address));
			}
		}
		
		if (needsRefresh)
		{
			PacketSendUtility.sendPacket(player, new SM_RECEIVE_BIDS(0));
		}
		
		letters = player.getMailbox().getNewSystemLetters("$$HS_OVERDUE_");
		for (Letter letter : letters)
		{
			if (letter.getSenderName().endsWith("FINAL") || letter.getSenderName().endsWith("3RD"))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_SEQUESTRATE);
			}
			else
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OVERDUE);
			}
		}
	}
	
	/**
	 * Retrieves a specific bid entry for a house based on its unique ID.<br>
	 * This method uses {@code synchronized} to ensure thread safety while accessing the internal map.
	 * @param houseObjectId The unique identifier of the house to look up.
	 * @return The {@link HouseBidEntry} associated with the provided ID, or {@code null} if not found.
	 */
	public HouseBidEntry getHouseBid(int houseObjectId)
	{
		synchronized (houseBids)
		{
			return houseBids.get(houseObjectId);
		}
	}
	
	/**
	 * Retrieves a list of house bid entries for a specific race.<br>
	 * This method filters the {@code houseBids} based on whether the land is Elyos or Asmodian.<br>
	 * It ensures that players only see bids relevant to their own faction.
	 * @param playerRace The {@link Race} of the player requesting the data.
	 * @return A list of {@link HouseBidEntry} objects visible to the specified race.
	 */
	public List<HouseBidEntry> getHouseBidEntries(Race playerRace)
	{
		synchronized (houseBids)
		{
			final List<HouseBidEntry> bids = new ArrayList<>();
			for (HouseBidEntry bid : houseBids.values())
			{
				final HousingLand land = DataManager.HOUSE_DATA.getLand(bid.getLandId());
				final boolean isEly = DataManager.NPC_DATA.getNpcTemplate(land.getManagerNpcId()).getTribe() == TribeClass.GENERAL;
				if (isEly && (playerRace == Race.ELYOS))
				{
					bids.add(bid);
				}
				else if (!isEly && (playerRace == Race.ASMODIANS))
				{
					bids.add(bid);
				}
			}
			
			return bids;
		}
	}
	
	/**
	 * Retrieves the most recent bid made by a specific player.<br>
	 * This method looks up the {@code HouseBidEntry} associated with the provided ID.
	 * @param playerId The unique identifier of the player to search for.
	 * @return The {@code HouseBidEntry} object if found, or {@code null} if no bid exists.
	 */
	public HouseBidEntry getLastPlayerBid(int playerId)
	{
		return playerBids.get(playerId);
	}
	
	/**
	 * Retrieves a specific bid entry from the internal list.<br>
	 * This method uses the provided {@code index} to find the corresponding {@link HouseBidEntry}.
	 * @param index The position of the bid in the collection.
	 * @return The {@code HouseBidEntry} at the specified position, or {@code null} if not found.
	 */
	public HouseBidEntry getBidByEntryIndex(int index)
	{
		synchronized (bidsByIndex)
		{
			return bidsByIndex.get(index);
		}
	}
	
	/**
	 * Calculates the minimum level required for a player to place a bid.<br>
	 * This method checks specific map restrictions based on the {@code Race}.<br>
	 * It also considers the house type defined in {@link HousingConfig}.<br>
	 * If no specific configuration is found, it defaults to the land sale options.
	 * @param player The {@code Player} attempting to place a bid.
	 * @param mapId The unique identifier for the current map.
	 * @param landId The unique identifier for the housing land.
	 * @return The minimum level required as an {@code int}.
	 */
	private static int getMinBidLevel(Player player, int mapId, int landId)
	{
		final HousingLand land = DataManager.HOUSE_DATA.getLand(landId);
		final Sale saleOptions = land.getSaleOptions();
		
		// all types are the same for the land, take the first
		final HouseType houseType = HouseType.fromValue(land.getBuildings().get(0).getSize());
		
		if (player.getRace() == Race.ELYOS)
		{
			if ((mapId == WorldMapType.HEIRON.getId()) || (mapId == WorldMapType.INGGISON.getId()))
			{
				return saleOptions.getMinLevel();
			}
		}
		else if ((mapId == WorldMapType.BELUSLAN.getId()) || (mapId == WorldMapType.GELKMAROS.getId()))
		{
			return saleOptions.getMinLevel();
		}
		
		switch (houseType)
		{
			case HOUSE:
				if (HousingConfig.HOUSE_MIN_BID_LEVEL > 0)
				{
					return HousingConfig.HOUSE_MIN_BID_LEVEL;
				}
				break;
			case MANSION:
				if (HousingConfig.MANSION_MIN_BID_LEVEL > 0)
				{
					return HousingConfig.MANSION_MIN_BID_LEVEL;
				}
				break;
			case ESTATE:
				if (HousingConfig.ESTATE_MIN_BID_LEVEL > 0)
				{
					return HousingConfig.ESTATE_MIN_BID_LEVEL;
				}
				break;
			case PALACE:
				if (HousingConfig.PALACE_MIN_BID_LEVEL > 0)
				{
					return HousingConfig.PALACE_MIN_BID_LEVEL;
				}
				break;
			default:
				break;
		}
		
		return saleOptions.getMinLevel();
	}
	
	/**
	 * Checks if a {@link Player} meets the level requirement to bid on a house.<br>
	 * This method compares the player's current level against the minimum required level.
	 * @param player The {@link Player} attempting to place a bid.
	 * @param mapId The unique identifier for the map where the land is located.
	 * @param landId The unique identifier for the specific land being auctioned.
	 * @return {@code true} if the player's level is sufficient, {@code false} otherwise.
	 */
	public static boolean canBidHouse(Player player, int mapId, int landId)
	{
		return player.getLevel() >= getMinBidLevel(player, mapId, landId);
	}
}
