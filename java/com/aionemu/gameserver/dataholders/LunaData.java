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
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.recipe.LunaTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link LunaTemplate} objects.<br>
 * It manages the collection of templates loaded from XML configuration files.<br>
 * Use this class to access and retrieve specific luna data throughout the server.
 */
@XmlRootElement(name = "luna_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class LunaData
{
	@XmlElement(name = "luna_template")
	protected List<LunaTemplate> list;
	
	private TIntObjectHashMap<LunaTemplate> lunaData;
	
	@XmlTransient
	private List<LunaTemplate> elyos, asmos, any;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code lunaData}, {@code elyos}, {@code asmos}, and {@code any} collections from the {@code list}.<br>
	 * The {@code list} is set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		lunaData = new TIntObjectHashMap<>();
		elyos = new ArrayList<>();
		asmos = new ArrayList<>();
		any = new ArrayList<>();
		for (LunaTemplate lt : list)
		{
			lunaData.put(lt.getId(), lt);
			switch (lt.getRace())
			{
				case ASMODIANS:
					asmos.add(lt);
					break;
				case ELYOS:
					elyos.add(lt);
					break;
				case PC_ALL:
					any.add(lt);
					break;
				default:
					break;
			}
		}
		
		list = null;
	}
	
	/**
	 * Retrieves all available {@link LunaTemplate} objects.<br>
	 * This method returns the complete list of templates from the {@code any} collection.
	 * @return a {@code List} containing all {@code LunaTemplate} entries.
	 */
	public List<LunaTemplate> getLunaTemplatesAny()
	{
		return any;
	}
	
	/**
	 * Retrieves a specific {@link LunaTemplate} from the data map.<br>
	 * It uses the provided unique identifier to find the template.
	 * @param id The unique integer ID of the template to retrieve.
	 * @return The {@code LunaTemplate} object associated with the given ID, or {@code null} if not found.
	 */
	public LunaTemplate getLunaTemplateById(int id)
	{
		return lunaData.get(id);
	}
	
	/**
	 * Retrieves the collection of all {@link LunaTemplate} objects.<br>
	 * The data is stored in a {@code TIntObjectHashMap}.
	 * @return A map containing all loaded luna templates.
	 */
	public TIntObjectHashMap<LunaTemplate> getLunaTemplates()
	{
		return lunaData;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return lunaData.size();
	}
}
