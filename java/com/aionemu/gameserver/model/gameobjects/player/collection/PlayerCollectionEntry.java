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
import com.aionemu.gameserver.model.templates.collection.CollectionTemplate;

/**
 * Represents an individual item or entry within a {@link Player} collection.<br>
 * This class manages the specific data and statistics associated with a single collection instance.
 */
public class PlayerCollectionEntry implements StatOwner
{
	private final int id;
	private boolean item1;
	private boolean item2;
	private boolean item3;
	private boolean item4;
	private boolean item5;
	private boolean item6;
	private boolean item7;
	private boolean item8;
	private boolean item9;
	private boolean item10;
	private boolean item11;
	private boolean item12;
	private boolean item13;
	private boolean item14;
	private int step;
	private boolean complete = false;
	private final CollectionTemplate ct;
	private final List<IStatFunction> functions = new ArrayList<>();
	
	/**
	 * Creates a new instance of {@code PlayerCollectionEntry}.<br>
	 * This constructor initializes the entry with specific item statuses and progress.<br>
	 * It also loads the corresponding template from the data manager.
	 * @param id The unique identifier for the collection template.
	 * @param item1 The status of the first required item.
	 * @param item2 The status of the second required item.
	 * @param item3 The status of the third required item.
	 * @param item4 The status of the fourth required item.
	 * @param item5 The status of the fifth required item.
	 * @param item6 The status of the sixth required item.
	 * @param item7 The status of the seventh required item.
	 * @param item8 The status of the eighth required item.
	 * @param item9 The status of the ninth required item.
	 * @param item10 The status of the tenth required item.
	 * @param item11 The status of the eleventh required item.
	 * @param item12 The status of the twelfth required item.
	 * @param item13 The status of the thirteenth required item.
	 * @param item14 The status of the
	 * @param step
	 */
	public PlayerCollectionEntry(int id, boolean item1, boolean item2, boolean item3, boolean item4, boolean item5, boolean item6, boolean item7, boolean item8, boolean item9, boolean item10, boolean item11, boolean item12, boolean item13, boolean item14, int step)
	{
		this.id = id;
		this.item1 = item1;
		this.item2 = item2;
		this.item3 = item3;
		this.item4 = item4;
		this.item5 = item5;
		this.item6 = item6;
		this.item7 = item7;
		this.item8 = item8;
		this.item9 = item9;
		this.item10 = item10;
		this.item11 = item11;
		this.item12 = item12;
		this.item13 = item13;
		this.item14 = item14;
		this.step = step;
		ct = DataManager.COLLECTION_TEMPLATE_DATA.getTemplate(this.id);
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
	 * Checks if the first item in the collection is active.<br>
	 * This method returns the current state of {@code item1}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem1()
	{
		return item1;
	}
	
	/**
	 * Updates the status of {@code item1}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item1 The new boolean value for {@code item1}.
	 */
	public void setItem1(boolean item1)
	{
		this.item1 = item1;
	}
	
	/**
	 * Checks the status of the second item in the collection.<br>
	 * Returns {@code true} if the item is active or collected.<br>
	 * Returns {@code false} otherwise.
	 * @return The boolean state of {@code item2}.
	 */
	public boolean isItem2()
	{
		return item2;
	}
	
	/**
	 * Updates the status of {@code item2}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item2 The new boolean value for {@code item2}.
	 */
	public void setItem2(boolean item2)
	{
		this.item2 = item2;
	}
	
	/**
	 * Checks if the third item in the collection is active.<br>
	 * This method returns the current state of {@code item3}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem3()
	{
		return item3;
	}
	
	/**
	 * Updates the status of {@code item3}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item3 The new boolean value for {@code item3}.
	 */
	public void setItem3(boolean item3)
	{
		this.item3 = item3;
	}
	
	/**
	 * Checks if the fourth item in the collection is active.<br>
	 * This method returns the current state of {@code item4}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem4()
	{
		return item4;
	}
	
	/**
	 * Updates the status of {@code item4}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item4 The new boolean value for {@code item4}.
	 */
	public void setItem4(boolean item4)
	{
		this.item4 = item4;
	}
	
	/**
	 * Checks if the fifth item in the collection is active.<br>
	 * This method returns the current state of {@code item5}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem5()
	{
		return item5;
	}
	
	/**
	 * Updates the status of {@code item5}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item5 The new boolean value for {@code item5}.
	 */
	public void setItem5(boolean item5)
	{
		this.item5 = item5;
	}
	
	/**
	 * Checks if the sixth item in the collection is active.<br>
	 * This method returns the current state of {@code item6}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem6()
	{
		return item6;
	}
	
	/**
	 * Updates the status of the sixth item in the collection.<br>
	 * This method sets the {@code item6} field to a new value.
	 * @param item6 The new boolean value to assign to the sixth item.
	 */
	public void setItem6(boolean item6)
	{
		this.item6 = item6;
	}
	
	/**
	 * Checks if the seventh item in the collection is active.<br>
	 * This method returns the current state of {@code item7}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem7()
	{
		return item7;
	}
	
	/**
	 * Updates the status of {@code item7}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item7 The new boolean value for {@code item7}.
	 */
	public void setItem7(boolean item7)
	{
		this.item7 = item7;
	}
	
	/**
	 * Checks if the eighth item in the collection is active.<br>
	 * This method returns the current state of {@code item8}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem8()
	{
		return item8;
	}
	
	/**
	 * Updates the status of {@code item8}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item8 The new boolean value for {@code item8}.
	 */
	public void setItem8(boolean item8)
	{
		this.item8 = item8;
	}
	
	/**
	 * Checks if the ninth item in the collection is active.<br>
	 * This method returns the current state of {@code item9}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem9()
	{
		return item9;
	}
	
	/**
	 * Updates the status of {@code item9}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item9 The new boolean value for {@code item9}.
	 */
	public void setItem9(boolean item9)
	{
		this.item9 = item9;
	}
	
	/**
	 * Checks if the tenth item in the collection is active.<br>
	 * This method returns the current state of {@code item10}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem10()
	{
		return item10;
	}
	
	/**
	 * Updates the status of {@code item10}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item10 The new boolean value for {@code item10}.
	 */
	public void setItem10(boolean item10)
	{
		this.item10 = item10;
	}
	
	/**
	 * Checks if the eleventh item in the collection is active.<br>
	 * This method returns the current state of {@code item11}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem11()
	{
		return item11;
	}
	
	/**
	 * Updates the status of {@code item11}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item11 The new boolean value for {@code item11}.
	 */
	public void setItem11(boolean item11)
	{
		this.item11 = item11;
	}
	
	/**
	 * Checks if the twelfth item in the collection is active.<br>
	 * This method returns the current state of {@code item12}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem12()
	{
		return item12;
	}
	
	/**
	 * Updates the status of {@code item12}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item12 The new boolean value for {@code item12}.
	 */
	public void setItem12(boolean item12)
	{
		this.item12 = item12;
	}
	
	/**
	 * Checks if the thirteenth item in the collection is active.<br>
	 * This method returns the current state of {@code item13}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem13()
	{
		return item13;
	}
	
	/**
	 * Updates the status of {@code item13}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item13 The new boolean value for {@code item13}.
	 */
	public void setItem13(boolean item13)
	{
		this.item13 = item13;
	}
	
	/**
	 * Checks if the fourteenth item in the collection is active.<br>
	 * This method returns the current state of {@code item14}.
	 * @return {@code true} if the item is active, {@code false} otherwise.
	 */
	public boolean isItem14()
	{
		return item14;
	}
	
	/**
	 * Updates the status of {@code item14}.<br>
	 * This method sets the value to either {@code true} or {@code false}.
	 * @param item14 The new boolean value for {@code item14}.
	 */
	public void setItem14(boolean item14)
	{
		this.item14 = item14;
	}
	
	/**
	 * Retrieves the current step of the player. <br>
	 * This value represents the progress in the game sequence.
	 * @return The current {@code int} step value.
	 */
	public int getStep()
	{
		return step;
	}
	
	/**
	 * Updates the current step of the player.<br>
	 * This method sets the {@code step} value for this object.
	 * @param step The new step number to assign.
	 */
	public void setStep(int step)
	{
		this.step = step;
	}
	
	/**
	 * Updates a specific item in the collection based on the provided index.<br>
	 * This method sets the corresponding item to {@code true}.
	 * @param index The position of the item to update, ranging from {@code 0} to {@code 13}.
	 */
	public void update(int index)
	{
		switch (index)
		{
			case 0:
			{
				setItem1(true);
				break;
			}
			case 1:
			{
				setItem2(true);
				break;
			}
			case 2:
			{
				setItem3(true);
				break;
			}
			case 3:
			{
				setItem4(true);
				break;
			}
			case 4:
			{
				setItem5(true);
				break;
			}
			case 5:
			{
				setItem6(true);
				break;
			}
			case 6:
			{
				setItem7(true);
				break;
			}
			case 7:
			{
				setItem8(true);
				break;
			}
			case 8:
			{
				setItem9(true);
				break;
			}
			case 9:
			{
				setItem10(true);
				break;
			}
			case 10:
			{
				setItem11(true);
				break;
			}
			case 11:
			{
				setItem12(true);
				break;
			}
			case 12:
			{
				setItem13(true);
				break;
			}
			case 13:
			{
				setItem14(true);
			}
		}
	}
	
	/**
	 * Checks if the collection entry is finished.<br>
	 * This method returns the current status of the {@code complete} field.
	 * @return {@code true} if the collection is finished, {@code false} otherwise.
	 */
	public boolean isComplete()
	{
		return complete;
	}
	
	/**
	 * Updates the completion status of this collection entry.<br>
	 * Sets the {@code complete} field to the provided value.
	 * @param complete The new completion status to set.
	 */
	public void setComplete(boolean complete)
	{
		this.complete = complete;
	}
	
	/**
	 * Applies the collection bonuses to a specific player.<br>
	 * This method adds all relevant {@link StatFunction} modifiers from the template to the {@code Player}.
	 * @param player The {@code Player} who will receive the stat effects.
	 */
	public void apply(Player player)
	{
		if (ct.getModifiers() != null)
		{
			for (StatFunction modifiers : ct.getModifiers().getModifiers())
			{
				functions.add(new StatAddFunction(modifiers.getName(), modifiers.getValue(), modifiers.isBonus()));
				player.getGameStats().addEffect(this, functions);
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
}
