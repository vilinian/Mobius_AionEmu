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
package system.handlers.ai.siege;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.ActionItemNpcAI2;

/**
 * Handles the artificial intelligence for the {@code emptyaethericcannon} object.<br>
 * This class manages how the cannon behaves during siege events.<br>
 * It extends {@link ActionItemNpcAI2} to provide specific logic for this entity.
 * @author Ranastic
 */
@AIName("emptyaethericcannon")
public class EmptyAethericCannonAI2 extends ActionItemNpcAI2
{
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It checks if the {@code player} has the required item in their inventory.<br>
	 * If the item exists, it calls {@code handleUseItemStart}.<br>
	 * Otherwise, it sends a system message to the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		if (player.getInventory().getItemCountByItemId(186000246) > 0)
		{
			// Magic Cannonball.
			super.handleUseItemStart(player);
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(2281763));
		}
	}
	
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		player.getController().stopProtectionActiveTask();
		SkillEngine.getInstance().getSkill(player, 21385, 1, player).useNoAnimationSkill();
		AI2Actions.deleteOwner(this);
		AI2Actions.scheduleRespawn(this);
	}
}
