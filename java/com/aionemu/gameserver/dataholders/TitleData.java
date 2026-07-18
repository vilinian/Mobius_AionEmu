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

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.TitleTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for player titles within the game server.<br>
 * It serves as a container to manage and access {@link TitleTemplate} information.
 * @author xavier
 */
@XmlRootElement(name = "player_titles")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleData
{
	@XmlElement(name = "title")
	private List<TitleTemplate> tts;
	private TIntObjectHashMap<TitleTemplate> titles;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code titles} map using the list of {@link TitleTemplate} objects.<br>
	 * The {@code tts} list is set to {@code null} after the map is built.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		titles = new TIntObjectHashMap<>();
		for (TitleTemplate tt : tts)
		{
			titles.put(tt.getTitleId(), tt);
		}
		
		tts = null;
	}
	
	/**
	 * Retrieves a specific {@link TitleTemplate} based on its unique ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param titleId The unique integer identifier for the title.
	 * @return The {@code TitleTemplate} associated with the given {@code titleId}.
	 */
	public TitleTemplate getTitleTemplate(int titleId)
	{
		return titles.get(titleId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return titles.size();
	}
}
