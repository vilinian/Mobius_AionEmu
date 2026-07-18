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

import com.aionemu.gameserver.model.templates.panels.SkillPanel;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for polymorph skills associated with various panels.<br>
 * It serves as a data container that maps {@link SkillPanel} objects to their respective skill sets.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "polymorph_panels")
public class PanelSkillsData
{
	@XmlElement(name = "panel")
	protected List<SkillPanel> templates;
	private final TIntObjectHashMap<SkillPanel> skillPanels = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data is loaded.<br>
	 * It copies elements from the {@code templates} list into the internal {@code skillPanels} map.<br>
	 * The {@code templates} list is then cleared and set to {@code null}.
	 * @param unmarshaller The {@link Unmarshaller} used to read the data.
	 * @param parent The object that contains this data.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		for (SkillPanel panel : templates)
		{
			skillPanels.put(panel.getPanelId(), panel);
		}
		
		templates.clear();
		templates = null;
	}
	
	/**
	 * Retrieves a specific {@link SkillPanel} based on its unique identifier.<br>
	 * This method looks up the panel in the internal data map.
	 * @param id The unique integer ID of the skill panel to find.
	 * @return The {@code SkillPanel} associated with the given {@code id}, or {@code null} if not found.
	 */
	public SkillPanel getSkillPanel(int id)
	{
		return skillPanels.get(id);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return skillPanels.size();
	}
}
