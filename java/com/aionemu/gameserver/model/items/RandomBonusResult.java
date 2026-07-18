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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * This class represents the result of a random bonus calculation for an item.<br>
 * It stores the specific {@link ModifiersTemplate} applied during the generation process.
 * @author Rolandas
 */
public class RandomBonusResult
{
	private final ModifiersTemplate template;
	private final int templateNumber;
	
	/**
	 * Creates a new {@link RandomBonusResult} object.<br>
	 * This constructor initializes the result with a specific template and ID.
	 * @param template The {@code ModifiersTemplate} to use for this result.
	 * @param number The unique identifier for the template.
	 */
	public RandomBonusResult(ModifiersTemplate template, int number)
	{
		this.template = template;
		templateNumber = number;
	}
	
	/**
	 * Retrieves the {@code ModifiersTemplate} associated with this result.<br>
	 * This method returns the base template used for the random bonus.
	 * @return The {@link ModifiersTemplate} object.
	 */
	public ModifiersTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Retrieves the unique identifier for the associated template.<br>
	 * This value is assigned during the creation of a {@link RandomBonusResult}.
	 * @return The {@code int} value representing the template number.
	 */
	public int getTemplateNumber()
	{
		return templateNumber;
	}
}
