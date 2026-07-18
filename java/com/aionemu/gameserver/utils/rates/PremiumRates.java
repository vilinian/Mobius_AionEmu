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
package com.aionemu.gameserver.utils.rates;

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.configs.main.RateConfig;

/**
 * This class defines the specific experience and drop rates for premium players.<br>
 * It extends {@link Rates} to provide specialized multipliers for various game mechanics.
 * @author ATracer
 * @author GiGatR00n v4.7.5.x
 */
public class PremiumRates extends Rates
{
	int holidayRate = HolidayRates.getHolidayRates(1);
	
	/**
	 * Gets the experience rate for groups.<br>
	 * This value is calculated from {@code PREMIUM_GROUPXP_RATE}.<br>
	 * It also includes any active holiday bonuses.
	 * @return The total group experience rate as a {@code float}.
	 */
	@Override
	public float getGroupXpRate()
	{
		return RateConfig.PREMIUM_GROUPXP_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current AP rate for NPCs.<br>
	 * This value is calculated using {@code RateConfig.PREMIUM_AP_NPC_RATE}.<br>
	 * It also includes any active holiday bonuses.
	 * @return The total AP NPC rate as a {@code float}.
	 */
	@Override
	public float getApNpcRate()
	{
		return RateConfig.PREMIUM_AP_NPC_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current AP gain rate for premium players.<br>
	 * This value includes the base rate from {@code PREMIUM_AP_PLAYER_GAIN_RATE}.<br>
	 * It also adds any active holiday bonuses.
	 * @return The total AP gain rate as a {@code float}.
	 */
	@Override
	public float getApPlayerGainRate()
	{
		return RateConfig.PREMIUM_AP_PLAYER_GAIN_RATE + holidayRate;
	}
	
	/**
	 * Gets the experience point gain rate for premium players.<br>
	 * This value includes the base rate from {@link RateConfig}.<br>
	 * It also adds any active holiday bonuses.
	 * @return The total calculated XP gain rate as a {@code float}.
	 */
	@Override
	public float getXpPlayerGainRate()
	{
		return RateConfig.PREMIUM_XP_PLAYER_GAIN_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current GP gain rate for premium players.<br>
	 * This value is fetched from {@link RateConfig}.
	 * @return The multiplier for player GP gains as a {@code float}.
	 */
	@Override
	public float getGpPlayerGainRate()
	{
		return RateConfig.PREMIUM_GP_PLAYER_GAIN_RATE;
	}
	
	/**
	 * Retrieves the current loss rate for premium players.<br>
	 * This value is calculated using {@code RateConfig.PREMIUM_AP_PLAYER_LOSS_RATE}.<br>
	 * It also includes any active holiday bonuses.
	 * @return The total player loss rate as a {@code float}.
	 */
	@Override
	public float getApPlayerLossRate()
	{
		return RateConfig.PREMIUM_AP_PLAYER_LOSS_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the GP loss rate for premium players.<br>
	 * This value is fetched from {@link RateConfig}.
	 * @return The current GP player loss rate as a {@code float}.
	 */
	@Override
	public float getGpPlayerLossRate()
	{
		return RateConfig.PREMIUM_GP_PLAYER_LOSS_RATE;
	}
	
	/**
	 * Retrieves the current drop rate for premium players.<br>
	 * This value combines the base {@code PREMIUM_DROP_RATE} with any active holiday bonuses.
	 * @return The calculated drop rate as a {@code float}.
	 */
	@Override
	public float getDropRate()
	{
		return RateConfig.PREMIUM_DROP_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current quest Kinah reward rate.<br>
	 * This value combines the premium config and active holiday rates.
	 * @return The total multiplier for quest rewards.
	 */
	@Override
	public float getQuestKinahRate()
	{
		return RateConfig.PREMIUM_QUEST_KINAH_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current experience rate for quests.<br>
	 * This value combines the premium quest rate and any active holiday bonuses.
	 * @return The total quest experience rate as a {@code float}.
	 */
	@Override
	public float getQuestXpRate()
	{
		return RateConfig.PREMIUM_QUEST_XP_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current AP rate for quests.<br>
	 * This value combines the premium quest AP rate and any active holiday rates.
	 * @return The total calculated quest AP rate as a {@code float}.
	 */
	@Override
	public float getQuestApRate()
	{
		return RateConfig.PREMIUM_QUEST_AP_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current Gold Point (GP) rate for quests.<br>
	 * This value is calculated based on the {@code RateConfig} settings.<br>
	 * It also includes any active holiday bonuses.
	 * @return The total quest GP rate as a {@code float}.
	 */
	@Override
	public float getQuestGpRate()
	{
		return RateConfig.PREMIUM_QUEST_GP_RATE + holidayRate;
	}
	
	/**
	 * Gets the current quest experience boost rate.<br>
	 * This value combines the premium config and active holiday rates.
	 * @return The total quest experience boost rate as a {@code float}.
	 */
	@Override
	public float getQuestExpBoostRate()
	{
		return RateConfig.PREMIUM_QUEST_EXP_BOOST_RATE + holidayRate;
	}
	
	/**
	 * Gets the current experience point rate for premium players.<br>
	 * This value combines the base {@code PREMIUM_XP_RATE} with any active holiday bonuses.
	 * @return The total calculated experience rate as a {@code float}.
	 */
	@Override
	public float getXpRate()
	{
		return RateConfig.PREMIUM_XP_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the experience rate for books.<br>
	 * This value combines the premium book rate and the current holiday rate.
	 * @return The total calculated experience rate as a {@code float}.
	 */
	@Override
	public float getBookXpRate()
	{
		return RateConfig.PREMIUM_BOOK_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the experience point rate for crafting.<br>
	 * This value is used to calculate rewards for premium players.<br>
	 * It returns the value defined in {@link RateConfig}.
	 * @return The current crafting experience rate as a {@code float}.
	 */
	@Override
	public float getCraftingXPRate()
	{
		return RateConfig.PREMIUM_CRAFTING_XP_RATE;
	}
	
	/**
	 * Retrieves the experience rate for gathering activities.<br>
	 * This value combines the premium rate and any active holiday bonuses.
	 * @return The total {@code float} experience rate.
	 */
	@Override
	public float getGatheringXPRate()
	{
		return RateConfig.PREMIUM_GATHERING_XP_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current gathering count rate for premium players.<br>
	 * This value combines the base rate from {@link RateConfig} with any active holiday bonuses.
	 * @return The total gathering count rate as an {@code int}.
	 */
	@Override
	public int getGatheringCountRate()
	{
		return RateConfig.PREMIUM_GATHERING_COUNT_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current rate for DP from NPCs.<br>
	 * This value combines the premium base rate and any active holiday bonuses.
	 * @return The calculated {@code float} rate for DP NPC rewards.
	 */
	@Override
	public float getDpNpcRate()
	{
		return RateConfig.PREMIUM_DP_NPC_RATE + holidayRate;
	}
	
	/**
	 * Gets the current DP gain rate for players.<br>
	 * This value combines the base premium rate and any active holiday bonuses.
	 * @return The total player DP rate as a {@code float}.
	 */
	@Override
	public float getDpPlayerRate()
	{
		return RateConfig.PREMIUM_DP_PLAYER_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current critical hit rate for crafting.<br>
	 * This value combines the premium base rate and any active holiday bonuses.
	 * @return The total craft critical hit rate as an {@code int}.
	 */
	@Override
	public int getCraftCritRate()
	{
		return CraftConfig.PREMIUM_CRAFT_CRIT_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current critical rate for combos.<br>
	 * This value is calculated using {@code PREMIUM_CRAFT_COMBO_RATE}.<br>
	 * It also includes any active holiday bonuses.
	 * @return The total combo critical rate as an {@code int}.
	 */
	@Override
	public int getComboCritRate()
	{
		return CraftConfig.PREMIUM_CRAFT_COMBO_RATE + holidayRate;
	}
	
	/**
	 * Gets the current reward rate for discipline.<br>
	 * This value is calculated using {@code PREMIUM_PVP_ARENA_DISCIPLINE_REWARD_RATE}.<br>
	 * It also includes any active holiday bonuses.
	 * @return The total discipline reward rate as a {@code float}.
	 */
	@Override
	public float getDisciplineRewardRate()
	{
		return RateConfig.PREMIUM_PVP_ARENA_DISCIPLINE_REWARD_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current reward rate for Chaos.<br>
	 * This value combines the premium PVP arena rate and any active holiday bonuses.
	 * @return The total calculated chaos reward rate as a {@code float}.
	 */
	@Override
	public float getChaosRewardRate()
	{
		return RateConfig.PREMIUM_PVP_ARENA_CHAOS_REWARD_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current reward rate for Harmony.<br>
	 * This value is calculated using {@code PREMIUM_PVP_ARENA_HARMONY_REWARD_RATE}.<br>
	 * It also includes any active holiday bonuses.
	 * @return The total harmony reward rate as a {@code float}.
	 */
	@Override
	public float getHarmonyRewardRate()
	{
		return RateConfig.PREMIUM_PVP_ARENA_HARMONY_REWARD_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current reward rate for Glory.<br>
	 * This value combines the premium PVP arena rate and any active holiday bonuses.
	 * @return The total glory reward rate as a {@code float}.
	 */
	@Override
	public float getGloryRewardRate()
	{
		return RateConfig.PREMIUM_PVP_ARENA_GLORY_REWARD_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current sell limit rate for premium users.<br>
	 * This value combines the base rate from {@link RateConfig} with any active holiday bonuses.
	 * @return The calculated sell limit rate as a {@code float}.
	 */
	@Override
	public float getSellLimitRate()
	{
		return RateConfig.PREMIUM_SELL_LIMIT_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current reward rate for Kamar.<br>
	 * This value combines the base rate from {@code RateConfig} with any active holiday bonuses.
	 * @return The total calculated reward rate as a {@code float}.
	 */
	@Override
	public float getKamarRewardRate()
	{
		return RateConfig.KAMAR_REWARD_RATE + holidayRate;
	}
	
	/**
	 * Gets the current reward rate for Runatorium boxes.<br>
	 * This value combines the base rate from {@code RateConfig} with any active holiday bonuses.
	 * @return The total calculated reward rate as a {@code float}.
	 */
	@Override
	public float getRunatoriumBoxRewardRate()
	{
		return RateConfig.RUNATORIUM_BOX_REWARD_RATE + holidayRate;
	}
	
	/**
	 * Retrieves the current reward rate for Jormungand.<br>
	 * This value combines the premium rate and the active holiday rate.
	 * @return The total calculated reward rate as a {@code float}.
	 */
	@Override
	public float getJormungandRewardRate()
	{
		return RateConfig.PREMIUM_JORMUNGAND_REWARD_RATE + holidayRate;
	}
	
	/**
	 * Gets the current reward rate for Steel Wall.<br>
	 * This value combines the base premium rate and any active holiday bonuses.
	 * @return The total calculated reward rate as a {@code float}.
	 */
	@Override
	public float getSteelWallRewardRate()
	{
		return RateConfig.PREMIUM_STEELWALL_REWARD_RATE + holidayRate;
	}
}
