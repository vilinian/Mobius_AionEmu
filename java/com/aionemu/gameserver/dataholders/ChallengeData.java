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
package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;
import com.aionemu.gameserver.model.templates.challenge.ChallengeTaskTemplate;

/**
 * This class serves as a data holder for challenge-related information.<br>
 * It stores and manages the properties of challenges within the game world.<br>
 * It is used to map configuration data into usable objects for the server.
 * @author ViAl
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"task"
})
@XmlRootElement(name = "challenge_tasks")
public class ChallengeData
{
	protected List<ChallengeTaskTemplate> task;
	@XmlTransient
	protected Map<Integer, ChallengeTaskTemplate> tasksById = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code tasksById} map using the list of {@link ChallengeTaskTemplate} objects.<br>
	 * The {@code task} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ChallengeTaskTemplate t : task)
		{
			tasksById.put(t.getId(), t);
		}
		
		task.clear();
		task = null;
	}
	
	/**
	 * Retrieves all challenge tasks from the data holder.<br>
	 * The tasks are organized by their unique IDs.
	 * @return a {@code Map} where the key is an {@code Integer} ID and the value is a {@link ChallengeTaskTemplate}.
	 */
	public Map<Integer, ChallengeTaskTemplate> getTasks()
	{
		return tasksById;
	}
	
	/**
	 * Retrieves a specific task based on its unique ID.<br>
	 * This method searches the internal map for the matching {@code ChallengeTaskTemplate}.
	 * @param taskId The unique identifier of the task to find.
	 * @return The {@code ChallengeTaskTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public ChallengeTaskTemplate getTaskByTaskId(int taskId)
	{
		return tasksById.get(taskId);
	}
	
	/**
	 * Retrieves a {@link ChallengeTaskTemplate} based on a specific quest ID.<br>
	 * This method searches through all tasks to find one associated with the given quest.
	 * @param questId The unique identifier of the quest to search for.
	 * @return The matching {@code ChallengeTaskTemplate} or {@code null} if no match is found.
	 */
	public ChallengeTaskTemplate getTaskByQuestId(int questId)
	{
		for (ChallengeTaskTemplate ct : tasksById.values())
		{
			for (ChallengeQuestTemplate cq : ct.getQuests())
			{
				if (cq.getId() == questId)
				{
					return ct;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves a {@link ChallengeQuestTemplate} based on its unique ID.<br>
	 * This method searches through all tasks to find the matching quest.
	 * @param questId The unique identifier of the quest to find.
	 * @return The matching {@code ChallengeQuestTemplate} or {@code null} if not found.
	 */
	public ChallengeQuestTemplate getQuestByQuestId(int questId)
	{
		for (ChallengeTaskTemplate ct : tasksById.values())
		{
			for (ChallengeQuestTemplate cq : ct.getQuests())
			{
				if (cq.getId() == questId)
				{
					return cq;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the total number of tasks.<br>
	 * This method calls {@code size} to get the count.
	 * @return The number of items currently stored in the collection.
	 */
	public int size()
	{
		return tasksById.size();
	}
}
