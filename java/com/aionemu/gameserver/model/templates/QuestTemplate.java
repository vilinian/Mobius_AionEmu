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
package com.aionemu.gameserver.model.templates;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.InventoryItems;
import com.aionemu.gameserver.model.templates.quest.QuestBonuses;
import com.aionemu.gameserver.model.templates.quest.QuestCategory;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.model.templates.quest.QuestExtraCategory;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestKill;
import com.aionemu.gameserver.model.templates.quest.QuestMentorType;
import com.aionemu.gameserver.model.templates.quest.QuestRepeatCycle;
import com.aionemu.gameserver.model.templates.quest.QuestTarget;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.model.templates.quest.Rewards;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;

/**
 * Represents the static data and configuration for a quest in the game.<br>
 * This class serves as a template used to define requirements, rewards, and objectives for {@code Quest} instances.
 * @author MrPoke
 * @modified vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quest")
public class QuestTemplate
{
	@XmlElement(name = "collect_items")
	protected CollectItems collectItems;
	@XmlElement(name = "inventory_items")
	protected InventoryItems inventoryItems;
	@XmlElement(name = "rewards")
	protected List<Rewards> rewards;
	@XmlElement(name = "bonus")
	protected List<QuestBonuses> bonus;
	@XmlElement(name = "extended_rewards")
	protected List<Rewards> extendedRewards;
	@XmlElement(name = "quest_drop")
	protected List<QuestDrop> questDrop;
	@XmlElement(name = "quest_kill")
	protected List<QuestKill> questKill;
	@XmlElement(name = "start_conditions")
	protected List<XMLStartCondition> startConds;
	@XmlList
	@XmlElement(name = "class_permitted")
	protected List<PlayerClass> classPermitted;
	@XmlElement(name = "gender_permitted")
	protected Gender genderPermitted;
	@XmlElement(name = "quest_work_items")
	protected QuestWorkItems questWorkItems;
	@XmlElement(name = "fighter_selectable_reward")
	protected List<QuestItems> fighterSelectableReward;
	@XmlElement(name = "knight_selectable_reward")
	protected List<QuestItems> knightSelectableReward;
	@XmlElement(name = "ranger_selectable_reward")
	protected List<QuestItems> rangerSelectableReward;
	@XmlElement(name = "assassin_selectable_reward")
	protected List<QuestItems> assassinSelectableReward;
	@XmlElement(name = "wizard_selectable_reward")
	protected List<QuestItems> wizardSelectableReward;
	@XmlElement(name = "elementalist_selectable_reward")
	protected List<QuestItems> elementalistSelectableReward;
	@XmlElement(name = "priest_selectable_reward")
	protected List<QuestItems> priestSelectableReward;
	@XmlElement(name = "chanter_selectable_reward")
	protected List<QuestItems> chanterSelectableReward;
	@XmlElement(name = "gunner_selectable_reward")
	protected List<QuestItems> gunnerSelectableReward;
	@XmlElement(name = "bard_selectable_reward")
	protected List<QuestItems> bardSelectableReward;
	@XmlElement(name = "painter_selectable_reward")
	protected List<QuestItems> painterSelectableReward;
	@XmlElement(name = "rider_selectable_reward")
	protected List<QuestItems> riderSelectableReward;
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "nameId")
	protected Integer nameId;
	@XmlAttribute(name = "minlevel_permitted")
	protected Integer minlevelPermitted;
	@XmlAttribute(name = "maxlevel_permitted")
	protected int maxlevelPermitted;
	@XmlAttribute(name = "max_repeat_count")
	protected Integer maxRepeatCount;
	@XmlAttribute(name = "quest_cooltime")
	protected int questCooltime;
	@XmlAttribute(name = "rank")
	private int rank;
	@XmlAttribute(name = "cannot_share")
	protected Boolean cannotShare;
	@XmlAttribute(name = "cannot_giveup")
	protected Boolean cannotGiveup;
	@XmlAttribute(name = "can_report")
	protected Boolean canReport;
	@XmlAttribute(name = "use_class_reward")
	protected Integer useClassReward;
	@XmlAttribute(name = "race_permitted")
	protected Race racePermitted;
	@XmlAttribute(name = "combineskill")
	protected Integer combineskill;
	@XmlAttribute(name = "combine_skillpoint")
	protected Integer combineSkillpoint;
	@XmlAttribute(name = "timer")
	protected Boolean timer;
	@XmlAttribute(name = "category")
	protected QuestCategory category;
	@XmlAttribute(name = "extra_category")
	protected QuestExtraCategory extraCategory;
	@XmlAttribute(name = "repeat_cycle")
	protected List<QuestRepeatCycle> repeatCycle;
	@XmlAttribute(name = "npcfaction_id")
	protected int npcFactionId;
	@XmlAttribute(name = "mentor_type")
	protected QuestMentorType mentorType = QuestMentorType.NONE;
	@XmlAttribute(name = "target")
	private QuestTarget target = QuestTarget.NONE;
	@XmlAttribute(name = "titleId")
	protected int titleId;
	
	/**
	 * Retrieves the items that need to be collected for this quest.<br>
	 * This method returns the {@code CollectItems} object associated with the template.
	 * @return the {@code CollectItems} data.
	 */
	public CollectItems getCollectItems()
	{
		return collectItems;
	}
	
	/**
	 * Retrieves the {@link InventoryItems} associated with this quest.<br>
	 * This method returns the list of items required for the quest.
	 * @return the {@code InventoryItems} object.
	 */
	public InventoryItems getInventoryItems()
	{
		return inventoryItems;
	}
	
	/**
	 * Retrieves the list of rewards for this quest.<br>
	 * If no rewards are defined, it returns an empty {@code List}.
	 * @return a {@code List} of {@link Rewards} objects.
	 */
	public List<Rewards> getRewards()
	{
		if (rewards == null)
		{
			rewards = new ArrayList<>();
		}
		
		return rewards;
	}
	
	/**
	 * Retrieves the list of extended rewards for this quest.<br>
	 * If no extended rewards exist, it returns an empty {@code List}.
	 * @return A {@code List} of {@link Rewards} objects.
	 */
	public List<Rewards> getExtendedRewards()
	{
		if (extendedRewards == null)
		{
			extendedRewards = new ArrayList<>();
		}
		
		return extendedRewards;
	}
	
	/**
	 * Retrieves the list of bonuses associated with this quest.<br>
	 * If no bonuses exist, it returns an empty {@code List}.
	 * @return a {@code List} of {@link QuestBonuses} objects.
	 */
	public List<QuestBonuses> getBonus()
	{
		if (bonus == null)
		{
			bonus = new ArrayList<>();
		}
		
		return bonus;
	}
	
	/**
	 * Retrieves the list of items dropped by this quest.<br>
	 * If no drops are defined, it returns an empty {@code List}.
	 * @return a {@code List} of {@link QuestDrop} objects.
	 */
	public List<QuestDrop> getQuestDrop()
	{
		if (questDrop == null)
		{
			questDrop = new ArrayList<>();
		}
		
		return questDrop;
	}
	
	/**
	 * Retrieves the list of required kills for this quest.<br>
	 * If no kills are defined, it returns an empty {@code List}.
	 * @return A {@code List} of {@link QuestKill} objects.
	 */
	public List<QuestKill> getQuestKill()
	{
		if (questKill == null)
		{
			questKill = new ArrayList<>();
		}
		
		return questKill;
	}
	
	/**
	 * Retrieves the list of conditions required to start a quest.<br>
	 * If no conditions are defined, it returns an empty {@code List}.
	 * @return A {@code List} of {@link XMLStartCondition} objects.
	 */
	public List<XMLStartCondition> getXMLStartConditions()
	{
		if (startConds == null)
		{
			startConds = new ArrayList<>();
		}
		
		return startConds;
	}
	
	/**
	 * Retrieves the list of player classes allowed to complete this quest.<br>
	 * If no specific classes are defined, it returns an empty {@code List}.
	 * @return a {@code List} of {@link PlayerClass} objects.
	 */
	public List<PlayerClass> getClassPermitted()
	{
		if (classPermitted == null)
		{
			classPermitted = new ArrayList<>();
		}
		
		return classPermitted;
	}
	
	/**
	 * Retrieves the allowed {@link Gender} for this quest.<br>
	 * This method returns the value stored in the {@code genderPermitted} field.
	 * @return The permitted {@code Gender} for the quest.
	 */
	public Gender getGenderPermitted()
	{
		return genderPermitted;
	}
	
	/**
	 * Retrieves the work items associated with this quest.<br>
	 * This method returns the {@code QuestWorkItems} object for the template.
	 * @return the {@code QuestWorkItems} of the quest.
	 */
	public QuestWorkItems getQuestWorkItems()
	{
		return questWorkItems;
	}
	
	/**
	 * Retrieves the list of rewards available for the Fighter class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Fighter class.
	 */
	public List<QuestItems> getFighterSelectableReward()
	{
		if (fighterSelectableReward == null)
		{
			fighterSelectableReward = new ArrayList<>();
		}
		
		return fighterSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards available for the Knight class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Knight class.
	 */
	public List<QuestItems> getKnightSelectableReward()
	{
		if (knightSelectableReward == null)
		{
			knightSelectableReward = new ArrayList<>();
		}
		
		return knightSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards available for the Ranger class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Ranger class.
	 */
	public List<QuestItems> getRangerSelectableReward()
	{
		if (rangerSelectableReward == null)
		{
			rangerSelectableReward = new ArrayList<>();
		}
		
		return rangerSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards selectable by the Assassin class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Assassin class.
	 */
	public List<QuestItems> getAssassinSelectableReward()
	{
		if (assassinSelectableReward == null)
		{
			assassinSelectableReward = new ArrayList<>();
		}
		
		return assassinSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards available for the Wizard class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Wizard class.
	 */
	public List<QuestItems> getWizardSelectableReward()
	{
		if (wizardSelectableReward == null)
		{
			wizardSelectableReward = new ArrayList<>();
		}
		
		return wizardSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards available for the Elementalist class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Elementalist class.
	 */
	public List<QuestItems> getElementalistSelectableReward()
	{
		if (elementalistSelectableReward == null)
		{
			elementalistSelectableReward = new ArrayList<>();
		}
		
		return elementalistSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards available for the Priest class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Priest class.
	 */
	public List<QuestItems> getPriestSelectableReward()
	{
		if (priestSelectableReward == null)
		{
			priestSelectableReward = new ArrayList<>();
		}
		
		return priestSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards selectable by the Chanter class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Chanter class.
	 */
	public List<QuestItems> getChanterSelectableReward()
	{
		if (chanterSelectableReward == null)
		{
			chanterSelectableReward = new ArrayList<>();
		}
		
		return chanterSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards selectable by the Gunner class.<br>
	 * This method ensures a non-null {@code List} is returned.
	 * @return A {@code List} of {@link QuestItems} for the Gunner class.
	 */
	public List<QuestItems> getGunnerSelectableReward()
	{
		if (gunnerSelectableReward == null)
		{
			gunnerSelectableReward = new ArrayList<>();
		}
		
		return gunnerSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards selectable by the Bard class.<br>
	 * This method returns an empty {@code List} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Bard class.
	 */
	public List<QuestItems> getBardSelectableReward()
	{
		if (bardSelectableReward == null)
		{
			bardSelectableReward = new ArrayList<>();
		}
		
		return bardSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards selectable by the Painter class.<br>
	 * This method ensures a non-null {@code List} is returned.
	 * @return A {@code List} of {@link QuestItems} for the Painter class.
	 */
	public List<QuestItems> getPainterSelectableReward()
	{
		if (painterSelectableReward == null)
		{
			painterSelectableReward = new ArrayList<>();
		}
		
		return painterSelectableReward;
	}
	
	/**
	 * Retrieves the list of rewards selectable by a Rider.<br>
	 * This method returns an empty {@code ArrayList} if no rewards are defined.
	 * @return A {@code List} of {@link QuestItems} for the Rider class.
	 */
	public List<QuestItems> getRiderSelectableReward()
	{
		if (riderSelectableReward == null)
		{
			riderSelectableReward = new ArrayList<>();
		}
		
		return riderSelectableReward;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the unique identifier for the quest name.<br>
	 * This value is used to identify the quest in the database.
	 * @return The {@code Integer} ID of the quest name.
	 */
	public Integer getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the minimum level required to start this quest.<br>
	 * This value is used to check if a player meets the level requirements.
	 * @return the {@code Integer} representing the minimum permitted level.
	 */
	public Integer getMinlevelPermitted()
	{
		return minlevelPermitted;
	}
	
	/**
	 * Retrieves the maximum level allowed for this quest.<br>
	 * This value determines if a player meets the level requirement.
	 * @return the maximum permitted level as an {@code int}.
	 */
	public int getMaxlevelPermitted()
	{
		return maxlevelPermitted;
	}
	
	/**
	 * Retrieves the minimum rank required to complete this quest.<br>
	 * This value is used to check if a player meets the level requirements.
	 * @return The required rank as an {@code int}.
	 */
	public int getRequiredRank()
	{
		return rank;
	}
	
	/**
	 * Retrieves the maximum number of times a quest can be repeated.<br>
	 * It returns {@code 1} if the value is {@code null} or less than {@code 2}.<br>
	 * Otherwise, it returns the stored {@code maxRepeatCount}.
	 * @return The maximum repeat count as an {@link Integer}.
	 */
	public Integer getMaxRepeatCount()
	{
		if ((maxRepeatCount == null) || !(maxRepeatCount > 1))
		{
			return 1;
		}
		
		return maxRepeatCount;
	}
	
	/**
	 * Checks if the quest is restricted from being shared.<br>
	 * Returns {@code false} if the value is {@code null}.
	 * @return {@code true} if sharing is disabled, otherwise {@code false}.
	 */
	public boolean isCannotShare()
	{
		if (cannotShare == null)
		{
			return false;
		}
		
		return cannotShare;
	}
	
	/**
	 * Checks if the quest has a "cannot give up" status.<br>
	 * This method returns {@code true} if the value is set to {@code true}.<br>
	 * It returns {@code false} if the value is {@code null} or {@code false}.
	 * @return {@code true} if the quest cannot be given up, otherwise {@code false}.
	 */
	public boolean isCannotGiveup()
	{
		if (cannotGiveup == null)
		{
			return false;
		}
		
		return cannotGiveup;
	}
	
	/**
	 * Checks if the quest is eligible to be reported.<br>
	 * This method returns {@code false} if the report status is {@code null}.
	 * @return {@code true} if reporting is allowed, otherwise {@code false}.
	 */
	public boolean isCanReport()
	{
		if (canReport == null)
		{
			return false;
		}
		
		return canReport;
	}
	
	/**
	 * Checks if the quest uses a single class reward.<br>
	 * This method verifies if the {@code useClassReward} value is equal to {@code 1}.
	 * @return {@code true} if the reward is restricted to a single class, {@code false} otherwise.
	 */
	public boolean isUseSingleClassReward()
	{
		if (useClassReward == null)
		{
			return false;
		}
		
		return useClassReward == 1;
	}
	
	/**
	 * Checks if the quest uses repeated class rewards.<br>
	 * This method verifies if the {@code useClassReward} value is equal to {@code 2}.
	 * @return {@code true} if repeated class rewards are enabled, otherwise {@code false}.
	 */
	public boolean isUseRepeatedClassReward()
	{
		if (useClassReward == null)
		{
			return false;
		}
		
		return useClassReward == 2;
	}
	
	/**
	 * Checks if this quest can be completed more than once.<br>
	 * It returns {@code true} if the maximum repeat count is greater than {@code 1}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if repeatable, {@code false} otherwise.
	 */
	public boolean isRepeatable()
	{
		return getMaxRepeatCount() > 1;
	}
	
	/**
	 * Retrieves the {@link Race} allowed for this quest.<br>
	 * This method returns the value stored in the {@code racePermitted} field.
	 * @return the permitted {@code Race} object.
	 */
	public Race getRacePermitted()
	{
		return racePermitted;
	}
	
	/**
	 * Retrieves the combined skill value for this quest.
	 * @return The {@code Integer} value of the combined skill.
	 */
	public Integer getCombineSkill()
	{
		return combineskill;
	}
	
	/**
	 * Retrieves the total combined skill points for this quest.<br>
	 * This value is used to determine the overall skill point reward.
	 * @return The {@code Integer} value of the combined skill points.
	 */
	public Integer getCombineSkillPoint()
	{
		return combineSkillpoint;
	}
	
	/**
	 * Checks if the quest has an associated timer.<br>
	 * Returns {@code true} if a timer exists.<br>
	 * Returns {@code false} if the timer is {@code null}.
	 * @return {@code true} if there is a timer, otherwise {@code false}.
	 */
	public boolean isTimer()
	{
		if (timer == null)
		{
			return false;
		}
		
		return timer;
	}
	
	/**
	 * Retrieves the category of the quest.<br>
	 * If no category is defined, it defaults to {@code QuestCategory.QUEST}.
	 * @return the {@link QuestCategory} associated with this template.
	 */
	public QuestCategory getCategory()
	{
		if (category == null)
		{
			category = QuestCategory.QUEST;
		}
		
		return category;
	}
	
	/**
	 * Retrieves the additional category for this quest.<br>
	 * If no category is defined, it returns {@code QuestExtraCategory.NONE}.
	 * @return the {@link QuestExtraCategory} associated with this quest.
	 */
	public QuestExtraCategory getExtraCategory()
	{
		if (extraCategory == null)
		{
			extraCategory = QuestExtraCategory.NONE;
		}
		
		return extraCategory;
	}
	
	/**
	 * Checks if this quest template is associated with a mentor type.<br>
	 * It returns {@code true} if the mentor type is not {@code NONE}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the quest has a mentor status, {@code false} otherwise.
	 */
	public boolean isMentor()
	{
		return mentorType != QuestMentorType.NONE;
	}
	
	/**
	 * Retrieves the mentor type associated with this quest.<br>
	 * This value determines which mentor category the quest belongs to.
	 * @return the {@code QuestMentorType} of the quest.
	 */
	public QuestMentorType getMentorType()
	{
		return mentorType;
	}
	
	/**
	 * Retrieves the primary target for this quest.<br>
	 * This method returns the {@code QuestTarget} object associated with the template.
	 * @return the {@code QuestTarget} of the quest.
	 */
	public QuestTarget getTarget()
	{
		return target;
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
	 * Retrieves the list of repeat cycles for this quest.<br>
	 * This method returns all {@link QuestRepeatCycle} objects associated with the template.
	 * @return A {@code List} of {@code QuestRepeatCycle} objects.
	 */
	public List<QuestRepeatCycle> getRepeatCycle()
	{
		return repeatCycle;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC faction.<br>
	 * This ID determines which group the NPC belongs to.
	 * @return The {@code int} value of the NPC faction ID.
	 */
	public int getNpcFactionId()
	{
		return npcFactionId;
	}
	
	/**
	 * Checks if the quest is based on a time cycle.<br>
	 * It returns {@code true} if the {@code repeatCycle} field is not {@code null}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the quest has a time-based cycle, {@code false} otherwise.
	 */
	public boolean isTimeBased()
	{
		return repeatCycle != null;
	}
	
	/**
	 * Retrieves the cooldown time for this quest.<br>
	 * This value determines how long a player must wait before repeating the quest.
	 * @return The cooldown time as an {@code int}.
	 */
	public int getQuestCoolTime()
	{
		return questCooltime;
	}
	
	/**
	 * Checks if the quest is a daily quest.<br>
	 * This method verifies if the quest is time-based and has a repeating cycle of {@code ALL}.
	 * @return {@code true} if the quest is daily, otherwise {@code false}.
	 */
	public boolean isDaily()
	{
		return isTimeBased() && (repeatCycle.size() == 1) && (repeatCycle.get(0) == QuestRepeatCycle.ALL);
	}
	
	/**
	 * Checks if the quest instance type is weekly.<br>
	 * This method returns {@code true} if it is time-based but not daily.
	 * @return {@code true} if the instance is weekly, otherwise {@code false}.
	 */
	public boolean isWeekly()
	{
		return isTimeBased() && !isDaily();
	}
	
	/**
	 * Checks if this quest template is a master quest.<br>
	 * This determines if the quest has a specific skill point value.
	 * @return {@code true} if the quest is a master, {@code false} otherwise.
	 */
	public boolean isMaster()
	{
		return (getCombineSkillPoint() != null) && (getCombineSkillPoint() == 499);
	}
	
	/**
	 * Checks if the quest is designated for experts.<br>
	 * This returns {@code true} only if the combine skill point is exactly {@code 399}.
	 * @return {@code true} if the quest is an expert quest, otherwise {@code false}.
	 */
	public boolean isExpert()
	{
		return (getCombineSkillPoint() != null) && (getCombineSkillPoint() == 399);
	}
	
	/**
	 * Checks if the quest belongs to a specific category.<br>
	 * It returns {@code true} if the category is {@code NON_COUNT} or {@code EVENT}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the quest is non-count or an event, {@code false} otherwise.
	 */
	public boolean isNoCount()
	{
		return category.equals(QuestCategory.NON_COUNT) || category.equals(QuestCategory.EVENT);
	}
}
