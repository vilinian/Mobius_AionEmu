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
package com.aionemu.gameserver.utils.collections;

/**
 * This interface defines a standard contract for cache structures.<br>
 * It provides common operations for storing and retrieving data efficiently.<br>
 * Use this to manage temporary data that requires fast access throughout the server.
 * @author Rolandas
 * @param <K>
 * @param <V>
 */
@SuppressWarnings(
{
	"rawtypes"
})
public interface ICache<K extends Comparable, V>
{
	V get(K obj);
	
	void put(K key, V obj);
	
	void remove(K key);
	
	CachePair[] getAll();
	
	int size();
}
