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
package com.aionemu.commons.objects.filter;

/**
 * This filter combines multiple {@link ObjectFilter} instances into a single logical unit.<br>
 * The {@code acceptObject} method returns {@code true} only if every filter passed to the constructor returns {@code true}.
 * @author Luno
 * @param <T>
 */
public class AndObjectFilter<T> implements ObjectFilter<T>
{
	/** All filters that are used when running acceptObject() method */
	private final ObjectFilter<? super T>[] filters;
	
	/**
	 * Creates a new {@link AndObjectFilter} using the provided filters.<br>
	 * This filter will only pass an object if every filter in the list returns {@code true}.
	 * @param filters The array of {@link ObjectFilter} instances to combine.
	 */
	@SafeVarargs
	public AndObjectFilter(ObjectFilter<? super T>... filters)
	{
		this.filters = filters;
	}
	
	/**
	 * Checks if the given {@code object} passes all internal filters.<br>
	 * It returns {@code true} only if every filter accepts the object.<br>
	 * If any filter rejects the object, it returns {@code false}.
	 * @param object The item to be checked by the filters.
	 * @return {@code true} if all filters pass, otherwise {@code false}.
	 */
	@Override
	public boolean acceptObject(T object)
	{
		for (ObjectFilter<? super T> filter : filters)
		{
			if ((filter != null) && !filter.acceptObject(object))
			{
				return false;
			}
		}
		
		return true;
	}
}
