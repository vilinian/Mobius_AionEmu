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
package com.aionemu.gameserver.model.gameobjects.player;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.StaticData;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.Petition;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.atreianpassport.AtreianPassportTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SILVER_STAR;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_DP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.XPLossEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This class holds the basic information for a player.<br>
 * It contains data that can be accessed even when the player is not online.
 * @author Luno
 * @modified cura
 */
public class PlayerCommonData extends VisibleObjectTemplate
{
	/**
	 * Logger used by this class and {@link StaticData} class
	 */
	static Logger log = LoggerFactory.getLogger(PlayerCommonData.class);
	private final int playerObjId;
	private Race race;
	private String name;
	private PlayerClass playerClass;
	/**
	 * Should be changed right after character creation *
	 */
	private int level = 0;
	private long exp = 0;
	private long expRecoverable = 0;
	private Gender gender;
	private Timestamp lastOnline = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
	private Timestamp lastStamp = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
	private boolean online;
	private String note;
	private WorldPosition position;
	private int cubeExpands = 0;
	private int warehouseSize = 0;
	private int AdvancedStigmaSlotSize = 0;
	private int titleId = -1;
	private int bonusTitleId = -1;
	private int dp = 0;
	private int mailboxLetters;
	private int soulSickness = 0;
	private boolean noExp = false;
	private long reposteCurrent;
	private long reposteMax;
	private long goldenStarEnergy;
	private final long goldenStarEnergyMax = 625000000;
	private boolean GoldenStarBoost = false;
	private long silverStarEnergy;
	private final long silverStarEnergyMax = 1000000;
	private boolean SilverStarBoost = false;
	private long growthEnergy;
	private long growthEnergyMax;
	private long salvationPoint;
	private int mentorFlagTime;
	private int worldOwnerId;
	private BoundRadius boundRadius;
	private long lastTransferTime;
	public int battleGroundPoints = 0;
	private int initialGameStatsDatabase = 0;
	private int fatigue = 0;
	private int fatigueRecover = 0;
	private int fatigueReset = 0;
	private int joinRequestLegionId = 0;
	private LegionJoinRequestState joinRequestState = LegionJoinRequestState.NONE;
	private PlayerUpgradeArcade upgradeArcade;
	
	private final PlayerBonusTime bonusTime = new PlayerBonusTime();
	private Timestamp creationDate;
	
	private int lunaCoins = 0;
	private int wardrobeSize = 256;
	private int lunaConsumePoint;
	private int muni_keys;
	private int consumeCount = 0;
	private int wardrobeSlot;
	private int floor;
	private int minionEnergy;
	private int lastMinion;
	private Timestamp minionFunctionTime;
	private int worldPlayTime;
	
	// Shugo Sweep 5.1
	private int goldenDice;
	private int resetBoard;
	
	// Atreian Passport
	private int stamps = 0;
	private int passportReward = 0;
	public Map<Integer, AtreianPassport> atreianPassports = new HashMap<>(1);
	private AtreianPassport completedPassports;
	
	// TODO: Move all function to playerService or Player class.
	/**
	 * Creates a new instance of {@link PlayerCommonData}.<br>
	 * This constructor initializes the player with a specific unique identifier.
	 * @param objId The unique identification number for the player.
	 */
	public PlayerCommonData(int objId)
	{
		playerObjId = objId;
	}
	
	/**
	 * Retrieves the unique object identifier for the player.<br>
	 * This value is used to identify which player created the {@link Petition}.
	 * @return The {@code int} value of the player's object ID.
	 */
	public int getPlayerObjId()
	{
		return playerObjId;
	}
	
	/**
	 * Retrieves the current experience points of the player.<br>
	 * This value represents the total accumulated {@code exp}.
	 * @return The current experience points as a {@code long}.
	 */
	public long getExp()
	{
		return exp;
	}
	
	/**
	 * Retrieves the number of cube expansions for the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The total count of cube expansions as an {@code int}.
	 */
	public int getCubeExpands()
	{
		return cubeExpands;
	}
	
	/**
	 * Updates the cube expansion value for the player.<br>
	 * This method also increases the inventory limit based on the {@code cubeExpands} amount.
	 * @param cubeExpands The number of cube expansions to apply.
	 */
	public void setCubeExpands(int cubeExpands)
	{
		this.cubeExpands = cubeExpands;
	}
	
	/**
	 * Retrieves the current size of the advanced stigma slots.<br>
	 * This value is stored in the {@code AdvancedStigmaSlotSize} field.
	 * @return The integer size of the advanced stigma slots.
	 */
	public int getAdvancedStigmaSlotSize()
	{
		return AdvancedStigmaSlotSize;
	}
	
	/**
	 * Sets the size of the advanced stigma slot.<br>
	 * This updates the {@code AdvancedStigmaSlotSize} field for the player.
	 * @param AdvancedStigmaSlotSize The new size to assign to the advanced stigma slot.
	 */
	public void setAdvancedStigmaSlotSize(int AdvancedStigmaSlotSize)
	{
		this.AdvancedStigmaSlotSize = AdvancedStigmaSlotSize;
	}
	
	/**
	 * Retrieves the amount of experience points currently shown to the player.<br>
	 * This value represents the difference between total experience and the starting experience for the current level.
	 * @return The calculated experience points shown.
	 */
	public long getExpShown()
	{
		return exp - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level);
	}
	
	/**
	 * Calculates the amount of experience required to reach the next level.<br>
	 * It checks the current {@code level} against the maximum level in {@link DataManager}.<br>
	 * If the player is at the maximum level, it returns {@code 0}.<br>
	 * Otherwise, it returns the difference between the start experience of the next level and the current level.
	 * @return The amount of experience needed for the next level as a {@code long}.
	 */
	public long getExpNeed()
	{
		if (level == DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel())
		{
			return 0;
		}
		
		return DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level + 1) - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level);
	}
	
	/**
	 * Calculates and updates the player's experience loss.<br>
	 * This method determines how much experience is lost based on the current level.<br>
	 * It splits the loss into recoverable and unrecoverable portions.<br>
	 * It also sends a {@link com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP} packet to the player if they are online.
	 */
	public void calculateExpLoss()
	{
		final long expLost = XPLossEnum.getExpLoss(level, getExpNeed());
		
		final int unrecoverable = (int) (expLost * 0.33333333);
		final int recoverable = (int) expLost - unrecoverable;
		final long allExpLost = recoverable + expRecoverable;
		
		if (getExpShown() > unrecoverable)
		{
			exp = exp - unrecoverable;
		}
		else
		{
			exp = exp - getExpShown();
		}
		
		if (getExpShown() > recoverable)
		{
			expRecoverable = allExpLost;
			exp = exp - recoverable;
		}
		else
		{
			expRecoverable = expRecoverable + getExpShown();
			exp = exp - getExpShown();
		}
		
		if (expRecoverable > (getExpNeed() * 0.25))
		{
			expRecoverable = Math.round(getExpNeed() * 0.25);
		}
		
		if (getPlayer() != null)
		{
			PacketSendUtility.sendPacket(getPlayer(), new SM_STATUPDATE_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), getCurrentReposteEnergy(), getMaxReposteEnergy(), getGoldenStarEnergy(), getGrowthEnergy()));
		}
	}
	
	/**
	 * Sets the amount of experience that can be recovered.<br>
	 * This updates the {@code expRecoverable} field in the player data.
	 * @param expRecoverable The new amount of recoverable experience.
	 */
	public void setRecoverableExp(long expRecoverable)
	{
		this.expRecoverable = expRecoverable;
	}
	
	/**
	 * Resets the recoverable experience to zero.<br>
	 * This method adds the current {@code expRecoverable} value to the total {@code exp}.<br>
	 * It then sets {@code expRecoverable} to {@code 0}.
	 */
	public void resetRecoverableExp()
	{
		final long el = expRecoverable;
		expRecoverable = 0;
		setExp(exp + el);
	}
	
	/**
	 * Retrieves the amount of experience that can be recovered.<br>
	 * This value is stored in the {@code expRecoverable} field.
	 * @return The current recoverable experience as a {@code long}.
	 */
	public long getExpRecoverable()
	{
		return expRecoverable;
	}
	
	/**
	 * Adds a specific amount of experience to the player.<br>
	 * This method updates the player's total experience based on an NPC identifier.
	 * @param value The amount of experience to add.
	 * @param npcNameId The unique identifier for the NPC providing the experience.
	 */
	public void addExp(long value, int npcNameId)
	{
		this.addExp(value, null, npcNameId, "", 0);
	}
	
	/**
	 * Adds a specific amount of experience to the player.<br>
	 * This method updates the player's total experience based on the provided {@code RewardType}.
	 * @param value The amount of experience to add.
	 * @param rewardType The type of reward being granted.
	 */
	public void addExp(long value, RewardType rewardType)
	{
		this.addExp(value, rewardType, 0, "", 0);
	}
	
	/**
	 * Adds experience points to the player.<br>
	 * This method updates the total experience based on a specific reward type and NPC.
	 * @param value The amount of experience to add.
	 * @param rewardType The category of the reward being granted.
	 * @param npcNameId The unique identifier for the NPC providing the reward.
	 */
	public void addExp(long value, RewardType rewardType, int npcNameId)
	{
		this.addExp(value, rewardType, npcNameId, "", 0);
	}
	
	/**
	 * Adds experience points to the player.<br>
	 * This method updates the player's total experience based on a specific reward type.<br>
	 * It tracks the source of the reward using an NPC and a quest ID.
	 * @param value The amount of experience to add.
	 * @param rewardType The category of the reward being granted.
	 * @param npcNameId The unique identifier for the NPC providing the reward.
	 * @param questId The unique identifier for the quest providing the reward.
	 */
	public void addExp(long value, RewardType rewardType, int npcNameId, int questId)
	{
		this.addExp(value, rewardType, npcNameId, "", questId);
	}
	
	/**
	 * Adds experience points to the player.<br>
	 * This method updates the total experience based on a specific reward type and a display name.
	 * @param value The amount of experience to add.
	 * @param rewardType The category of the reward being granted.
	 * @param name The descriptive name for the source of the experience.
	 */
	public void addExp(long value, RewardType rewardType, String name)
	{
		this.addExp(value, rewardType, 0, name, 0);
	}
	
	/**
	 * Adds experience points to the player based on various rewards and energy bonuses.<br>
	 * This method calculates final rewards including Repose, Salvation, Golden Star, Growth, and Silver Star energies.<br>
	 * It also sends the appropriate system messages to the player depending on the {@code RewardType}.
	 * @param value The base amount of experience to add.
	 * @param rewardType The category of the reward which determines bonus calculations.
	 * @param npcNameId The unique identifier for the NPC providing the reward.
	 * @param name The display name used for PVP kill messages.
	 * @param questId The unique identifier for the quest being completed.
	 */
	public void addExp(long value, RewardType rewardType, int npcNameId, String name, int questId)
	{
		if (noExp)
		{
			return;
		}
		
		if (CustomConfig.ENABLE_EXP_CAP)
		{
			value = value > CustomConfig.EXP_CAP_VALUE ? CustomConfig.EXP_CAP_VALUE : value;
		}
		
		long reward = value;
		if ((getPlayer() != null) && (rewardType != null))
		{
			reward = rewardType.calcReward(getPlayer(), value);
		}
		
		long repose = 0;
		if (isReadyForReposteEnergy() && (getCurrentReposteEnergy() > 0))
		{
			repose = (long) ((reward / 100f) * 40); // 40% bonus
			addReposteEnergy(-repose);
		}
		
		long salvation = 0;
		if (isReadyForSalvationPoints() && (getCurrentSalvationPercent() > 0))
		{
			salvation = (long) ((reward / 100f) * getCurrentSalvationPercent());
			// TODO! remove salvation points?
		}
		
		long goldenstar = 0;
		long goldenstarboost = 0;
		if (isReadyForGoldenStarEnergy() && (getGoldenStarEnergy() > 0))
		{
			goldenstar = (reward);
			addGoldenStarEnergy(-goldenstar);
			if (GoldenStarBoost)
			{
				goldenstarboost = (long) ((reward / 100f) * 20);
			}
		}
		
		long growth = 0;
		if (isReadyForGrowthEnergy() && (getGrowthEnergy() > 0))
		{
			growth = (long) ((reward / 100f) * 60);
			addGrowthEnergy(-growth * 5); // reduce
		}
		
		long silverstar = 0;
		long silverstarBoost = 0;
		if (isReadyForSilverStarEnergy() && (getSilverStarEnergy() > 0))
		{
			silverstar = reward;
			addSilverStarEnergy(-silverstar);
			if (SilverStarBoost)
			{
				silverstarBoost = (long) ((reward / 100f) * 50);
			}
		}
		
		if (getPlayer() != null)
		{
			if (rewardType != null)
			{
				if (getPlayer().getPosition().getMapId() != 302400000)
				{
					// TowerOfChallenge
					if ((rewardType == RewardType.HUNTING) || (rewardType == RewardType.GROUP_HUNTING) || (rewardType == RewardType.CRAFTING) || (rewardType == RewardType.GATHERING) || (rewardType == RewardType.MONSTER_BOOK))
					{
						reward += repose + goldenstar + goldenstarboost + silverstar + silverstarBoost + growth;
					}
					else
					{
						reward += repose;
					}
				}
				else
				{
					reward += 0;
				}
			}
		}
		
		setExp(exp + reward);
		if (getPlayer() != null)
		{
			if (rewardType != null)
			{
				switch (rewardType)
				{
					case HUNTING:
					case GROUP_HUNTING:
					case CRAFTING:
					case GATHERING:
						if (npcNameId == 0)
						{// Exeption quest w/o reward npc You have gained %num1 XP.
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2(reward));
						}
						
						PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId((npcNameId * 2) + 1), reward));
						if (repose > 0)
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(2805577), repose));
						}
						
						if (growth > 0)
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(2806377), growth));
						}
						
						if (goldenstar > 0)
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(2806671), goldenstar));
						}
						break;
					case QUEST:
						if (npcNameId == 0) // Exeption quest w/o reward npc
						
						// You have gained %num1 XP.
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2(reward));
						}
						else if ((repose > 0) && (salvation > 0)) // You have gained %num1 XP from %0 (Energy of Repose %num2, Energy of Salvation %num3).
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_MAKEUP_BONUS_DESC(new DescriptionId((npcNameId * 2) + 1), reward, repose, salvation));
						}
						else if ((repose > 0) && (salvation == 0)) // You have gained %num1 XP from %0 (Energy of Repose %num2).
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_BONUS_DESC(new DescriptionId((npcNameId * 2) + 1), reward, repose));
						}
						else if ((repose == 0) && (salvation > 0)) // You have gained %num1 XP from %0 (Energy of Salvation %num2).
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_MAKEUP_BONUS_DESC(new DescriptionId((npcNameId * 2) + 1), reward, salvation));
						}
						else // You have gained %num1 XP from %0.
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId((npcNameId * 2) + 1), reward));
						}
						break;
					case PVP_KILL:
						if ((repose > 0) && (salvation > 0)) // You have gained %num1 XP from %0 (Energy of Repose %num2, Energy of Salvation %num3).
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_MAKEUP_BONUS(name, reward, repose, salvation));
						}
						else if ((repose > 0) && (salvation == 0)) // You have gained %num1 XP from %0 (Energy of Repose %num2).
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_BONUS(name, reward, repose));
						}
						else if ((repose == 0) && (salvation > 0)) // You have gained %num1 XP from %0 (Energy of Salvation %num2).
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_MAKEUP_BONUS(name, reward, salvation));
						}
						else // You have gained %num1 XP from %0.
						{
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP(name, reward));
						}
						break;
					default:
						break;
				}
			}
		}
	}
	
	/**
	 * Checks if the player is eligible to receive salvation points.<br>
	 * The player must be at least level {@code 15}.<br>
	 * The player must also be below the maximum level defined in {@link GSConfig}.
	 * @return {@code true} if the player meets the requirements, otherwise {@code false}.
	 */
	public boolean isReadyForSalvationPoints()
	{
		return (level >= 15) && (level < (GSConfig.PLAYER_MAX_LEVEL + 1));
	}
	
	/**
	 * Checks if the player can receive energy. <br>
	 * This is allowed only if the player's level is {@code 10} or higher.
	 * @return {@code true} if the player meets the level requirement, {@code false} otherwise.
	 */
	public boolean isReadyForReposteEnergy()
	{
		return level >= 10;
	}
	
	/**
	 * Increases the current Reposte Energy of the player.<br>
	 * This method checks if the player is eligible to receive energy first.<br>
	 * The value is clamped between 0 and the maximum allowed amount.
	 * @param add The amount of energy to add to the player.
	 */
	public void addReposteEnergy(long add)
	{
		if (!isReadyForReposteEnergy())
		{
			return;
		}
		
		reposteCurrent += add;
		if (reposteCurrent < 0)
		{
			reposteCurrent = 0;
		}
		else if (reposteCurrent > getMaxReposteEnergy())
		{
			reposteCurrent = getMaxReposteEnergy();
		}
	}
	
	/**
	 * Updates the maximum reposte energy value for the player.<br>
	 * This method checks if the player is ready for reposte energy.<br>
	 * If not ready, both current and max values are set to {@code 0}.<br>
	 * Otherwise, it calculates {@codeposteMax} as 25% of the required experience.
	 */
	public void updateMaxReposte()
	{
		if (!isReadyForReposteEnergy())
		{
			reposteCurrent = 0;
			reposteMax = 0;
		}
		else
		{
			reposteMax = (long) (getExpNeed() * 0.25f); // Retail 99%
		}
	}
	
	/**
	 * Updates the current amount of Reposte Energy for the player.<br>
	 * This method sets the {@code reproteCurrent} field to a new value.
	 * @param value The new energy value to set.
	 */
	public void setCurrentReposteEnergy(long value)
	{
		reposteCurrent = value;
	}
	
	/**
	 * Retrieves the current amount of Reposte Energy for the player.<br>
	 * This method checks if the player is ready to use the energy first.<br>
	 * If they are not ready, it returns {@code 0}.
	 * @return The current Reposte Energy value as a {@code long}.
	 */
	public long getCurrentReposteEnergy()
	{
		return isReadyForReposteEnergy() ? reposteCurrent : 0;
	}
	
	/**
	 * Retrieves the maximum amount of Reposte Energy for the player.<br>
	 * This method checks if the player is eligible to have energy.<br>
	 * If they are not ready, it returns {@code 0}.
	 * @return The maximum Reposte Energy value or {@code 0} if ineligible.
	 */
	public long getMaxReposteEnergy()
	{
		return isReadyForReposteEnergy() ? reposteMax : 0;
	}
	
	/**
	 * Checks if the player meets the requirements for Golden Star Energy.<br>
	 * The player must reach at least level {@code 10}.
	 * @return {@code true} if the player is level 10 or higher, {@code false} otherwise.
	 */
	public boolean isReadyForGoldenStarEnergy()
	{
		return level >= 10;
	}
	
	/**
	 * Adds a specific amount of energy to the player's Golden Star. <br>
	 * This method checks if the player is ready for energy before updating.<br>
	 * It ensures the final value stays between 0 and the maximum allowed limit.<br>
	 * Finally, it triggers a check on the current percentage.
	 * @param add The amount of energy to add.
	 */
	public void addGoldenStarEnergy(long add)
	{
		if (!isReadyForGoldenStarEnergy())
		{
			return;
		}
		
		goldenStarEnergy += add;
		if (goldenStarEnergy < 0)
		{
			goldenStarEnergy = 0;
		}
		else if (goldenStarEnergy > getMaxGoldenStarEnergy())
		{
			goldenStarEnergy = getMaxGoldenStarEnergy();
		}
		
		checkGoldenStarPercent();
	}
	
	/**
	 * Sets the amount of golden star energy for the player.<br>
	 * If the provided {@code value} is less than {@code 0}, it will be set to {@code 0}.<br>
	 * This method also triggers a check for the golden star percentage.
	 * @param value The new energy value to assign.
	 */
	public void setGoldenStarEnergy(long value)
	{
		goldenStarEnergy = value;
		if (goldenStarEnergy < 0)
		{
			goldenStarEnergy = 0;
		}
		
		checkGoldenStarPercent();
	}
	
	/**
	 * Retrieves the current energy amount for the Golden Star.<br>
	 * This method checks if the player is ready to receive energy first.<br>
	 * If the player is not ready, it returns {@code 0}.
	 * @return The amount of golden star energy as a {@code long}.
	 */
	public long getGoldenStarEnergy()
	{
		return isReadyForGoldenStarEnergy() ? goldenStarEnergy : 0;
	}
	
	/**
	 * Retrieves the maximum energy for a Golden Star.<br>
	 * This method checks if the player is ready for Golden Star energy first.<br>
	 * If they are not ready, it returns {@code 0}.
	 * @return The maximum Golden Star energy value or {@code 0} if not ready.
	 */
	public long getMaxGoldenStarEnergy()
	{
		return isReadyForGoldenStarEnergy() ? goldenStarEnergyMax : 0;
	}
	
	/**
	 * Checks the current percentage of golden star energy.<br>
	 * It updates the {@code GoldenStarBoost} status based on a 50 percent threshold.<br>
	 * This method sends system messages to the player when the boost state changes or energy is empty.
	 */
	public void checkGoldenStarPercent()
	{
		if (getPlayer() != null)
		{
			if (isReadyForGoldenStarEnergy())
			{
				final int percent = (int) ((goldenStarEnergy * 100f) / getMaxGoldenStarEnergy());
				if (!GoldenStarBoost && (percent > 50))
				{
					GoldenStarBoost = true;
					PacketSendUtility.sendPacket(getPlayer(), new SM_SYSTEM_MESSAGE(1403399, 50));
				}
				else if (GoldenStarBoost && (percent < 50))
				{
					GoldenStarBoost = false;
					PacketSendUtility.sendPacket(getPlayer(), new SM_SYSTEM_MESSAGE(1403400, 50));
				}
				else if (goldenStarEnergy <= 0)
				{
					PacketSendUtility.sendPacket(getPlayer(), new SM_SYSTEM_MESSAGE(1403401));
				}
			}
		}
	}
	
	/**
	 * Checks if the player meets the requirements for Silver Star Energy.<br>
	 * This method verifies if the player's level is at least {@code 45}.
	 * @return {@code true} if the player is ready, otherwise {@code false}.
	 */
	public boolean isReadyForSilverStarEnergy()
	{
		return level >= 45;
	}
	
	/**
	 * Increases the current silver star energy of the player.<br>
	 * This method checks if the player is eligible to receive energy first.<br>
	 * It ensures the final value stays between 0 and the maximum allowed limit.<br>
	 * Finally, it triggers a check for the silver star percentage.
	 * @param add The amount of energy to add.
	 */
	public void addSilverStarEnergy(long add)
	{
		if (!isReadyForSilverStarEnergy())
		{
			return;
		}
		
		silverStarEnergy += add;
		if (silverStarEnergy < 0)
		{
			silverStarEnergy = 0;
		}
		else if (silverStarEnergy > getMaxSilverStarEnergy())
		{
			silverStarEnergy = getMaxSilverStarEnergy();
		}
		
		checkSilverStarPercent();
	}
	
	/**
	 * Sets the current energy level for the Silver Star.<br>
	 * This method updates the {@code silverStarEnergy} field.<br>
	 * It also triggers a check of the Silver Star percentage.
	 * @param value The new energy value to assign.
	 */
	public void setSilverStarEnergy(long value)
	{
		silverStarEnergy = value;
		checkSilverStarPercent();
	}
	
	/**
	 * Retrieves the current energy for the Silver Star.<br>
	 * This method checks if the player is ready to receive energy first.<br>
	 * If the player is not ready, it returns {@code 0}.
	 * @return The amount of Silver Star energy as a {@code long}.
	 */
	public long getSilverStarEnergy()
	{
		return isReadyForSilverStarEnergy() ? silverStarEnergy : 0;
	}
	
	/**
	 * Retrieves the maximum energy for a Silver Star.<br>
	 * This method checks if the player is ready for Silver Star energy.<br>
	 * If they are not ready, it returns {@code 0}.
	 * @return The maximum silver star energy value or {@code 0} if not ready.
	 */
	public long getMaxSilverStarEnergy()
	{
		return isReadyForSilverStarEnergy() ? silverStarEnergyMax : 0;
	}
	
	/**
	 * Checks the current percentage of silver star energy.<br>
	 * It updates the {@code SilverStarBoost} status based on the energy level.<br>
	 * This method sends a packet to the player if the boost state changes or energy is empty.
	 */
	public void checkSilverStarPercent()
	{
		if ((getPlayer() != null) && (isReadyForSilverStarEnergy()))
		{
			final int percent = (int) ((silverStarEnergy * 100.0) / getMaxSilverStarEnergy());
			if (!SilverStarBoost && (percent > 50))
			{
				SilverStarBoost = true;
				PacketSendUtility.sendPacket(getPlayer(), new SM_SILVER_STAR());
				PacketSendUtility.sendPacket(getPlayer(), new SM_SYSTEM_MESSAGE(1404029, 50));
			}
			else if (SilverStarBoost && (percent < 50))
			{
				SilverStarBoost = false;
				PacketSendUtility.sendPacket(getPlayer(), new SM_SILVER_STAR());
				PacketSendUtility.sendPacket(getPlayer(), new SM_SYSTEM_MESSAGE(1404030, 50));
			}
			else if (silverStarEnergy <= 0)
			{
				PacketSendUtility.sendPacket(getPlayer(), new SM_SILVER_STAR());
				PacketSendUtility.sendPacket(getPlayer(), new SM_SYSTEM_MESSAGE(1404031, 50));
				
			}
		}
	}
	
	/**
	 * Checks if the player is eligible to receive growth energy.<br>
	 * The player must be at least level {@code 66}.<br>
	 * The player must also be below the maximum level defined in {@link GSConfig}.
	 * @return {@code true} if the player meets the requirements, {@code false} otherwise.
	 */
	public boolean isReadyForGrowthEnergy()
	{
		return (level >= 66) && (level < (GSConfig.PLAYER_MAX_LEVEL + 1));
	}
	
	/**
	 * Increases the player's growth energy by a specific amount.<br>
	 * This method checks if the player is ready to receive energy first.<br>
	 * It ensures the final value stays between 0 and the maximum allowed limit.
	 * @param add The amount of energy to add to the current total.
	 */
	public void addGrowthEnergy(long add)
	{
		if (!isReadyForGrowthEnergy())
		{
			return;
		}
		
		growthEnergy += add;
		if (growthEnergy < 0)
		{
			growthEnergy = 0;
		}
		else if (growthEnergy > getMaxGrowthEnergy())
		{
			growthEnergy = getMaxGrowthEnergy();
		}
	}
	
	/**
	 * Updates the {@code growthEnergyMax} value based on the player's current level.<br>
	 * This method checks if the player is eligible for growth energy first.<br>
	 * It calculates the maximum capacity using specific formulas for different level brackets.
	 */
	public void updateMaxGrowthEnergy()
	{
		if (!isReadyForGrowthEnergy())
		{
			growthEnergy = 0;
			growthEnergyMax = 0;
		}
		else
		{
			if (level < 70)
			{
				growthEnergyMax = (77000000 + (7000000 * (level - 66)));
			}
			else if (level == 70)
			{
				growthEnergyMax = 106000000;
			}
			else if (level == 71)
			{
				growthEnergyMax = 127000000;
			}
			else if (level < 75)
			{
				growthEnergyMax = (127000000 + (11000000 * (level - 71)));
			}
			else
			{
				growthEnergyMax = 175000000;
			}
		}
	}
	
	/**
	 * Sets the growth energy for the player.<br>
	 * This updates the {@code growthEnergy} field with a new value.
	 * @param value The new amount of growth energy to set.
	 */
	public void setGrowthEnergy(long value)
	{
		growthEnergy = value;
	}
	
	/**
	 * Retrieves the current amount of growth energy for the player.<br>
	 * This method checks if the player is eligible to receive growth energy first.<br>
	 * If they are not ready, it returns {@code 0}.
	 * @return The amount of growth energy as a {@code long} value.
	 */
	public long getGrowthEnergy()
	{
		return isReadyForGrowthEnergy() ? growthEnergy : 0;
	}
	
	/**
	 * Retrieves the maximum growth energy for the player.<br>
	 * This method checks if the player is ready for growth energy first.<br>
	 * If they are not ready, it returns {@code 0}.
	 * @return The maximum growth energy value or {@code 0} if not ready.
	 */
	public long getMaxGrowthEnergy()
	{
		return isReadyForGrowthEnergy() ? growthEnergyMax : 0;
	}
	
	/**
	 * Sets the experience points for the player.<br>
	 * This method updates the {@code level} based on the new value.<br>
	 * It also caps the experience at the maximum allowed amount.<br>
	 * If the player is online, it sends a status update packet.
	 * @param exp The new experience value to set.
	 */
	public void setExp(long exp)
	{
		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		final long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(maxLevel);
		if ((getPlayerClass() != null) && getPlayerClass().isStartingClass())
		{
			maxLevel = GSConfig.STARTING_LEVEL > GSConfig.STARTCLASS_MAXLEVEL ? GSConfig.STARTING_LEVEL : GSConfig.STARTCLASS_MAXLEVEL;
			if ((getLevel() == 9) && (getExp() >= 74059))
			{
				// You can advance to level 10 only after you have completed the class change quest.
				PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_LEVEL_LIMIT_QUEST_NOT_FINISHED1);
			}
		}
		
		if (exp > maxExp)
		{
			exp = maxExp;
		}
		
		final int oldLvl = level;
		this.exp = exp;
		
		// make sure level is never larger than maxLevel-1
		boolean up = false;
		while ((((level + 1) < maxLevel) && (up = exp >= DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level + 1))) || (((level - 1) >= 0) && (exp < DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level))))
		{
			if (up)
			{
				level++;
			}
			else
			{
				level--;
			}
			
			upgradePlayerData();
		}
		
		if (getPlayer() != null)
		{
			if (oldLvl != level)
			{
				updateMaxReposte();
				updateMaxGrowthEnergy();
			}
			
			PacketSendUtility.sendPacket(getPlayer(), new SM_STATUPDATE_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), getCurrentReposteEnergy(), getMaxReposteEnergy(), getGoldenStarEnergy(), getGrowthEnergy()));
		}
	}
	
	/**
	 * Updates the player's status and attributes.<br>
	 * This method calls {@code getController}<br>
	 * to perform a level or rank upgrade.<br>
	 * It also resets the salvation points for the player.
	 */
	private void upgradePlayerData()
	{
		final Player player = getPlayer();
		if (player != null)
		{
			player.getController().upgradePlayer();
			resetSalvationPoints();
		}
	}
	
	/**
	 * Sets whether the player should receive experience points.<br>
	 * Use {@code true} to disable experience gains.<br>
	 * Use {@code false} to allow normal experience gains.
	 * @param value The boolean state to set for experience gain.
	 */
	public void setNoExp(boolean value)
	{
		noExp = value;
	}
	
	/**
	 * Checks if the player has experience points disabled.<br>
	 * Returns {@code true} if experience is disabled.<br>
	 * Returns {@code false} if experience is enabled.
	 * @return The status of the no-experience flag.
	 */
	public boolean getNoExp()
	{
		return noExp;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Returns the opposing faction for the current player.<br>
	 * If the player is {@code Race.ELYOS}, it returns {@code Race.ASMODIANS}.<br>
	 * Otherwise, it returns {@code Race.ELYOS}.
	 * @return The opposite {@link Race} type.
	 */
	public Race getOppositeRace()
	{
		return race == Race.ELYOS ? Race.ASMODIANS : Race.ELYOS;
	}
	
	/**
	 * Retrieves the current time for the mentor flag.<br>
	 * This value is used to track when a player's mentor status expires.
	 * @return The {@code int} value representing the mentor flag time.
	 */
	public int getMentorFlagTime()
	{
		return mentorFlagTime;
	}
	
	/**
	 * Checks if the player currently has an active mentor status.<br>
	 * It compares the {@code mentorFlagTime} against the current system time.
	 * @return {@code true} if the mentor flag is still active, {@code false} otherwise.
	 */
	public boolean isHaveMentorFlag()
	{
		return mentorFlagTime > (System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Updates the mentor flag time for the player.<br>
	 * This method sets the value of {@code mentorFlagTime}.
	 * @param mentorFlagTime The new time value to set.
	 */
	public void setMentorFlagTime(int mentorFlagTime)
	{
		this.mentorFlagTime = mentorFlagTime;
	}
	
	/**
	 * Sets the {@code race} for this location.<br>
	 * This updates the internal {@code race} field.
	 * @param race The new {@link Race} to assign.
	 */
	public void setRace(Race race)
	{
		this.race = race;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name.
	 * @param name The new name to assign.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Sets the character class for the player.<br>
	 * This updates the {@code playerClass} field in this object.
	 * @param playerClass The new {@link PlayerClass} to assign.
	 */
	public void setPlayerClass(PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}
	
	/**
	 * Checks if the player is currently online.<br>
	 * This method returns the current status of the {@code isOnline} flag.
	 * @return {@code true} if the player is online, {@code false} otherwise.
	 */
	public boolean isOnline()
	{
		return online;
	}
	
	/**
	 * Updates the online status of the player.<br>
	 * Sets the {@code online} field to the provided value.
	 * @param online The new online status to set.
	 */
	public void setOnline(boolean online)
	{
		this.online = online;
	}
	
	/**
	 * Retrieves the gender of the player.<br>
	 * This value is stored in the {@code playerGender} field.
	 * @return the {@link Gender} of the player.
	 */
	public Gender getGender()
	{
		return gender;
	}
	
	/**
	 * Sets the gender of the player.<br>
	 * This updates the {@code gender} field in this object.
	 * @param gender The {@link Gender} to assign to the player.
	 */
	public void setGender(Gender gender)
	{
		this.gender = gender;
	}
	
	/**
	 * Retrieves the current location of this visible object.
	 * @return the {@code WorldPosition} of the object.
	 */
	public WorldPosition getPosition()
	{
		return position;
	}
	
	/**
	 * Retrieves the timestamp of when the player was last online.
	 * @return a {@code Timestamp} object representing the last login time.
	 */
	public Timestamp getLastOnline()
	{
		return lastOnline;
	}
	
	/**
	 * Updates the last online time for the player.<br>
	 * This method sets the {@code lastOnline} field to the provided value.
	 * @param timestamp The {@code Timestamp} representing when the player was last online.
	 */
	public void setLastOnline(Timestamp timestamp)
	{
		lastOnline = timestamp;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Sets the player level based on the provided value.<br>
	 * This method updates the experience points using {@code PLAYER_EXPERIENCE_TABLE}.<br>
	 * The update only occurs if the level is within the valid range.
	 * @param level The new level to set for the player.
	 */
	public void setLevel(int level)
	{
		if (level <= DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel())
		{
			setExp(DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level));
		}
	}
	
	/**
	 * Retrieves the note associated with this friend.<br>
	 * This method fetches the data from the {@link PlayerCommonData} object.
	 * @return The note as a {@code String}.
	 */
	public String getNote()
	{
		return note;
	}
	
	/**
	 * Updates the personal note for this friend.<br>
	 * This method stores a new {@code String} value in the internal field.
	 * @param note The new note to assign to the friend.
	 */
	public void setNote(String note)
	{
		this.note = note;
	}
	
	/**
	 * Retrieves the unique identifier for the title.<br>
	 * This value corresponds to the {@code title_id} attribute.
	 * @return The integer ID of the title.
	 */
	public int getTitleId()
	{
		return titleId;
	}
	
	/**
	 * Sets the unique identifier for the player's title.<br>
	 * This value is used to determine which title is displayed for the character.
	 * @param titleId The {@code int} ID of the title to assign.
	 */
	public void setTitleId(int titleId)
	{
		this.titleId = titleId;
	}
	
	/**
	 * Retrieves the unique identifier for the player's bonus title.<br>
	 * This value is used to determine which specific title is currently active.
	 * @return The {@code int} ID of the bonus title.
	 */
	public int getBonusTitleId()
	{
		return bonusTitleId;
	}
	
	/**
	 * Sets the unique identifier for the player's bonus title.<br>
	 * This value is used to determine which specific title is displayed.
	 * @param bonusTitleId The {@code int} ID of the bonus title.
	 */
	public void setBonusTitleId(int bonusTitleId)
	{
		this.bonusTitleId = bonusTitleId;
	}
	
	/**
	 * Updates the current location of this object.<br>
	 * This method sets the {@code position} field to a new {@link WorldPosition}.
	 * @param position The new {@link WorldPosition} to assign to this object.
	 */
	public void setPosition(WorldPosition position)
	{
		this.position = position;
	}
	
	/**
	 * Retrieves the {@link Player} object associated with this data.<br>
	 * This method returns {@code null} if the player is offline or has no position.
	 * @return The {@code Player} object, or {@code null} if not found.
	 */
	public Player getPlayer()
	{
		if (online && (getPosition() != null))
		{
			return World.getInstance().findPlayer(playerObjId);
		}
		
		return null;
	}
	
	/**
	 * Increases the player's current DP value.<br>
	 * This method updates the internal state by adding a specific amount to the existing {@code dp}.
	 * @param dp The amount of DP to add to the player.
	 */
	public void addDp(int dp)
	{
		setDp(this.dp + dp);
	}
	
	/**
	 * Updates the current DP value of the player.<br>
	 * This method ensures the new {@code dp} does not exceed the maximum allowed limit.<br>
	 * It also broadcasts the updated value to other players and updates the visual stats.
	 * @param dp The new DP value to set for the player.
	 */
	public void setDp(int dp)
	{
		if (getPlayer() != null)
		{
			if (playerClass.isStartingClass())
			{
				return;
			}
			
			final int maxDp = getPlayer().getGameStats().getMaxDp().getCurrent();
			this.dp = dp > maxDp ? maxDp : dp;
			
			PacketSendUtility.broadcastPacket(getPlayer(), new SM_DP_INFO(playerObjId, this.dp), true);
			getPlayer().getGameStats().updateStatsAndSpeedVisually();
			PacketSendUtility.sendPacket(getPlayer(), new SM_STATUPDATE_DP(this.dp));
		}
		else
		{
			log.debug("CHECKPOINT : getPlayer in PCD return null for setDP " + isOnline() + " " + getPosition());
		}
	}
	
	/**
	 * Retrieves the current DP value of the player.
	 * @return The integer value of {@code dp}.
	 */
	public int getDp()
	{
		return dp;
	}
	
	/**
	 * Retrieves the unique identifier for this player template.<br>
	 * This value is calculated based on the character's race and gender.
	 * @return The unique template ID as an {@code int}.
	 */
	@Override
	public int getTemplateId()
	{
		return 100000 + (race.getRaceId() * 2) + gender.getGenderId();
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	@Override
	public int getNameId()
	{
		return 0;
	}
	
	/**
	 * Updates the warehouse capacity for the player.<br>
	 * This method modifies the {@code PlayerCommonData} and increases the current limit.
	 * @param warehouseSize The amount of space to add to the warehouse.
	 */
	public void setWarehouseSize(int warehouseSize)
	{
		this.warehouseSize = warehouseSize;
	}
	
	/**
	 * Retrieves the total size of the player's warehouse.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The integer size of the warehouse.
	 */
	public int getWarehouseSize()
	{
		return warehouseSize;
	}
	
	/**
	 * Updates the number of letters in the player's mailbox.<br>
	 * This method sets the {@code mailboxLetters} field to a new value.
	 * @param count The total number of letters to set.
	 */
	public void setMailboxLetters(int count)
	{
		mailboxLetters = count;
	}
	
	/**
	 * Retrieves the current number of letters in the player's mailbox.
	 * @return The total count of letters as an {@code int}.
	 */
	public int getMailboxLetters()
	{
		return mailboxLetters;
	}
	
	/**
	 * Sets the {@code BoundRadius} for this player object.<br>
	 * This updates the internal {@code boundRadius} field with the provided value.
	 * @param boundRadius The new {@link BoundRadius} to apply.
	 */
	public void setBoundingRadius(BoundRadius boundRadius)
	{
		this.boundRadius = boundRadius;
	}
	
	/**
	 * Retrieves the current {@code BoundRadius} for this player.<br>
	 * This value defines the area around the player where certain actions are restricted.
	 * @return The {@link BoundRadius} object associated with this player.
	 */
	@Override
	public BoundRadius getBoundRadius()
	{
		return boundRadius;
	}
	
	/**
	 * Updates the soul sickness value for the player.<br>
	 * This method sets the {@code soulSickness} field to a new value.
	 * @param count The number of souls to set.
	 */
	public void setDeathCount(int count)
	{
		soulSickness = count;
	}
	
	/**
	 * Retrieves the current death count for the player.<br>
	 * This value is stored in the {@code soulSickness} field.
	 * @return The total number of deaths as an {@code int}.
	 */
	public int getDeathCount()
	{
		return soulSickness;
	}
	
	/**
	 * Retrieves the current salvation percentage for the player.<br>
	 * This value is calculated based on the {@code salvationPoint} field.<br>
	 * The result is capped at a maximum of {@code 30}.
	 * @return The salvation percentage as a {@code byte}.
	 */
	public byte getCurrentSalvationPercent()
	{
		if (salvationPoint <= 0)
		{
			return 0;
		}
		
		final long per = salvationPoint / 1000;
		if (per > 30)
		{
			return 30;
		}
		
		return (byte) per;
	}
	
	/**
	 * Adds a specific amount of salvation points to the player.<br>
	 * This method updates the {@code PlayerCommonData} and sends a status packet to the client.
	 * @param points The number of points to add.
	 */
	public void addSalvationPoints(long points)
	{
		salvationPoint += points;
	}
	
	/**
	 * Resets the player's salvation points to {@code 0}.<br>
	 * This method updates the internal state of the current object.
	 */
	public void resetSalvationPoints()
	{
		salvationPoint = 0;
	}
	
	/**
	 * Updates the last transfer time for the player.<br>
	 * This method sets the {@code lastTransferTime} field to a new value.
	 * @param value The timestamp to set as the last transfer time.
	 */
	public void setLastTransferTime(long value)
	{
		lastTransferTime = value;
	}
	
	/**
	 * Retrieves the timestamp of the most recent transfer.<br>
	 * This value is stored as a {@code long}.
	 * @return The time of the last transfer.
	 */
	public long getLastTransferTime()
	{
		return lastTransferTime;
	}
	
	/**
	 * Retrieves the unique identifier of the owner for this world.
	 * @return The {@code int} ID of the world owner.
	 */
	public int getWorldOwnerId()
	{
		return worldOwnerId;
	}
	
	/**
	 * Sets the unique identifier for the owner of the world.<br>
	 * This value is stored in the {@code worldOwnerId} field.
	 * @param worldOwnerId The unique ID of the world owner.
	 */
	public void setWorldOwnerId(int worldOwnerId)
	{
		this.worldOwnerId = worldOwnerId;
	}
	
	/**
	 * Retrieves the current number of battle ground points.<br>
	 * This value represents the player's accumulated points in battle grounds.
	 * @return The total amount of {@code battleGroundPoints}.
	 */
	public int getBattleGroundPoints()
	{
		return battleGroundPoints;
	}
	
	/**
	 * Updates the current {@code battleGroundPoints} for the player.<br>
	 * This method sets the point value used in battle ground activities.
	 * @param battleGroundPoints The new amount of points to assign.
	 */
	public void setBattleGroundPoints(int battleGroundPoints)
	{
		this.battleGroundPoints = battleGroundPoints;
	}
	
	/**
	 * Checks if the player has the default game statistics.<br>
	 * This method retrieves the value from {@code initialGameStatsDatabase}.
	 * @return The integer value representing the initial game stats.
	 */
	public int isInitialGameStats()
	{
		return initialGameStatsDatabase;
	}
	
	/**
	 * Sets the starting game statistics for the player.<br>
	 * This value is stored in the {@code initialGameStatsDatabase}.
	 * @param initialGameStats The integer value to set as the initial stats.
	 */
	public void setInitialGameStats(int initialGameStats)
	{
		initialGameStatsDatabase = initialGameStats;
	}
	
	/**
	 * Updates the fatigue level of the player.<br>
	 * This method sets the {@code fatigue} variable to a new value.
	 * @param value The new integer value for the player's fatigue.
	 */
	public void setFatigue(int value)
	{
		fatigue = value;
	}
	
	/**
	 * Sets the amount of fatigue to be recovered.<br>
	 * This updates the {@code fatigueRecover} field in the player data.
	 * @param count The number of fatigue points to recover.
	 */
	public void setFatigueRecover(int count)
	{
		fatigueRecover = count;
	}
	
	/**
	 * Retrieves the current fatigue level of the player.<br>
	 * This value represents how much energy the player has remaining.
	 * @return The current {@code int} fatigue value.
	 */
	public int getFatigue()
	{
		return fatigue;
	}
	
	/**
	 * Retrieves the current amount of fatigue recovery.<br>
	 * This value is used to determine how much fatigue a player can recover.
	 * @return The {@code int} value representing the fatigue recovery amount.
	 */
	public int getFatigueRecover()
	{
		return fatigueRecover;
	}
	
	/**
	 * Sets the fatigue reset value for the player.<br>
	 * This updates the {@code fatigueReset} field in the data object.
	 * @param value The new integer value to set for fatigue reset.
	 */
	public void setFatigueReset(int value)
	{
		fatigueReset = value;
	}
	
	/**
	 * Retrieves the current fatigue reset value.<br>
	 * This value is stored in the {@code fatigueReset} field.
	 * @return The integer value of the fatigue reset.
	 */
	public int getFatigueReset()
	{
		return fatigueReset;
	}
	
	/**
	 * Retrieves the unique identifier for the legion join request.<br>
	 * This ID is used to track specific requests within the system.
	 * @return The {@code int} value of the join request legion ID.
	 */
	public int getJoinRequestLegionId()
	{
		return joinRequestLegionId;
	}
	
	/**
	 * Sets the unique identifier for a legion join request.<br>
	 * This value is used to track specific requests within the system.
	 * @param joinRequestLegionId The {@code int} ID of the legion join request.
	 */
	public void setJoinRequestLegionId(int joinRequestLegionId)
	{
		this.joinRequestLegionId = joinRequestLegionId;
	}
	
	/**
	 * Retrieves the current state of a legion join request.<br>
	 * This method returns the {@code LegionJoinRequestState} for the player.
	 * @return The current {@link LegionJoinRequestState}.
	 */
	public LegionJoinRequestState getJoinRequestState()
	{
		return joinRequestState;
	}
	
	/**
	 * Updates the current state of a legion join request.<br>
	 * This method sets the {@code joinRequestState} field for the player.
	 * @param joinRequestState The new {@link LegionJoinRequestState} to apply.
	 */
	public void setJoinRequestState(LegionJoinRequestState joinRequestState)
	{
		this.joinRequestState = joinRequestState;
	}
	
	/**
	 * Retrieves the arcade upgrade data for the player.<br>
	 * This method fetches information from the {@code PlayerCommonData}.
	 * @return the {@code PlayerUpgradeArcade} object associated with this player.
	 */
	public PlayerUpgradeArcade getUpgradeArcade()
	{
		if (upgradeArcade == null)
		{
			upgradeArcade = new PlayerUpgradeArcade();
		}
		
		return upgradeArcade;
	}
	
	/**
	 * Sets the arcade upgrade information for the player.<br>
	 * This updates the {@code upgradeArcade} field in the current object.
	 * @param upgradeArcade The {@link PlayerUpgradeArcade} data to assign.
	 */
	public void setUpgradeArcade(PlayerUpgradeArcade upgradeArcade)
	{
		this.upgradeArcade = upgradeArcade;
	}
	
	/**
	 * Retrieves the current bonus time for the player.<br>
	 * This value is used to track active time-based rewards.
	 * @return The {@code PlayerBonusTime} object containing the bonus details.
	 */
	public PlayerBonusTime getBonusTime()
	{
		return bonusTime;
	}
	
	/**
	 * Updates the bonus time for the player.<br>
	 * This method sets the internal {@code bonusTime} field to the provided value.
	 * @param time The new {@link Timestamp} to set for the bonus period.
	 */
	public void setBonusTime(Timestamp time)
	{
		bonusTime.setTime(time);
	}
	
	/**
	 * Updates the status of the player's bonus time.<br>
	 * This method modifies the {@code bonusTime} object using the provided value.
	 * @param status The new {@link PlayerBonusTimeStatus} to apply.
	 */
	public void setBonusType(PlayerBonusTimeStatus status)
	{
		bonusTime.setStatus(status);
	}
	
	/**
	 * Sets the creation date for the player.<br>
	 * This updates the {@code creationDate} field with the provided value.
	 * @param date The {@code Timestamp} representing when the player was created.
	 */
	public void setCreationDate(Timestamp date)
	{
		creationDate = date;
	}
	
	/**
	 * Retrieves the date and time when the account was created.<br>
	 * This value is stored as a {@code Timestamp}.
	 * @return The {@code Timestamp} representing the creation date.
	 */
	public Timestamp getCreationDate()
	{
		return creationDate;
	}
	
	/**
	 * Retrieves the current amount of Luna Coins for the player.
	 * @return The total number of {@code lunaCoins}.
	 */
	public int getLunaCoins()
	{
		return lunaCoins;
	}
	
	/**
	 * Updates the player's current amount of Luna Coins.<br>
	 * This method sets the {@code lunaCoins} value for the player object.
	 * @param lunaCoins The new amount of coins to set.
	 */
	public void setLunaCoins(int lunaCoins)
	{
		this.lunaCoins = lunaCoins;
	}
	
	/**
	 * Retrieves the current size of the player's wardrobe.<br>
	 * This value represents the total capacity for items.
	 * @return The integer size of the wardrobe.
	 */
	public int getWardrobeSize()
	{
		return wardrobeSize;
	}
	
	/**
	 * Updates the size of the player's wardrobe.<br>
	 * This method sets the {@code wardrobeSize} field to a new value.
	 * @param wardrobeSize The new size for the wardrobe.
	 */
	public void setWardrobeSize(int wardrobeSize)
	{
		this.wardrobeSize = wardrobeSize;
	}
	
	/**
	 * Updates the Luna consume point for the player.<br>
	 * This method modifies the value stored in {@code PlayerCommonData}.
	 * @param point The new integer value to set for the Luna consume point.
	 */
	public void setLunaConsumePoint(int point)
	{
		lunaConsumePoint = point;
	}
	
	/**
	 * Retrieves the current Luna Consume Point value for the player.<br>
	 * This method fetches data from the {@code PlayerCommonData} object.
	 * @return The integer value of the Luna Consume Points.
	 */
	public int getLunaConsumePoint()
	{
		return lunaConsumePoint;
	}
	
	/**
	 * Updates the municipality keys for the player.<br>
	 * This method updates the {@code keys} value in the {@code PlayerCommonData}.
	 * @param keys The new integer value for the municipality keys.
	 */
	public void setMuniKeys(int keys)
	{
		muni_keys = keys;
	}
	
	/**
	 * Retrieves the number of municipal keys held by the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The total count of municipal keys as an {@code int}.
	 */
	public int getMuniKeys()
	{
		return muni_keys;
	}
	
	/**
	 * Updates the number of Luna items consumed by the player.<br>
	 * This method updates the value in {@code PlayerCommonData}.
	 * @param count The new consumption count to set.
	 */
	public void setLunaConsumeCount(int count)
	{
		consumeCount = count;
	}
	
	/**
	 * Retrieves the total number of Luna items consumed by the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The current count of consumed Luna items as an {@code int}.
	 */
	public int getLunaConsumeCount()
	{
		return consumeCount;
	}
	
	/**
	 * Updates the current wardrobe slot for the player.<br>
	 * This method modifies the {@code PlayerCommonData} object.
	 * @param slot The index of the wardrobe slot to set.
	 */
	public void setWardrobeSlot(int slot)
	{
		wardrobeSlot = slot;
	}
	
	/**
	 * Retrieves the current wardrobe slot index for the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The integer representing the wardrobe slot.
	 */
	public int getWardrobeSlot()
	{
		return wardrobeSlot;
	}
	
	/**
	 * Retrieves the current amount of golden dice owned by the player.
	 * @return The number of {@code goldenDice} as an {@code int}.
	 */
	public int getGoldenDice()
	{
		return goldenDice;
	}
	
	/**
	 * Sets the number of golden dice for the player.<br>
	 * This updates the {@code goldenDice} field in the {@link PlayerCommonData} object.
	 * @param dice The amount of golden dice to set.
	 */
	public void setGoldenDice(int dice)
	{
		goldenDice = dice;
	}
	
	/**
	 * Retrieves the current state of the reset board.<br>
	 * This value is used to determine if a player can interact with the board.
	 * @return The {@code int} value representing the reset board status.
	 */
	public int getResetBoard()
	{
		return resetBoard;
	}
	
	/**
	 * Updates the board reset status for the player.<br>
	 * This method sets the {@code resetBoard} value to the provided integer.
	 * @param reset The new value to assign to the board reset state.
	 */
	public void setResetBoard(int reset)
	{
		resetBoard = reset;
	}
	
	/**
	 * Updates the current floor level for the player.<br>
	 * This method updates the value within the {@link PlayerCommonData} object.
	 * @param floor The new floor number to set.
	 */
	public void setFloor(int floor)
	{
		this.floor = floor;
	}
	
	/**
	 * Retrieves the current floor level of the player.<br>
	 * This method fetches the data from the {@link PlayerCommonData} object.
	 * @return The integer value representing the current floor.
	 */
	public int getFloor()
	{
		return floor;
	}
	
	/**
	 * Sets the current energy level for the minion.<br>
	 * This updates the {@code minionEnergy} field.
	 * @param energy The new energy value to assign.
	 */
	public void setMinionEnergy(int energy)
	{
		minionEnergy = energy;
	}
	
	/**
	 * Retrieves the current energy level of the player's minion.
	 * @return The {@code int} value representing the minion's energy.
	 */
	public int getMinionEnergy()
	{
		return minionEnergy;
	}
	
	/**
	 * Updates the ID of the last minion used by the player.<br>
	 * This value is stored in the {@code lastMinion} field.
	 * @param id The unique identifier for the last minion.
	 */
	public void setLastMinion(int id)
	{
		lastMinion = id;
	}
	
	/**
	 * Retrieves the ID of the most recently summoned minion.
	 * @return The {@code int} identifier of the last minion.
	 */
	public int getLastMinion()
	{
		return lastMinion;
	}
	
	/**
	 * Retrieves the time associated with the minion function.<br>
	 * This value is stored in a {@code Timestamp} format.
	 * @return The {@code Timestamp} of the minion function time.
	 */
	public Timestamp getMinionFunctionTime()
	{
		return minionFunctionTime;
	}
	
	/**
	 * Sets the minimum time required for a minion to perform its function.<br>
	 * This value is stored in the {@code minionFunctionTime} field.
	 * @param minionFunctionTime The {@code Timestamp} representing the minimum duration.
	 */
	public void setMinionFunctionTime(Timestamp minionFunctionTime)
	{
		this.minionFunctionTime = minionFunctionTime;
	}
	
	/**
	 * Retrieves the most recent timestamp for this entry.<br>
	 * This value represents when the event was last updated.
	 * @return the {@code Timestamp} of the last update.
	 */
	public Timestamp getLastStamp()
	{
		return lastStamp;
	}
	
	/**
	 * Updates the last recorded {@code Timestamp} for this player.<br>
	 * This value is used to track the most recent activity or update time.
	 * @param timestamp The new {@code Timestamp} to assign to the last stamp.
	 */
	public void setLastStamp(Timestamp timestamp)
	{
		lastStamp = timestamp;
	}
	
	/**
	 * Retrieves the total number of passport stamps for the player.
	 * @return The current count of {@code stamps}.
	 */
	public int getPassportStamps()
	{
		return stamps;
	}
	
	/**
	 * Updates the number of passport stamps for the player.<br>
	 * This method sets the {@code stamps} field to a new value.
	 * @param stamps The total number of stamps to assign.
	 */
	public void setPassportStamps(int stamps)
	{
		this.stamps = stamps;
	}
	
	/**
	 * Retrieves the collection of all player passports.<br>
	 * The map uses the unique object ID as the key.
	 * @return A {@code Map} containing {@link Integer} IDs and their corresponding {@link AtreianPassport} objects.
	 */
	public Map<Integer, AtreianPassport> getPlayerPassports()
	{
		return atreianPassports;
	}
	
	/**
	 * Retrieves the list of finished passports for the player.<br>
	 * This method returns the {@code completedPassports} collection.
	 * @return A collection of {@link AtreianPassport} objects that have been completed.
	 */
	public AtreianPassport getCompletedPassports()
	{
		return completedPassports;
	}
	
	/**
	 * Adds a specific passport to the list of completed passports.<br>
	 * This method updates the {@code completedPassports} collection using the provided template.
	 * @param atreianPassportTemplate The {@link AtreianPassportTemplate} object to be added.
	 */
	public void addToCompletedPassports(AtreianPassportTemplate atreianPassportTemplate)
	{
		completedPassports.addPassport(atreianPassportTemplate.getId(), atreianPassportTemplate);
	}
	
	/**
	 * Updates the completed passports for the player.<br>
	 * This method assigns a new {@link AtreianPassport} object to the internal field.
	 * @param atreianPassport The {@code AtreianPassport} object to set.
	 */
	public void setCompletedPassports(AtreianPassport atreianPassport)
	{
		completedPassports = atreianPassport;
	}
	
	/**
	 * Retrieves the current reward value for the player's passport.
	 * @return The {@code int} value of the passport reward.
	 */
	public int getPassportReward()
	{
		return passportReward;
	}
	
	/**
	 * Sets the reward value for the player's passport.<br>
	 * This updates the {@code passportReward} field in the current object.
	 * @param passportReward The new reward amount to assign.
	 */
	public void setPassportReward(int passportReward)
	{
		this.passportReward = passportReward;
	}
	
	/**
	 * Updates the current world play time for the player.<br>
	 * This method sets the {@code worldPlayTime} variable to a new value.
	 * @param playTime The new play time value to set.
	 */
	public void setWorldPlayTime(int playTime)
	{
		worldPlayTime = playTime;
	}
	
	/**
	 * Retrieves the total time spent in the game world.<br>
	 * This value is stored as an integer representing seconds.
	 * @return The current {@code worldPlayTime}.
	 */
	public int getWorldPlayTime()
	{
		return worldPlayTime;
	}
}
