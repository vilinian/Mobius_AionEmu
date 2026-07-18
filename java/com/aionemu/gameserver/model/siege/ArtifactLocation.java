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
package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.templates.siegelocation.ArtifactActivation;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * Represents a specific location where an artifact can be found or activated during a siege.<br>
 * This class extends {@link SiegeLocation} to provide data related to artifact-specific mechanics.
 * @author Source
 */
public class ArtifactLocation extends SiegeLocation
{
	private ArtifactStatus status;
	
	/**
	 * Creates a new instance of {@code ArtifactLocation}.<br>
	 * This constructor initializes the {@code status} to {@code ArtifactStatus.IDLE}.
	 */
	public ArtifactLocation()
	{
		status = ArtifactStatus.IDLE;
	}
	
	/**
	 * Creates a new {@link ArtifactLocation} instance using a template.<br>
	 * This constructor initializes the location with data from the provided {@code SiegeLocationTemplate}.<br>
	 * It also sets the vulnerability status to {@code true} by default.
	 * @param template The {@code SiegeLocationTemplate} used to build this location.
	 */
	public ArtifactLocation(SiegeLocationTemplate template)
	{
		super(template);
		
		// Artifacts Always Vulnerable
		setVulnerable(true);
	}
	
	/**
	 * Retrieves the next state for this artifact.<br>
	 * This method returns the {@code STATE_VULNERABLE} value.
	 * @return The integer representing the next state.
	 */
	@Override
	public int getNextState()
	{
		return STATE_VULNERABLE;
	}
	
	/**
	 * Retrieves the timestamp of the most recent activation.<br>
	 * This value is stored as a {@code long}.
	 * @return The time of the last activation.
	 */
	public long getLastActivation()
	{
		return lastArtifactActivation;
	}
	
	/**
	 * Updates the timestamp of the last activation.<br>
	 * This method sets the {@code lastArtifactActivation} field.
	 * @param paramLong The new timestamp to set.
	 */
	public void setLastActivation(long paramLong)
	{
		lastArtifactActivation = paramLong;
	}
	
	/**
	 * Calculates the remaining cooldown time for this artifact.<br>
	 * It compares the last activation time against the template's cooldown value.<br>
	 * The result is returned in seconds.
	 * @return The number of seconds remaining until the artifact can be activated again.
	 */
	public int getCoolDown()
	{
		final long i = template.getActivation().getCd();
		final long l = System.currentTimeMillis() - lastArtifactActivation;
		if (l > i)
		{
			return 0;
		}
		
		return (int) ((i - l) / 1000);
	}
	
	/**
	 * Retrieves the {@link DescriptionId} for this artifact's name.<br>
	 * This ID is based on the skill template associated with the activation.
	 * @return The {@code DescriptionId} of the artifact name.
	 */
	public DescriptionId getNameAsDescriptionId()
	{
		// Get Skill id, item, count and target defined for each artifact.
		final ArtifactActivation activation = getTemplate().getActivation();
		final int skillId = activation.getSkillId();
		final SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		return new DescriptionId(skillTemplate.getNameId());
	}
	
	/**
	 * Checks if this location exists independently.<br>
	 * It returns {@code true} if the location is not part of any fortress.<br>
	 * This method uses {@link SiegeService} to verify the status.
	 * @return {@code true} if it is a stand-alone location, {@code false} otherwise.
	 */
	public boolean isStandAlone()
	{
		return !SiegeService.getInstance().getFortresses().containsKey(getLocationId());
	}
	
	/**
	 * Retrieves the {@link FortressLocation} that owns this artifact.<br>
	 * It uses the current location ID to find the fortress via {@link SiegeService}.
	 * @return the {@code FortressLocation} associated with this object.
	 */
	public FortressLocation getOwningFortress()
	{
		return SiegeService.getInstance().getFortress(getLocationId());
	}
	
	/**
	 * Retrieves the current state of the artifact.<br>
	 * If the {@code status} is {@code null}, it returns {@code IDLE}.
	 * @return The current {@code ArtifactStatus} of this location.
	 */
	public ArtifactStatus getStatus()
	{
		return status != null ? status : ArtifactStatus.IDLE;
	}
	
	/**
	 * Updates the current status of this {@link ArtifactLocation}.<br>
	 * This method sets the internal {@code status} field.
	 * @param status The new {@code ArtifactStatus} to assign.
	 */
	public void setStatus(ArtifactStatus status)
	{
		this.status = status;
	}
}
