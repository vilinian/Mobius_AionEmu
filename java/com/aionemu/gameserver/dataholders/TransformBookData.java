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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link TransformBookTemplate} objects.<br>
 * It manages the collection of all transform book templates loaded from the configuration files.
 */
@XmlRootElement(name = "transform_book_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransformBookData
{
	@XmlElement(name = "transform_book_template")
	private List<TransformBookTemplate> tlist;
	@SuppressWarnings("unused")
	private List<TransformBookTemplate> normal;
	@SuppressWarnings("unused")
	private List<TransformBookTemplate> greater;
	@SuppressWarnings("unused")
	private List<TransformBookTemplate> ancient;
	@SuppressWarnings("unused")
	private List<TransformBookTemplate> legendary;
	@SuppressWarnings("unused")
	private List<TransformBookTemplate> ultime;
	@XmlTransient
	private final List<TransformBookTemplate> rndTransform = new ArrayList<>();
	@XmlTransient
	private final Map<Integer, Integer> transformSkill = new HashMap<>();
	@XmlTransient
	private final TIntObjectHashMap<TransformBookTemplate> transformBookData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code transformBookData}, {@code rndTransform}, and {@code transformSkill} collections.<br>
	 * The data is processed from the {@code tlist} collection.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (TransformBookTemplate book : tlist)
		{
			transformBookData.put(book.getId(), book);
			rndTransform.add(book);
			transformSkill.put(book.getSkillId(), book.getId());
		}
	}
	
	/**
	 * Returns the total number of transform books.<br>
	 * This method calls {@code size} to get the count.
	 * @return The size of the internal data map.
	 */
	public int size()
	{
		return transformBookData.size();
	}
	
	/**
	 * Retrieves all the transformation book templates.<br>
	 * This method returns the internal map of data.
	 * @return a {@code TIntObjectHashMap} containing all {@link TransformBookTemplate} objects.
	 */
	public TIntObjectHashMap<TransformBookTemplate> getAllBooks()
	{
		return transformBookData;
	}
	
	/**
	 * Retrieves a specific {@link TransformBookTemplate} using its unique identifier.<br>
	 * This method looks up the data in the internal map.
	 * @param id The unique integer ID of the transform book to find.
	 * @return The {@code TransformBookTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public TransformBookTemplate getTransformBookById(int id)
	{
		return transformBookData.get(id);
	}
	
	/**
	 * Retrieves all random transform templates.<br>
	 * This method returns the internal list of {@code TransformBookTemplate} objects.
	 * @return a {@code List} containing all random transforms.
	 */
	public List<TransformBookTemplate> getAllTransform()
	{
		return rndTransform;
	}
	
	/**
	 * Retrieves all transform books with an ancient grade.<br>
	 * This method filters the {@code transformBookData} collection.<br>
	 * It returns a list of templates where the grade is equal to {@code 1}.
	 * @return A {@code List} of {@link TransformBookTemplate} objects.
	 */
	public List<TransformBookTemplate> getAncient()
	{
		final List<TransformBookTemplate> list = new ArrayList<>();
		for (TransformBookTemplate tp : transformBookData.valueCollection())
		{
			if (tp.getGrade() == 1)
			{
				list.add(tp);
			}
		}
		
		return list;
	}
	
	/**
	 * Retrieves all transform books with a grade of {@code 2}.<br>
	 * This method filters the internal data to find specific templates.
	 * @return A {@code List} containing all {@link TransformBookTemplate} objects that match the criteria.
	 */
	public List<TransformBookTemplate> getGreater()
	{
		final List<TransformBookTemplate> list = new ArrayList<>();
		for (TransformBookTemplate tp : transformBookData.valueCollection())
		{
			if (tp.getGrade() == 2)
			{
				list.add(tp);
			}
		}
		
		return list;
	}
	
	/**
	 * Retrieves all legendary transform books.<br>
	 * This method filters the {@code transformBookData} for items with a grade of {@code 3}.
	 * @return A {@code List} containing all {@link TransformBookTemplate} objects that are legendary.
	 */
	public List<TransformBookTemplate> getLegendary()
	{
		final List<TransformBookTemplate> list = new ArrayList<>();
		for (TransformBookTemplate tp : transformBookData.valueCollection())
		{
			if (tp.getGrade() == 3)
			{
				list.add(tp);
			}
		}
		
		return list;
	}
	
	/**
	 * Retrieves a list of normal transform book templates.<br>
	 * This method filters the data to find items with a grade of {@code 4}.
	 * @return A {@code List} containing all {@link TransformBookTemplate} objects of normal grade.
	 */
	public List<TransformBookTemplate> getNormal()
	{
		final List<TransformBookTemplate> list = new ArrayList<>();
		for (TransformBookTemplate tp : transformBookData.valueCollection())
		{
			if (tp.getGrade() == 4)
			{
				list.add(tp);
			}
		}
		
		return list;
	}
	
	/**
	 * Retrieves a list of all ultime transform books.<br>
	 * This method filters the {@code transformBookData} for items with a grade of {@code 5}.
	 * @return A {@code List} containing all {@link TransformBookTemplate} objects of ultime grade.
	 */
	public List<TransformBookTemplate> getUltime()
	{
		final List<TransformBookTemplate> list = new ArrayList<>();
		for (TransformBookTemplate tp : transformBookData.valueCollection())
		{
			if (tp.getGrade() == 5)
			{
				list.add(tp);
			}
		}
		
		return list;
	}
	
	/**
	 * Retrieves the unique identifier for a transformation based on a skill ID.<br>
	 * This method checks if the {@code skillId} exists in the internal mapping.<br>
	 * If the skill is not found, it returns {@code 0}.
	 * @param skillId The unique identifier of the skill to look up.
	 * @return The corresponding transformation ID or {@code 0} if no match exists.
	 */
	public Integer getTransformId(int skillId)
	{
		int transformId = 0;
		if (transformSkill.containsKey(skillId))
		{
			transformId = transformSkill.get(skillId);
		}
		
		return transformId;
	}
}
