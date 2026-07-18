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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SourceLocation;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;

/**
 * This class serves as a data holder for all siege-related locations.<br>
 * It manages collections of {@link SourceLocation}, {@link FortressLocation}, and {@link ArtifactLocation} objects.<br>
 * It is used to load and store configuration data from XML files.
 * @author Sarynth, antness
 */
@XmlRootElement(name = "siege_locations")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiegeLocationData
{
	@XmlElement(name = "siege_location")
	private List<SiegeLocationTemplate> siegeLocationTemplates;
	/**
	 * Map that contains skillId - SkillTemplate key-value pair
	 */
	@XmlTransient
	private final Map<Integer, ArtifactLocation> artifactLocations = new HashMap<>();
	@XmlTransient
	private final Map<Integer, FortressLocation> fortressLocations = new HashMap<>();
	@XmlTransient
	private final Map<Integer, SourceLocation> sourceLocations = new HashMap<>();
	@XmlTransient
	private final Map<Integer, SiegeLocation> siegeLocations = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the location maps using the list of {@link SiegeLocationTemplate} templates.<br>
	 * The internal maps are cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		artifactLocations.clear();
		fortressLocations.clear();
		sourceLocations.clear();
		siegeLocations.clear();
		for (SiegeLocationTemplate template : siegeLocationTemplates)
		{
			switch (template.getType())
			{
				case FORTRESS:
					final FortressLocation fortress = new FortressLocation(template);
					fortressLocations.put(template.getId(), fortress);
					siegeLocations.put(template.getId(), fortress);
					artifactLocations.put(template.getId(), new ArtifactLocation(template));
					break;
				case ARTIFACT:
					final ArtifactLocation artifact = new ArtifactLocation(template);
					artifactLocations.put(template.getId(), artifact);
					siegeLocations.put(template.getId(), artifact);
					break;
				case SOURCE:
					final SourceLocation source = new SourceLocation(template);
					sourceLocations.put(template.getId(), source);
					siegeLocations.put(template.getId(), source);
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Returns the total number of siege locations.<br>
	 * This method retrieves the count from the internal {@code siegeLocations} map.
	 * @return The number of siege locations currently stored.
	 */
	public int size()
	{
		return siegeLocations.size();
	}
	
	/**
	 * Retrieves the map of all artifact locations.<br>
	 * The keys in this map are {@code Integer} IDs.<br>
	 * Each value is an {@link ArtifactLocation} object.
	 * @return A {@code FastMap} containing the artifact locations.
	 */
	public Map<Integer, ArtifactLocation> getArtifacts()
	{
		return artifactLocations;
	}
	
	/**
	 * Retrieves the map of all fortress locations.<br>
	 * The keys in this map are unique identifiers for each fortress.
	 * @return a {@link Map} containing {@link FortressLocation} objects.
	 */
	public Map<Integer, FortressLocation> getFortress()
	{
		return fortressLocations;
	}
	
	/**
	 * Retrieves the map of source locations.<br>
	 * The keys are {@code Integer} IDs and the values are {@link SourceLocation} objects.
	 * @return a {@link Map} containing all source locations.
	 */
	public Map<Integer, SourceLocation> getSource()
	{
		return sourceLocations;
	}
	
	/**
	 * Retrieves all registered siege locations.<br>
	 * The data is organized by an {@code Integer} ID key.
	 * @return A {@link Map} containing the mapping of IDs to {@link SiegeLocation} objects.
	 */
	public Map<Integer, SiegeLocation> getSiegeLocations()
	{
		return siegeLocations;
	}
}
