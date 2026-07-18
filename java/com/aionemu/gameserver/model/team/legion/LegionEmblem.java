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

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Represents the emblem associated with a specific {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * This class stores the visual and identifying data for the legion's insignia.
 * @author Simple modified cura
 */
public class LegionEmblem
{
	private int emblemId = 0x00;
	private int color_r = 0x00;
	private int color_g = 0x00;
	private int color_b = 0x00;
	private boolean defaultEmblem = true;
	private LegionEmblemType emblemType = LegionEmblemType.DEFAULT;
	private PersistentState persistentState;
	private boolean isUploading = false;
	private int uploadSize = 0;
	private int uploadedSize = 0;
	private byte[] uploadData;
	private byte[] customEmblemData;
	
	/**
	 * Retrieves the raw data for a custom emblem.<br>
	 * This method returns the {@code byte[]} array stored in this object.
	 * @return the {@code byte[]} array containing the custom emblem data.
	 */
	public byte[] getCustomEmblemData()
	{
		return customEmblemData;
	}
	
	/**
	 * Sets the raw data for a custom emblem.<br>
	 * This method updates the {@code persistentState} to require an update.<br>
	 * It also changes the {@link LegionEmblemType} to {@code CUSTOM}.
	 * @param customEmblemData The byte array containing the emblem image data.
	 */
	public void setCustomEmblemData(byte[] customEmblemData)
	{
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		this.customEmblemData = customEmblemData;
		emblemType = LegionEmblemType.CUSTOM;
	}
	
	/**
	 * Creates a new instance of {@link LegionEmblem}.<br>
	 * This constructor initializes the object with a {@code PersistentState.NEW} state.
	 */
	public LegionEmblem()
	{
		setPersistentState(PersistentState.NEW);
	}
	
	/**
	 * Updates the emblem details for a legion.<br>
	 * This method sets the ID, colors, type, and data.<br>
	 * It also marks the state as requiring an update.
	 * @param emblemId The unique identifier for the emblem.
	 * @param color_r The red component of the emblem color.
	 * @param color_g The green component of the emblem color.
	 * @param color_b The blue component of the emblem color.
	 * @param emblemType The {@link LegionEmblemType} category.
	 * @param emblem_data The raw byte array containing custom emblem data.
	 */
	public void setEmblem(int emblemId, int color_r, int color_g, int color_b, LegionEmblemType emblemType, byte[] emblem_data)
	{
		this.emblemId = emblemId;
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
		this.emblemType = emblemType;
		customEmblemData = emblem_data;
		if (this.emblemType.equals(LegionEmblemType.CUSTOM) && (customEmblemData == null))
		{
			this.emblemId = 0;
			this.emblemType = LegionEmblemType.DEFAULT;
		}
		
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		defaultEmblem = false;
	}
	
	/**
	 * Retrieves the unique identifier for the legion emblem.<br>
	 * This value is used to identify which emblem is currently active.
	 * @return the {@code int} ID of the emblem.
	 */
	public int getEmblemId()
	{
		return emblemId;
	}
	
	/**
	 * Retrieves the red component of the emblem color.<br>
	 * This value is part of the {@code color_r} field.
	 * @return the red color value as an {@code int}.
	 */
	public int getColor_r()
	{
		return color_r;
	}
	
	/**
	 * Retrieves the green color component of the emblem.<br>
	 * This value is part of the RGB color set for the {@link LegionEmblem}.
	 * @return the green color value as an {@code int}
	 */
	public int getColor_g()
	{
		return color_g;
	}
	
	/**
	 * Retrieves the blue component of the emblem color.<br>
	 * This value is part of the {@code color_r}, {@code color_g}, and {@code color_b} set.
	 * @return the integer value representing the blue color component.
	 */
	public int getColor_b()
	{
		return color_b;
	}
	
	/**
	 * Checks if the emblem is set to the default value.<br>
	 * This method returns {@code true} if it is a default emblem.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the emblem is default, {@code false} otherwise.
	 */
	public boolean isDefaultEmblem()
	{
		return defaultEmblem;
	}
	
	/**
	 * Updates the upload status of the emblem.<br>
	 * This method sets whether the object is currently in the process of uploading.
	 * @param isUploading The new upload status to set.
	 */
	public void setUploading(boolean isUploading)
	{
		this.isUploading = isUploading;
	}
	
	/**
	 * Checks if the emblem is currently being uploaded.<br>
	 * This method returns the current status of the upload process.
	 * @return {@code true} if an upload is in progress, {@code false} otherwise.
	 */
	public boolean isUploading()
	{
		return isUploading;
	}
	
	/**
	 * Sets the total size of the emblem being uploaded.<br>
	 * This updates the {@code uploadSize} field.
	 * @param emblemSize The size of the emblem in bytes.
	 */
	public void setUploadSize(int emblemSize)
	{
		uploadSize = emblemSize;
	}
	
	/**
	 * Retrieves the total size of the data to be uploaded.<br>
	 * This value is stored in bytes.
	 * @return the current {@code uploadSize} as an {@code int}.
	 */
	public int getUploadSize()
	{
		return uploadSize;
	}
	
	/**
	 * This method appends new bytes to the existing {@code uploadData}.<br>
	 * It creates a new array based on the current {@code uploadedSize}.<br>
	 * The data is merged into the internal buffer.
	 * @param data The new byte array to add to the upload.
	 */
	public void addUploadData(byte[] data)
	{
		final byte[] newData = new byte[uploadedSize];
		int i = 0;
		if ((uploadData != null) && (uploadData.length > 0))
		{
			for (byte dataByte : uploadData)
			{
				newData[i] = dataByte;
				i++;
			}
		}
		
		for (byte dataByte : data)
		{
			newData[i] = dataByte;
			i++;
		}
		
		uploadData = newData;
	}
	
	/**
	 * Retrieves the raw data for the current upload.<br>
	 * This method returns the {@code byte[]} array stored in the {@code uploadData} field.
	 * @return the {@code byte[]} array containing the upload data.
	 */
	public byte[] getUploadData()
	{
		return uploadData;
	}
	
	/**
	 * Updates the total size of the data that has been uploaded.<br>
	 * This method adds the provided value to the current {@code uploadedSize}.
	 * @param uploadedSize The amount of data to add to the total.
	 */
	public void addUploadedSize(int uploadedSize)
	{
		this.uploadedSize += uploadedSize;
	}
	
	/**
	 * Retrieves the total size of the data that has been uploaded.<br>
	 * This value is updated as data is processed.
	 * @return the current {@code uploadedSize} in bytes.
	 */
	public int getUploadedSize()
	{
		return uploadedSize;
	}
	
	/**
	 * Sets the type of the legion emblem.<br>
	 * This updates the {@code emblemType} field in this object.
	 * @param emblemType The new {@link LegionEmblemType} to assign.
	 */
	public void setEmblemType(LegionEmblemType emblemType)
	{
		this.emblemType = emblemType;
	}
	
	/**
	 * Retrieves the current type of the legion emblem.<br>
	 * This method returns the {@code LegionEmblemType} assigned to this object.
	 * @return the current {@code LegionEmblemType}
	 */
	public LegionEmblemType getEmblemType()
	{
		return emblemType;
	}
	
	/**
	 * Resets the upload status and data for this emblem.<br>
	 * It sets {@code isUploading} to {@code false}.<br>
	 * It clears the {@code uploadedSize} to {@code 0}.<br>
	 * It sets the {@code uploadData} array to {@code null}.
	 */
	public void resetUploadSettings()
	{
		isUploading = false;
		uploadedSize = 0;
		uploadData = null;
	}
	
	/**
	 * Updates the {@code persistentState} of this quest.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
				{
					break;
				}
			default:
				this.persistentState = persistentState;
		}
	}
	
	/**
	 * Retrieves the current state of this challenge.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
}
