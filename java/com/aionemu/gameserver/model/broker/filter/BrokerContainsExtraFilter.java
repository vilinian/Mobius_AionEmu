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

import java.util.Arrays;

import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * This filter checks if an item contains specific extra properties.<br>
 * It is used to refine search results in the broker system based on additional criteria.
 * @author ATracer
 */
public class BrokerContainsExtraFilter extends BrokerFilter
{
	private final int[] masks;
	
	/**
	 * Creates a new filter based on specific bitwise masks.<br>
	 * This allows for custom filtering of items in the broker.
	 * @param masks The array of {@code int} values used to define the filters.
	 */
	public BrokerContainsExtraFilter(int... masks)
	{
		this.masks = masks;
	}
	
	/**
	 * Checks if the item matches any of the allowed masks.<br>
	 * It compares the {@link ItemTemplate} ID against the internal mask list.
	 * @param template The {@code ItemTemplate} to check.
	 * @return {@code true} if a match is found, otherwise {@code false}.
	 */
	@Override
	public boolean accept(ItemTemplate template)
	{
		final int value = template.getTemplateId() / 10000;
		return Arrays.stream(masks).anyMatch(e -> e == value);
	}
}
