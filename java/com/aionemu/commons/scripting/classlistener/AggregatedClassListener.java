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
package com.aionemu.commons.scripting.classlistener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;

/**
 * This class acts as a container that aggregates multiple {@link ClassListener} instances.<br>
 * It allows you to group several listeners together and treat them as a single listener.<br>
 * Note that {@code shutdown} events will be executed in reverse order.
 * @author SoulKeeper
 */
public class AggregatedClassListener implements ClassListener
{
	private final List<ClassListener> classListeners;
	
	/**
	 * Creates a new instance of {@link AggregatedClassListener}.<br>
	 * This initializes an empty list to store the listeners.
	 */
	public AggregatedClassListener()
	{
		classListeners = new ArrayList<>();
	}
	
	/**
	 * Creates a new {@link AggregatedClassListener} using a provided list of listeners.<br>
	 * This allows multiple {@code ClassListener} objects to be grouped together.
	 * @param classListeners The list of {@code ClassListener} instances to aggregate.
	 */
	public AggregatedClassListener(List<ClassListener> classListeners)
	{
		this.classListeners = classListeners;
	}
	
	/**
	 * Retrieves the list of all registered {@link ClassListener} objects.<br>
	 * This method returns the internal collection used by this instance.
	 * @return a {@code List} containing all {@code ClassListener} instances.
	 */
	public List<ClassListener> getClassListeners()
	{
		return classListeners;
	}
	
	/**
	 * Adds a new listener to the collection.<br>
	 * This allows {@link AggregatedClassListener} to notify the provided listener of events.
	 * @param cl The {@code ClassListener} instance to add.
	 */
	public void addClassListener(ClassListener cl)
	{
		getClassListeners().add(cl);
	}
	
	/**
	 * This method notifies all registered listeners that the classes have been loaded.<br>
	 * It iterates through every {@link ClassListener} in the collection.<br>
	 * Each listener's {@code postLoad} method is called with the provided array.
	 * @param classes An array of {@code Class} objects to be processed.
	 */
	@Override
	public void postLoad(Class<?>[] classes)
	{
		for (ClassListener cl : getClassListeners())
		{
			cl.postLoad(classes);
		}
	}
	
	/**
	 * Prepares the system to unload specific classes.<br>
	 * This method removes valid {@code DAO} classes from the {@link DAOManager}.<br>
	 * It iterates through the provided array and unregisters each valid class.
	 * @param classes The array of {@code Class<?>} objects to be processed for unloading.
	 */
	@Override
	public void preUnload(Class<?>[] classes)
	{
		final List<ClassListener> reversed = new ArrayList<>(getClassListeners());
		Collections.reverse(reversed);
		for (ClassListener cl : reversed)
		{
			cl.preUnload(classes);
		}
	}
}
