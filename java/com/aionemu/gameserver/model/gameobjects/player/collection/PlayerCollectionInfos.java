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
package com.aionemu.gameserver.model.gameobjects.player.collection;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.collection.CollectionExpTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionType;

/**
 * This class manages the collection progress and information for a {@link Player}.<br>
 * It tracks various collection types and handles related statistics.<br>
 * It serves as a data container for all player-specific collection achievements.
 */
public class PlayerCollectionInfos implements StatOwner
{
	private final CollectionType type;
	private int level;
	private int exp;
	private final List<IStatFunction> functions = new ArrayList<>();
	
	/**
	 * Creates a new instance of {@code PlayerCollectionInfos}.<br>
	 * This constructor initializes the collection data for a player.
	 * @param type The {@link CollectionType} of the collection.
	 * @param level The current level of the collection.
	 * @param exp The current experience points of the collection.
	 */
	public PlayerCollectionInfos(CollectionType type, int level, int exp)
	{
		this.type = type;
		this.level = level;
		this.exp = exp;
	}
	
	/**
	 * Retrieves the specific type of the collection.<br>
	 * This identifies which category this information belongs to.
	 * @return The {@code CollectionType} associated with this object.
	 */
	public CollectionType getType()
	{
		return type;
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
	 * Sets the current level of the collection.<br>
	 * This updates the {@code level} field with the new value.
	 * @param level The new level to assign to this collection.
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	/**
	 * Retrieves the current experience points for this collection.<br>
	 * This value represents the progress toward the next level.
	 * @return The current {@code int} value of experience.
	 */
	public int getExp()
	{
		return exp;
	}
	
	/**
	 * Updates the experience points for this collection.<br>
	 * This method sets the {@code exp} field to a new value.
	 * @param exp The new amount of experience to set.
	 */
	public void setExp(int exp)
	{
		this.exp = exp;
	}
	
	/**
	 * Applies the collection bonuses to a specific player.<br>
	 * This method adds all relevant {@link StatFunction} modifiers from the template to the {@code Player}.
	 * @param player The {@code Player} who will receive the stat effects.
	 */
	public void apply(Player player)
	{
		if ((type != CollectionType.EVENT) && (level != 1))
		{
			final CollectionExpTemplate template = DataManager.COLLECTION_EXP_DATA.getTemplate(level, type);
			if (template.getModifiers() != null)
			{
				for (StatFunction modifiers : template.getModifiers().getModifiers())
				{
					functions.add(new StatAddFunction(modifiers.getName(), modifiers.getValue(), modifiers.isBonus()));
					player.getGameStats().addEffect(this, functions);
				}
			}
		}
	}
	
	/**
	 * Removes the buff from the specified {@link Player}.<br>
	 * Clears all associated functions and sets the bonus to {@code false}.<br>
	 * Notifies the game stats to end the effect.
	 * @param player The {@code Player} instance to update.
	 */
	public void end(Player player)
	{
		functions.clear();
		player.getGameStats().endEffect(this);
	}
	
	/**
	 * Updates the collection level when a player gains enough experience.<br>
	 * This method increments the {@code level} variable.<br>
	 * It also resets the {@code exp} value to {@code 0}.
	 */
	public void onLevelUp()
	{
		++level;
		exp = 0;
	}
	
	/**
	 * Increases the current experience points of the collection.<br>
	 * This method increments the {@code exp} variable by 1.
	 */
	public void addexp()
	{
		++exp;
	}
}
