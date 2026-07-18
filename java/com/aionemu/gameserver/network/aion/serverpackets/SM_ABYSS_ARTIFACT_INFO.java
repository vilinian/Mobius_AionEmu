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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.ArrayList;
import java.util.Collection;

import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.SiegeService;

/**
 * This packet provides information regarding the location of Abyss Artifacts.<br>
 * It is used by the server to synchronize artifact data with the client.
 */
public class SM_ABYSS_ARTIFACT_INFO extends AionServerPacket
{
	private final Collection<ArtifactLocation> locations;
	private boolean teleportStatus;
	
	/**
	 * Creates a new {@link SM_ABYSS_ARTIFACT_INFO} packet.<br>
	 * This constructor initializes the packet with a set of artifact locations.
	 * @param collection A {@code Collection} of {@link ArtifactLocation} objects.
	 */
	public SM_ABYSS_ARTIFACT_INFO(Collection<ArtifactLocation> collection)
	{
		locations = collection;
	}
	
	/**
	 * Creates a packet containing information for a specific artifact.<br>
	 * This constructor retrieves the artifact using its unique ID.<br>
	 * It uses {@link SiegeService} to fetch the data from the server.
	 * @param loc The unique identifier of the artifact location.
	 */
	public SM_ABYSS_ARTIFACT_INFO(int loc)
	{
		locations = new ArrayList<>();
		locations.add(SiegeService.getInstance().getArtifact(loc));
	}
	
	/**
	 * Creates a new {@link SM_ABYSS_ARTIFACT_INFO} packet.<br>
	 * This constructor initializes the artifact data based on a specific ID.<br>
	 * It also sets the current teleport state.
	 * @param locationId The unique identifier for the artifact location.
	 * @param teleportStatus The status of the teleportation, either {@code true} or {@code false}.
	 */
	public SM_ABYSS_ARTIFACT_INFO(int locationId, boolean teleportStatus)
	{
		locations = new ArrayList<>();
		locations.add(SiegeService.getInstance().getArtifact(locationId));
		this.teleportStatus = teleportStatus;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(locations.size());
		for (ArtifactLocation artifact : locations)
		{
			writeD((artifact.getLocationId() * 10) + 1);
			writeC(artifact.getStatus().getValue());
			writeD(0);
			writeC(teleportStatus ? 1 : 0);
		}
	}
}
