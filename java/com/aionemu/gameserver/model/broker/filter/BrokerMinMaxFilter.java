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
package com.aionemu.gameserver.model.broker.filter;

import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * This class filters items based on a minimum and maximum price range.<br>
 * It ensures that only items within the specified bounds are displayed to the user.
 * @author ATracer
 */
public class BrokerMinMaxFilter extends BrokerFilter
{
	private final int min;
	private final int max;
	
	/**
	 * Creates a new filter based on a range of values.<br>
	 * This constructor scales the input values by {@code 100000}.<br>
	 * It is used to determine if an item meets specific limits.
	 * @param min The minimum value for the filter.
	 * @param max The maximum value for the filter.
	 */
	public BrokerMinMaxFilter(int min, int max)
	{
		this.min = min * 100000;
		this.max = max * 100000;
	}
	
	/**
	 * Checks if an item fits within a specific ID range.<br>
	 * It compares the {@code ItemTemplate} ID against the minimum and maximum limits.
	 * @param template The {@link ItemTemplate} to check.
	 * @return {@code true} if the ID is within the range, otherwise {@code false}.
	 */
	@Override
	public boolean accept(ItemTemplate template)
	{
		return (template.getTemplateId() >= min) && (template.getTemplateId() < max);
	}
}
