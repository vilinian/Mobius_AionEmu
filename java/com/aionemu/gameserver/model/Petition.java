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
package com.aionemu.gameserver.model;

/**
 * Represents a petition object within the game world.<br>
 * This class stores data related to player-driven requests or community goals.<br>
 * It is used by {@code Player} interactions to track progress.
 * @author zdead
 */
public class Petition
{
	private final int petitionId;
	private final int playerObjId;
	private final PetitionType type;
	private final String title;
	private final String contentText;
	private final String additionalData;
	private final PetitionStatus status;
	
	/**
	 * Creates a new {@link Petition} instance with default values.<br>
	 * The {@code petitionId} is set, while other fields are initialized to defaults.
	 * @param petitionId The unique identifier for the new petition.
	 */
	public Petition(int petitionId)
	{
		this.petitionId = petitionId;
		playerObjId = 0;
		type = PetitionType.INQUIRY;
		title = "";
		contentText = "";
		additionalData = "";
		status = PetitionStatus.PENDING;
	}
	
	/**
	 * Creates a new {@link Petition} object with all required details.<br>
	 * This constructor maps integer IDs to specific {@code PetitionType} and {@code PetitionStatus} enums.
	 * @param petitionId The unique identifier for the petition.
	 * @param playerObjId The unique identifier of the player who created the petition.
	 * @param petitionTypeId The numeric ID used to determine the type of petition.
	 * @param title The short summary or name of the petition.
	 * @param contentText The main body text describing the issue.
	 * @param additionalData Any extra information provided by the user.
	 * @param petitionStatus The numeric status code for the current state of the petition.
	 */
	public Petition(int petitionId, int playerObjId, int petitionTypeId, String title, String contentText, String additionalData, int petitionStatus)
	{
		this.petitionId = petitionId;
		this.playerObjId = playerObjId;
		switch (petitionTypeId)
		{
			case 256:
				type = PetitionType.CHARACTER_STUCK;
				break;
			case 512:
				type = PetitionType.CHARACTER_RESTORATION;
				break;
			case 768:
				type = PetitionType.BUG;
				break;
			case 1024:
				type = PetitionType.QUEST;
				break;
			case 1280:
				type = PetitionType.UNACCEPTABLE_BEHAVIOR;
				break;
			case 1536:
				type = PetitionType.SUGGESTION;
				break;
			case 65280:
				type = PetitionType.INQUIRY;
				break;
			default:
				type = PetitionType.INQUIRY;
				break;
		}
		
		this.title = title;
		this.contentText = contentText;
		this.additionalData = additionalData;
		switch (petitionStatus)
		{
			case 0:
				status = PetitionStatus.PENDING;
				break;
			case 1:
				status = PetitionStatus.IN_PROGRESS;
				break;
			case 2:
				status = PetitionStatus.REPLIED;
				break;
			default:
				status = PetitionStatus.PENDING;
				break;
		}
	}
	
	/**
	 * Retrieves the unique object identifier for the player.<br>
	 * This value is used to identify which player created the {@link Petition}.
	 * @return The {@code int} value of the player's object ID.
	 */
	public int getPlayerObjId()
	{
		return playerObjId;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Petition}.<br>
	 * This ID is assigned when the object is created.
	 * @return The {@code int} value of the petition ID.
	 */
	public int getPetitionId()
	{
		return petitionId;
	}
	
	/**
	 * Retrieves the category of this petition.<br>
	 * This method returns the {@code PetitionType} associated with the current object.
	 * @return The {@link PetitionType} of the petition.
	 */
	public PetitionType getPetitionType()
	{
		return type;
	}
	
	/**
	 * Retrieves the title of the {@link Petition}.<br>
	 * This returns the name given to the petition.
	 * @return The {@code String} representing the title.
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Retrieves the main text body of the {@link Petition}.<br>
	 * This method returns the description provided by the user.
	 * @return The {@code String} containing the petition content.
	 */
	public String getContentText()
	{
		return contentText;
	}
	
	/**
	 * Retrieves the extra information associated with this {@link Petition}.<br>
	 * This data is stored as a {@code String}.
	 * @return The additional data string or {@code null} if no data exists.
	 */
	public String getAdditionalData()
	{
		return additionalData;
	}
	
	/**
	 * Retrieves the current status of this {@link Petition}.<br>
	 * This method returns the {@code PetitionStatus} value.
	 * @return The current {@code PetitionStatus}.
	 */
	public PetitionStatus getStatus()
	{
		return status;
	}
}
