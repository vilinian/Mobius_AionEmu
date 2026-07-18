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
package com.aionemu.gameserver.services.enchant;

import java.util.ArrayList;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DAEVANION_SKILL_FUSION;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for combining Daevanion books.<br>
 * This service processes requests to fuse items and manages the resulting outcomes.<br>
 * It interacts with {@link Player} data and uses {@link ItemService} to update inventories.
 */
public class CombineDaevanionBook
{
	/**
	 * Combines Daevanion books to create a new skill book.<br>
	 * This method removes the required items from the {@code Player} inventory.<br>
	 * It determines the resulting item based on the player class and a random chance.<br>
	 * Finally, it sends a fusion packet and adds the new item to the player.
	 * @param player The {@link Player} who is performing the combination.
	 * @param sacrificeBook A list of {@code Integer} IDs representing the books to be sacrificed.
	 */
	public static void combineDaevanionBook(Player player, ArrayList<Integer> sacrificeBook)
	{
		for (int sacrifices : sacrificeBook)
		{
			player.getInventory().decreaseByObjectId(sacrifices, 1L);
		}
		
		int result = 0;
		final int chance = Rnd.get(0, 3);
		switch (player.getPlayerClass())
		{
			case GLADIATOR:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501640, 169501645);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501784, 169501785);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501806, 169501807);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501773;
				break;
			}
			case TEMPLAR:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501646, 169501651);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501786, 169501787);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501808, 169501809);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501774;
				break;
			}
			case ASSASSIN:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501652, 169501657);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501788, 169501789);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501810, 169501811);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501775;
				break;
			}
			case RANGER:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501658, 169501663);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501790, 169501791);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501812, 169501813);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501776;
				break;
			}
			case SORCERER:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501676, 169501681);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501792, 169501793);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501818, 169501819);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501777;
				break;
			}
			case SPIRIT_MASTER:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501682, 169501687);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501794, 169501795);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501820, 169501821);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501778;
				break;
			}
			case CLERIC:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501664, 169501669);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501796, 169501797);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501816, 169501817);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501779;
				break;
			}
			case CHANTER:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501670, 169501675);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501798, 169501799);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501814, 169501815);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501780;
				break;
			}
			case GUNNER:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501688, 169501693);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501800, 169501801);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501826, 169501827);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501781;
				break;
			}
			case BARD:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501700, 169501705);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501802, 169501803);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501822, 169501823);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501782;
				break;
			}
			case RIDER:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501694, 169501699);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501804, 169501805);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501824, 169501825);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501783;
				break;
			}
			case PAINTER:
			{
				if (chance == 0)
				{
					result = Rnd.get(169501872, 169501877);
				}
				
				if (chance == 1)
				{
					result = Rnd.get(169501878, 169501879);
				}
				
				if (chance == 2)
				{
					result = Rnd.get(169501880, 169501881);
				}
				
				if (chance != 3)
				{
					break;
				}
				
				result = 169501882;
			}
			default:
				break;
		}
		
		PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_FUSION(1, result));
		ItemService.addItem(player, result, 1L);
	}
}
