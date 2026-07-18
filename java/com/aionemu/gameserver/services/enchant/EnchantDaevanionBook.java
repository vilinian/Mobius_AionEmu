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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DAEVANION_SKILL_ENCHANT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillLearnTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for using a Daevanion Book to enchant skills.<br>
 * This class processes the item usage and updates the {@link PlayerSkillList} accordingly.
 */
public class EnchantDaevanionBook
{
	/**
	 * Enchants a specific Daevanion skill for the player.<br>
	 * This method checks for required Kinah and consumes items to attempt an enchantment.<br>
	 * It handles success or failure logic based on a random chance.
	 * @param player The {@link Player} who is performing the enchantment.
	 * @param skillId The unique identifier of the skill to be enchanted.
	 * @param bookObjId The object ID of the Daevanion book used for the process.
	 * @param materials The object ID of the material required if it is not 0.
	 */
	public static void enchantDaevanionSkill(Player player, int skillId, int bookObjId, int materials)
	{
		final Item parentItem = player.getInventory().getItemByObjId(bookObjId);
		final ItemTemplate template = parentItem.getItemTemplate();
		final int nameId = template.getNameId();
		final PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		final boolean isSuccess = Rnd.chance(75);
		final int currentEnchant = skill.getSkillLevel();
		if (player.getInventory().getKinah() < 100000)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
			return;
		}
		
		final ItemUseObserver moveObserver = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(nameId)));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 2, 0), true);
			}
		};
		player.getObserveController().attach(moveObserver);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(() ->
		{
			int enchantLevel;
			player.getController().cancelTask(TaskId.ITEM_USE);
			player.getObserveController().removeObserver(moveObserver);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 1), true);
			if (!player.getInventory().decreaseByObjectId(bookObjId, 1L))
			{
				return;
			}
			
			player.getInventory().decreaseKinah(100000L);
			if (materials != 0)
			{
				player.getInventory().decreaseByObjectId(materials, 1L);
			}
			
			if (isSuccess)
			{
				enchantLevel = currentEnchant + 1;
				skill.setSkillLvl(enchantLevel);
				player.getSkillList().addSkill(player, skill.getSkillId(), enchantLevel);
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
				PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_ENCHANT(skillId, skill.getSkillLevel(), currentEnchant));
			}
			else
			{
				enchantLevel = currentEnchant - 1;
				skill.setSkillLvl(enchantLevel);
				player.getSkillList().addSkill(player, skill.getSkillId(), enchantLevel);
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
				PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_ENCHANT(skillId, skill.getSkillLevel(), currentEnchant));
			}
			
			if (currentEnchant >= 15)
			{
				final SkillLearnTemplate[] skillTemplates = DataManager.SKILL_TREE_DATA.getTemplatesFor(player.getPlayerClass(), player.getLevel(), player.getRace());
				final PlayerSkillList playerSkillList = player.getSkillList();
				for (SkillLearnTemplate template1 : skillTemplates)
				{
					if (template1.getRequiredSkill() != skillId)
					{
						continue;
					}
					
					playerSkillList.addSkill(player, template1.getSkillId(), 1);
				}
			}
		}, 1000));
	}
}
