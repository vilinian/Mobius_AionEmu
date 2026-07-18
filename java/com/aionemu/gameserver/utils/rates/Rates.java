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

/**
 * This class serves as a base for managing game experience and drop rates.<br>
 * It provides a centralized way to handle various {@code rate} configurations across the server.
 * @author ATracer
 * @author GiGatR00n v4.7.5.x
 */
public abstract class Rates
{
	public abstract float getGroupXpRate();
	
	public abstract float getXpRate();
	
	public abstract float getBookXpRate();
	
	public abstract float getApNpcRate();
	
	public abstract float getApPlayerGainRate();
	
	public abstract float getGpPlayerGainRate();
	
	public abstract float getXpPlayerGainRate();
	
	public abstract float getApPlayerLossRate();
	
	public abstract float getGpPlayerLossRate();
	
	public abstract float getGatheringXPRate();
	
	public abstract int getGatheringCountRate();
	
	public abstract float getCraftingXPRate();
	
	public abstract float getDropRate();
	
	public abstract float getQuestXpRate();
	
	public abstract float getQuestKinahRate();
	
	public abstract float getQuestApRate();
	
	public abstract float getQuestGpRate();
	
	public abstract float getQuestExpBoostRate();
	
	public abstract float getDpNpcRate();
	
	public abstract float getDpPlayerRate();
	
	public abstract int getCraftCritRate();
	
	public abstract int getComboCritRate();
	
	public abstract float getDisciplineRewardRate();
	
	public abstract float getChaosRewardRate();
	
	public abstract float getHarmonyRewardRate();
	
	public abstract float getGloryRewardRate();
	
	public abstract float getSellLimitRate();
	
	public abstract float getKamarRewardRate();
	
	public abstract float getRunatoriumBoxRewardRate();
	
	public abstract float getJormungandRewardRate();
	
	public abstract float getSteelWallRewardRate();
	
	/**
	 * Retrieves the correct {@link Rates} object based on a membership type.<br>
	 * This method maps specific byte values to different rate configurations.<br>
	 * It returns {@code RegularRates} for values 0 and 1.<br>
	 * It returns {@code PremiumRates} for value 2.<br>
	 * It defaults to {@code VipRates} for all other values.
	 * @param membership The membership level provided as a {@code byte}.
	 * @return A new instance of the corresponding {@link Rates} class.
	 */
	public static Rates getRatesFor(byte membership)
	{
		switch (membership)
		{
			case 0:
			case 1:
				return new RegularRates();
			case 2:
				return new PremiumRates();
			case 3:
				return new VipRates();
			default:
				return new VipRates();
		}
	}
}
