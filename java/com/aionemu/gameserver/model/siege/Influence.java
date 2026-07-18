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
package com.aionemu.gameserver.model.siege;

import java.util.Iterator;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * This class handles the calculation of influence scores for siege mechanics.<br>
 * It currently assigns {@code 10} points to fortresses and {@code 1} point to artifacts.
 * @author Sarynth
 * @updated Eloann
 */
public class Influence
{
	private static final Influence instance = new Influence();
	private float inggison_e = 0;
	private float inggison_a = 0;
	private float inggison_b = 0;
	private float gelkmaros_e = 0;
	private float gelkmaros_a = 0;
	private float gelkmaros_b = 0;
	private float abyss_e = 0;
	private float abyss_a = 0;
	private float abyss_b = 0;
	private float kaldor_e = 0;
	private float kaldor_a = 0;
	private float kaldor_b = 0;
	private final float belus_e = 0;
	private final float belus_a = 0;
	private final float belus_b = 0;
	private final float aspida_e = 0;
	private final float aspida_a = 0;
	private final float aspida_b = 0;
	private final float atanatos_e = 0;
	private final float atanatos_a = 0;
	private final float atanatos_b = 0;
	private final float disillon_e = 0;
	private final float disillon_a = 0;
	private final float disillon_b = 0;
	private float global_e = 0;
	private float global_a = 0;
	private float global_b = 0;
	
	/**
	 * Private constructor for the {@link Influence} class.<br>
	 * This prevents other classes from creating new instances.<br>
	 * It automatically triggers the initial calculation of influence values.
	 */
	private Influence()
	{
		calculateInfluence();
	}
	
	/**
	 * Provides access to the singleton instance of the {@link Influence} class.<br>
	 * Use this method to retrieve the global influence manager.
	 * @return The single {@code Influence} instance.
	 */
	public static Influence getInstance()
	{
		return instance;
	}
	
	/**
	 * Updates the influence values for all regions.<br>
	 * This method triggers the internal {@code calculateInfluence()} logic.
	 */
	public void recalculateInfluence()
	{
		calculateInfluence();
	}
	
	/**
	 * This method calculates the influence ratios for different regions.<br>
	 * It iterates through all siege locations provided by {@link SiegeService}.<br>
	 * The values are updated based on fortress ownership and race types.<br>
	 * Finally, it computes the global influence percentages.
	 */
	private void calculateInfluence()
	{
		final float balaurea = 0.0039512194f;
		final float abyss = 0.006097561f;
		float e_inggison = 0;
		float a_inggison = 0;
		float b_inggison = 0;
		float t_inggison = 0;
		float e_gelkmaros = 0;
		float a_gelkmaros = 0;
		float b_gelkmaros = 0;
		float t_gelkmaros = 0;
		float e_abyss = 0;
		float a_abyss = 0;
		float b_abyss = 0;
		float t_abyss = 0;
		float e_kaldor = 0;
		float a_kaldor = 0;
		float b_kaldor = 0;
		float t_kaldor = 0;
		for (SiegeLocation sLoc : SiegeService.getInstance().getSiegeLocations().values())
		{
			switch (sLoc.getWorldId())
			{
				case 210050000:
					if (sLoc instanceof FortressLocation)
					{
						t_inggison += sLoc.getInfluenceValue();
						switch (sLoc.getRace())
						{
							case ELYOS:
								e_inggison += sLoc.getInfluenceValue();
								break;
							case ASMODIANS:
								a_inggison += sLoc.getInfluenceValue();
								break;
							case BALAUR:
								b_inggison += sLoc.getInfluenceValue();
								break;
						}
					}
					break;
				case 220070000:
					if (sLoc instanceof FortressLocation)
					{
						t_gelkmaros += sLoc.getInfluenceValue();
						switch (sLoc.getRace())
						{
							case ELYOS:
								e_gelkmaros += sLoc.getInfluenceValue();
								break;
							case ASMODIANS:
								a_gelkmaros += sLoc.getInfluenceValue();
								break;
							case BALAUR:
								b_gelkmaros += sLoc.getInfluenceValue();
								break;
						}
					}
					break;
				case 400010000:
					if (sLoc instanceof FortressLocation)
					{
						t_abyss += sLoc.getInfluenceValue();
						switch (sLoc.getRace())
						{
							case ELYOS:
								e_abyss += sLoc.getInfluenceValue();
								break;
							case ASMODIANS:
								a_abyss += sLoc.getInfluenceValue();
								break;
							case BALAUR:
								b_abyss += sLoc.getInfluenceValue();
								break;
						}
					}
					break;
				case 800050000:
					if (sLoc instanceof FortressLocation)
					{
						t_kaldor += sLoc.getInfluenceValue();
						switch (sLoc.getRace())
						{
							case ELYOS:
								e_kaldor += sLoc.getInfluenceValue();
								break;
							case ASMODIANS:
								a_kaldor += sLoc.getInfluenceValue();
								break;
							case BALAUR:
								b_kaldor += sLoc.getInfluenceValue();
						}
					}
					break;
			}
		}
		
		inggison_e = e_inggison / t_inggison;
		inggison_a = a_inggison / t_inggison;
		inggison_b = b_inggison / t_inggison;
		
		gelkmaros_e = e_gelkmaros / t_gelkmaros;
		gelkmaros_a = a_gelkmaros / t_gelkmaros;
		gelkmaros_b = b_gelkmaros / t_gelkmaros;
		
		abyss_e = e_abyss / t_abyss;
		abyss_a = a_abyss / t_abyss;
		abyss_b = b_abyss / t_abyss;
		
		kaldor_e = (e_kaldor / t_kaldor);
		kaldor_a = (a_kaldor / t_kaldor);
		kaldor_b = (b_kaldor / t_kaldor);
		
		// global_e = ((kaldor_e * balaurea + inggison_e * balaurea + gelkmaros_e * balaurea + abyss_e * abyss) * 100f);
		// global_a = ((kaldor_a * balaurea + inggison_a * balaurea + gelkmaros_a * balaurea + abyss_a * abyss) * 100f);
		// global_b = ((kaldor_b * balaurea + inggison_b * balaurea + gelkmaros_b * balaurea + abyss_b * abyss) * 100f);
		// without Inggison and Gelkmaros since 5.3
		global_e = (((kaldor_e * balaurea) + (abyss_e * abyss)) * 100f);
		global_a = (((kaldor_a * balaurea) + (abyss_a * abyss)) * 100f);
		global_b = (((kaldor_b * balaurea) + (abyss_b * abyss)) * 100f);
	}
	
	/**
	 * Sends the current influence ratio to all players.<br>
	 * This method creates a new {@code SM_INFLUENCE_RATIO} packet.<br>
	 * It iterates through every {@link Player} in the {@link World}.<br>
	 * Each player receives the packet via {@code Object)}.
	 */
	@SuppressWarnings("unused")
	private void broadcastInfluencePacket()
	{
		final SM_INFLUENCE_RATIO pkt = new SM_INFLUENCE_RATIO();
		
		Player player;
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext())
		{
			player = iter.next();
			PacketSendUtility.sendPacket(player, pkt);
		}
	}
	
	/**
	 * Retrieves the current influence level for the Elyos faction.<br>
	 * This value represents the total global score for {@code Elyos}.
	 * @return The current global influence as a {@code float}.
	 */
	public float getGlobalElyosInfluence()
	{
		return global_e;
	}
	
	/**
	 * Retrieves the current total influence for the Asmodian faction.<br>
	 * This value represents the global score across all regions.
	 * @return The {@code float} value of the global Asmodian influence.
	 */
	public float getGlobalAsmodiansInfluence()
	{
		return global_a;
	}
	
	/**
	 * Retrieves the current total influence for the Balaur faction.<br>
	 * This value represents the global score across all regions.
	 * @return The {@code float} value of the global Balaur influence.
	 */
	public float getGlobalBalaursInfluence()
	{
		return global_b;
	}
	
	/**
	 * Retrieves the current influence level for Elyos in Inggison.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Elyos influence.
	 */
	public float getInggisonElyosInfluence()
	{
		return inggison_e;
	}
	
	/**
	 * Retrieves the current influence level for Asmodians in the Inggison region.<br>
	 * This value is used to determine regional control and power.
	 * @return The {@code float} value representing the Asmodian influence.
	 */
	public float getInggisonAsmodiansInfluence()
	{
		return inggison_a;
	}
	
	/**
	 * Retrieves the current influence value for the Balaurs in Inggison.<br>
	 * This value is used to determine regional control.
	 * @return The {@code float} value representing Balaur influence.
	 */
	public float getInggisonBalaursInfluence()
	{
		return inggison_b;
	}
	
	/**
	 * Retrieves the current influence level for Elyos in the Gelkmaros region.<br>
	 * This value is used to determine regional control.
	 * @return The {@code float} value representing the Elyos influence score.
	 */
	public float getGelkmarosElyosInfluence()
	{
		return gelkmaros_e;
	}
	
	/**
	 * Retrieves the current influence level for Asmodians in the Gelkmaros region.<br>
	 * This value is used to determine regional control.
	 * @return The {@code float} value representing Asmodian influence.
	 */
	public float getGelkmarosAsmodiansInfluence()
	{
		return gelkmaros_a;
	}
	
	/**
	 * Retrieves the current influence value for the Balaurs in Gelkmaros.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Balaur influence.
	 */
	public float getGelkmarosBalaursInfluence()
	{
		return gelkmaros_b;
	}
	
	/**
	 * Retrieves the current influence level for Elyos in the Abyss region.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Elyos influence in the Abyss.
	 */
	public float getAbyssElyosInfluence()
	{
		return abyss_e;
	}
	
	/**
	 * Retrieves the current influence level for Asmodians in the Abyss region.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Asmodian influence.
	 */
	public float getAbyssAsmodiansInfluence()
	{
		return abyss_a;
	}
	
	/**
	 * Retrieves the current influence value for the Balaurs in the Abyss region.<br>
	 * This value is used to determine regional control.
	 * @return The {@code float} value representing Balaur influence.
	 */
	public float getAbyssBalaursInfluence()
	{
		return abyss_b;
	}
	
	/**
	 * Retrieves the current influence score for Elyos in the Kaldor region.<br>
	 * This value is used to determine regional control.
	 * @return The {@code float} value representing the Elyos influence.
	 */
	public float getKaldorElyosInfluence()
	{
		return kaldor_e;
	}
	
	/**
	 * Retrieves the current influence score for Asmodians in the Kaldor region.<br>
	 * This value is used to determine regional control.
	 * @return The {@code float} value representing the total Asmodian influence.
	 */
	public float getKaldorAsmodiansInfluence()
	{
		return kaldor_a;
	}
	
	/**
	 * Retrieves the current influence score for the Balaurs in Kaldor.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} influence value for Balaurs in Kaldor.
	 */
	public float getKaldorBalaursInfluence()
	{
		return kaldor_b;
	}
	
	/**
	 * Retrieves the current influence level for Elyos in the Belus region.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing the Elyos influence score.
	 */
	public float getBelusElyosInfluence()
	{
		return belus_e;
	}
	
	/**
	 * Retrieves the current influence level for the Asmodian faction in Belus.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Asmodian influence.
	 */
	public float getBelusAsmodiansInfluence()
	{
		return belus_a;
	}
	
	/**
	 * Retrieves the current influence value for the Balaurs in Belus.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} influence score for Balaurs in Belus.
	 */
	public float getBelusBalaursInfluence()
	{
		return belus_b;
	}
	
	/**
	 * Retrieves the current influence value for the Elyos faction in Aspida.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Elyos influence in Aspida.
	 */
	public float getAspidaElyosInfluence()
	{
		return aspida_e;
	}
	
	/**
	 * Retrieves the current influence score for the Asmodian faction in Aspida.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Asmodian influence.
	 */
	public float getAspidaAsmodiansInfluence()
	{
		return aspida_a;
	}
	
	/**
	 * Retrieves the current influence value for the Balaurs in Aspida.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Balaur influence in Aspida.
	 */
	public float getAspidaBalaursInfluence()
	{
		return aspida_b;
	}
	
	/**
	 * Retrieves the current influence value for Elyos in Atanatos.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Elyos influence.
	 */
	public float getAtanatosElyosInfluence()
	{
		return atanatos_e;
	}
	
	/**
	 * Retrieves the current influence value for the Asmodian faction in Atanatos.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Asmodian influence.
	 */
	public float getAtanatosAsmodiansInfluence()
	{
		return atanatos_a;
	}
	
	/**
	 * Retrieves the current influence value for Balaurs in Atanatos.<br>
	 * This value is used to determine regional control.
	 * @return The {@code float} value representing Balaur influence.
	 */
	public float getAtanatosBalaursInfluence()
	{
		return atanatos_b;
	}
	
	/**
	 * Retrieves the current influence value for Elyos in Disillon.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Elyos influence.
	 */
	public float getDisillonElyosInfluence()
	{
		return disillon_e;
	}
	
	/**
	 * Retrieves the current influence level for the Asmodian faction in Disillon.<br>
	 * This value is used to determine territory control.
	 * @return The {@code float} value representing Asmodian influence.
	 */
	public float getDisillonAsmodiansInfluence()
	{
		return disillon_a;
	}
	
	/**
	 * Retrieves the current influence value for Balaurs in Disillon.<br>
	 * This value is used to determine the power balance of this specific region.
	 * @return The {@code float} value representing Balaur influence.
	 */
	public float getDisillonBalaursInfluence()
	{
		return disillon_b;
	}
	
	/**
	 * Calculates the PvP race bonus based on global influence levels.<br>
	 * This method checks the current influence of {@code ELYOS} and {@code ASMODIANS}.<br>
	 * It returns a multiplier that increases depending on how dominant one race is over the other.
	 * @param attRace The race being checked for its bonus.
	 * @return A {@code float} representing the calculated PvP bonus multiplier.
	 */
	public float getPvpRaceBonus(Race attRace)
	{
		float bonus = 1;
		final float elyos = getGlobalElyosInfluence();
		final float asmo = getGlobalAsmodiansInfluence();
		switch (attRace)
		{
			case ASMODIANS:
				if ((elyos >= 0.81f) && (asmo <= 0.10f))
				{
					bonus = 1.2f;
				}
				else if ((elyos >= 0.81f) || ((elyos >= 0.71f) && (asmo <= 0.10f)))
				{
					bonus = 1.15f;
				}
				else if (elyos >= 0.71f)
				{
					bonus = 1.1f;
				}
				break;
			case ELYOS:
				if ((asmo >= 0.81f) && (elyos <= 0.10f))
				{
					bonus = 1.2f;
				}
				else if ((asmo >= 0.81f) || ((asmo >= 0.71f) && (elyos <= 0.10f)))
				{
					bonus = 1.15f;
				}
				else if (asmo >= 0.71f)
				{
					bonus = 1.1f;
				}
				break;
			default:
				break;
		}
		
		return bonus;
	}
}
