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
package com.aionemu.gameserver.model.templates.quest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the rewards granted to a player upon completing a quest.<br>
 * This class holds the collection of items or benefits associated with a {@code Quest}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rewards", propOrder =
{
	"selectableRewardItem",
	"rewardItem"
})
public class Rewards
{
	@XmlElement(name = "selectable_reward_item")
	protected List<QuestItems> selectableRewardItem;
	@XmlElement(name = "reward_item")
	protected List<QuestItems> rewardItem;
	@XmlAttribute
	protected Long gold;
	@XmlAttribute
	protected Long exp;
	@XmlAttribute(name = "reward_abyss_point")
	protected Integer rewardAbyssPoint;
	@XmlAttribute(name = "reward_glory_point")
	protected Integer rewardGloryPoint;
	@XmlAttribute(name = "expBoost")
	protected Integer expBoost;
	@XmlAttribute(name = "reward_creativity_point")
	protected Integer rewardCP;
	@XmlAttribute
	protected Integer title;
	@XmlAttribute(name = "extend_inventory")
	protected Integer extendInventory;
	@XmlAttribute(name = "extend_stigma")
	protected Integer extendStigma;
	@XmlAttribute(name = "fame_exp")
	protected Integer fameExp;
	
	/**
	 * Retrieves the list of items that a player can choose as a reward.<br>
	 * This method ensures that an empty {@code ArrayList} is returned if the list is null.
	 * @return A {@code List} of {@link QuestItems} available for selection.
	 */
	public List<QuestItems> getSelectableRewardItem()
	{
		if (selectableRewardItem == null)
		{
			selectableRewardItem = new ArrayList<>();
		}
		
		return selectableRewardItem;
	}
	
	/**
	 * Retrieves the list of items rewarded for completing a quest.<br>
	 * This method ensures that an empty {@code ArrayList} is returned if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems}.
	 */
	public List<QuestItems> getRewardItem()
	{
		if (rewardItem == null)
		{
			rewardItem = new ArrayList<>();
		}
		
		return rewardItem;
	}
	
	/**
	 * Retrieves the amount of gold awarded.<br>
	 * This value is stored as a {@code Long}.
	 * @return The amount of gold.
	 */
	public Long getGold()
	{
		return gold;
	}
	
	/**
	 * Retrieves the experience points granted as a reward.<br>
	 * This value is stored in the {@code exp} field.
	 * @return the amount of experience points as a {@code Long}.
	 */
	public Long getExp()
	{
		return exp;
	}
	
	/**
	 * Retrieves the number of abyss points awarded as a reward.<br>
	 * This value is stored in the {@code rewardAbyssPoint} field.
	 * @return The amount of abyss points as an {@code Integer}.
	 */
	public Integer getRewardAbyssPoint()
	{
		return rewardAbyssPoint;
	}
	
	/**
	 * Retrieves the amount of glory points awarded.<br>
	 * This value is stored in the {@code rewardGloryPoint} field.
	 * @return The number of glory points as an {@code Integer}.
	 */
	public Integer getRewardGloryPoint()
	{
		return rewardGloryPoint;
	}
	
	/**
	 * Retrieves the experience boost value.<br>
	 * This value is stored in the {@code expBoost} field.
	 * @return the {@code Integer} value of the experience boost.
	 */
	public Integer getExpBoost()
	{
		return expBoost;
	}
	
	/**
	 * Retrieves the Creativity Point (CP) reward value.<br>
	 * This value is stored in the {@code rewardCP} field.
	 * @return The amount of CP awarded as an {@code Integer}.
	 */
	public Integer getRewardCP()
	{
		return rewardCP;
	}
	
	/**
	 * Retrieves the title of the achievement action.<br>
	 * This value is stored as an {@code Integer}.
	 * @return The title of the achievement action, or {@code null} if it is not set.
	 */
	public Integer getTitle()
	{
		return title;
	}
	
	/**
	 * Retrieves the value of the {@code extendInventory} attribute.<br>
	 * This value determines if the inventory should be expanded.
	 * @return The integer value of the extended inventory property.
	 */
	public Integer getExtendInventory()
	{
		return extendInventory;
	}
	
	/**
	 * Retrieves the value of the {@code extend_stigma} attribute.<br>
	 * This value determines if the stigma is extended for the quest reward.
	 * @return The integer value of the {@code extendStigma} property.
	 */
	public Integer getExtendStigma()
	{
		return extendStigma;
	}
	
	/**
	 * Retrieves the amount of fame experience granted as a reward.<br>
	 * This value is stored in the {@code fameExp} field.
	 * @return The amount of fame experience as an {@code Integer}.
	 */
	public Integer getFameExp()
	{
		return fameExp;
	}
}
