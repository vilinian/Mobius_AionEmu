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
package com.aionemu.gameserver.model.templates.materials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * This class manages a collection of mesh data for materials.<br>
 * It serves as a container to store and organize multiple {@code String} identifiers or objects related to 3D meshes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeshList", propOrder =
{
	"meshMaterials"
})
public class MeshList
{
	@XmlElement(name = "mesh", required = true)
	protected List<MeshMaterial> meshMaterials;
	@XmlAttribute(name = "world_id", required = true)
	protected int worldId;
	@XmlTransient
	Map<String, Integer> materialIdsByPath = new HashMap<>();
	@XmlTransient
	Map<Integer, String> pathZones = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code materialIdsByPath} and {@code pathZones} maps from the list of {@link MeshMaterial} objects.<br>
	 * The {@code meshMaterials} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (meshMaterials == null)
		{
			return;
		}
		
		for (MeshMaterial meshMaterial : meshMaterials)
		{
			materialIdsByPath.put(meshMaterial.path, meshMaterial.materialId);
			pathZones.put(meshMaterial.path.hashCode(), meshMaterial.getZoneName());
			meshMaterial.path = null;
		}
		
		meshMaterials.clear();
		meshMaterials = null;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Retrieves the unique identifier for a specific mesh material.<br>
	 * This method looks up the ID based on the provided file path.<br>
	 * It returns {@code 0} if no matching path is found.
	 * @param meshPath The file path of the mesh to look up.
	 * @return The integer ID of the material, or {@code 0} if not found.
	 */
	public int getMeshMaterialId(String meshPath)
	{
		final Integer materialId = materialIdsByPath.get(meshPath);
		if (materialId == null)
		{
			return 0;
		}
		
		return materialId;
	}
	
	/**
	 * Retrieves all unique paths for the meshes in this list.<br>
	 * This method returns the keys from the internal {@code materialIdsByPath} map.
	 * @return A {@code Set<String>} containing all mesh paths.
	 */
	public Set<String> getMeshPaths()
	{
		return materialIdsByPath.keySet();
	}
	
	/**
	 * Retrieves the name of the zone associated with a specific mesh path.<br>
	 * This method looks up the path in the internal {@code pathZones} map.
	 * @param meshPath The file path of the mesh to look up.
	 * @return The name of the zone as a {@code String}, or {@code null} if not found.
	 */
	public String getZoneName(String meshPath)
	{
		return pathZones.get(meshPath.hashCode());
	}
	
	/**
	 * Returns the total number of materials in this list.<br>
	 * This count corresponds to the entries in the {@code materialIdsByPath} map.
	 * @return The number of elements currently stored.
	 */
	public int size()
	{
		return materialIdsByPath.size();
	}
}
