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
package com.aionemu.gameserver.model.account;

/**
 * This class manages a collection of {@code Transform} objects associated with an account.<br>
 * It provides helper methods to handle and organize character transformations for the user.
 */
public class TransformCollection
{
	private final int id;
	// private final TransformCollectionTemplate template;
	private final TransformCollectionBuff cb;
	
	/**
	 * Creates a new instance of {@link TransformCollection}.<br>
	 * It initializes the collection with a specific unique identifier.<br>
	 * This constructor also sets up a new {@code TransformCollectionBuff} object.
	 * @param id The unique integer ID for this collection.
	 */
	public TransformCollection(int id)
	{
		this.id = id;
		
		// template = DataManager.TRANSFORM_COLLECTION_DATA.getTransformCollectionById(id);
		cb = new TransformCollectionBuff();
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
	 * Retrieves the {@code TransformCollectionBuff} associated with this collection.<br>
	 * This method provides access to the buff data.
	 * @return the {@code TransformCollectionBuff} object.
	 */
	public TransformCollectionBuff getCb()
	{
		return cb;
	}
}
