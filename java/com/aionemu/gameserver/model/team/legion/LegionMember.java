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
package com.aionemu.gameserver.model.team.legion;

/**
 * Represents an individual member within a {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * This class stores the data and properties associated with a specific player belonging to a legion.
 * @author Simple
 */
public class LegionMember
{
	private int objectId = 0;
	protected Legion legion = null;
	protected String nickname = "";
	protected String selfIntro = "";
	protected int challengeScore;
	protected LegionRank rank = LegionRank.VOLUNTEER;
	
	/**
	 * Creates a new {@link LegionMember} instance.<br>
	 * This constructor initializes the member with a specific ID.<br>
	 * Use this when the player is defined later in the process.
	 * @param objectId The unique identifier for the member.
	 */
	public LegionMember(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Creates a new {@link LegionMember} with specific details.<br>
	 * This constructor initializes the member's identity and position.
	 * @param objectId The unique identifier for the player.
	 * @param legion The {@link Legion} that this member belongs to.
	 * @param rank The {@link LegionRank} assigned to this member.
	 */
	public LegionMember(int objectId, Legion legion, LegionRank rank)
	{
		setObjectId(objectId);
		setLegion(legion);
		setRank(rank);
	}
	
	/**
	 * Creates a new instance of {@link LegionMember}.<br>
	 * This constructor initializes the member with default values.<br>
	 * Use this when the player details are defined later.
	 */
	public LegionMember()
	{
	}
	
	/**
	 * Sets the {@link Legion} associated with this member.<br>
	 * This updates the internal {@code legion} field.
	 * @param legion The {@code Legion} object to assign.
	 */
	public void setLegion(Legion legion)
	{
		this.legion = legion;
	}
	
	/**
	 * Retrieves the {@link Legion} associated with this member.<br>
	 * This method returns the internal {@code legion} field.
	 * @return the {@code Legion} object or {@code null} if no legion is assigned.
	 */
	public Legion getLegion()
	{
		return legion;
	}
	
	/**
	 * Updates the {@code LegionRank} of this member.<br>
	 * This method sets the internal rank field to the provided value.
	 * @param rank The new {@link LegionRank} to assign.
	 */
	public void setRank(LegionRank rank)
	{
		this.rank = rank;
	}
	
	/**
	 * Retrieves the current rank of the legion member.<br>
	 * This method returns a {@link LegionRank} object.
	 * @return The {@code LegionRank} assigned to this member.
	 */
	public LegionRank getRank()
	{
		return rank;
	}
	
	/**
	 * Checks if the member holds the {@code BRIGADE_GENERAL} rank.<br>
	 * This method compares the current {@link LegionRank} of the member.
	 * @return {@code true} if the member is a brigade general, otherwise {@code false}.
	 */
	public boolean isBrigadeGeneral()
	{
		return rank == LegionRank.BRIGADE_GENERAL;
	}
	
	/**
	 * Updates the nickname for this {@link LegionMember}.<br>
	 * This method sets the internal {@code nickname} field.
	 * @param nickname The new name to assign to the member.
	 */
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	
	/**
	 * Retrieves the nickname of the legion member.<br>
	 * This method returns the {@code String} value stored in the nickname field.
	 * @return The current nickname as a {@code String}.
	 */
	public String getNickname()
	{
		return nickname;
	}
	
	/**
	 * Updates the personal introduction for this legion member.<br>
	 * This method stores the provided {@code String} in the internal field.
	 * @param selfIntro The new introduction text to set.
	 */
	public void setSelfIntro(String selfIntro)
	{
		this.selfIntro = selfIntro;
	}
	
	/**
	 * Retrieves the personal introduction of the legion member.<br>
	 * This method returns the {@code String} stored in the {@code selfIntro} field.
	 * @return The member's self-introduction text.
	 */
	public String getSelfIntro()
	{
		return selfIntro;
	}
	
	/**
	 * Retrieves the current challenge score for this legion member.<br>
	 * This value represents the points earned during challenges.
	 * @return The current {@code int} score of the member.
	 */
	public int getChallengeScore()
	{
		return challengeScore;
	}
	
	/**
	 * Updates the current challenge score for this member.<br>
	 * This method sets the {@code challengeScore} field to a new value.
	 * @param challengeScore The new integer score to assign.
	 */
	public void setChallengeScore(int challengeScore)
	{
		this.challengeScore = challengeScore;
	}
	
	/**
	 * Increases the current challenge score of the member.<br>
	 * This method adds a specific value to the existing {@code challengeScore}.
	 * @param amount The number to add to the score.
	 */
	public void increaseChallengeScore(int amount)
	{
		challengeScore += amount;
	}
	
	/**
	 * Sets the unique identifier for this character passkey.<br>
	 * This value is used to identify the specific object in the system.
	 * @param objectId The {@code int} value to assign as the new ID.
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	/**
	 * Retrieves the unique identifier of this character passkey.<br>
	 * This value is used to identify the specific object in the system.
	 * @return The {@code int} value of the object ID.
	 */
	public int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * Checks if the member has specific permissions.<br>
	 * This method evaluates rights based on the current {@link LegionRank}.<br>
	 * It compares the rank's permission level against the provided mask.
	 * @param permissions The {@code LegionPermissionsMask} to check against.
	 * @return {@code true} if the member has the required rights, otherwise {@code false}.
	 */
	public boolean hasRights(LegionPermissionsMask permissions)
	{
		int legionarPermission = 0;
		switch (getRank())
		{
			case BRIGADE_GENERAL:
				return true;
			case DEPUTY:
				legionarPermission = legion.getDeputyPermission();
				break;
			case CENTURION:
				legionarPermission = legion.getCenturionPermission();
				break;
			case LEGIONARY:
				legionarPermission = legion.getLegionaryPermission();
				break;
			case VOLUNTEER:
				legionarPermission = legion.getVolunteerPermission();
				break;
		}
		
		return permissions.can(legionarPermission);
	}
}
