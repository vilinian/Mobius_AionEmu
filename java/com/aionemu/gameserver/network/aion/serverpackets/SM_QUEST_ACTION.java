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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestExtraCategory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This packet handles actions related to quest progression.<br>
 * It is used to notify the client about specific changes in a {@link QuestTemplate}.
 * @author VladimirZ
 */
public class SM_QUEST_ACTION extends AionServerPacket
{
	protected int questId;
	private int status;
	private int step;
	protected int action;
	private int timer;
	private int sharerId;
	@SuppressWarnings("unused")
	private boolean unk;
	
	/**
	 * Creates a new instance of the {@code SM_QUEST_ACTION} packet.<br>
	 * This packet is used to notify the client about quest progress updates.
	 */
	SM_QUEST_ACTION()
	{
	}
	
	/**
	 * Creates a new {@code SM_QUEST_ACTION} packet.<br>
	 * This method initializes the quest data for the client.<br>
	 * It sets the action type to {@code 1}.
	 * @param questId The unique identifier for the quest.
	 * @param status The current progress status of the quest.
	 * @param step The specific step number within the quest.
	 */
	public SM_QUEST_ACTION(int questId, int status, int step)
	{
		action = 1;
		this.questId = questId;
		this.status = status;
		this.step = step;
	}
	
	/**
	 * Creates a new {@code SM_QUEST_ACTION} packet.<br>
	 * This packet updates the progress of a specific quest.<br>
	 * It sets the action type to {@code 2}.
	 * @param questId The unique identifier for the quest.
	 * @param status The current state of the quest using the {@link QuestStatus} enum.
	 * @param step The current progress step within the quest.
	 */
	public SM_QUEST_ACTION(int questId, QuestStatus status, int step)
	{
		action = 2;
		this.questId = questId;
		this.status = status.value();
		this.step = step;
	}
	
	/**
	 * Creates a new {@code SM_QUEST_ACTION} packet.<br>
	 * This constructor sets the action type to {@code 3}.<br>
	 * It initializes the packet with the provided quest identifier.
	 * @param questId The unique ID of the quest associated with this action.
	 */
	public SM_QUEST_ACTION(int questId)
	{
		action = 3;
		this.questId = questId;
	}
	
	/**
	 * Creates a new {@code SM_QUEST_ACTION} packet.<br>
	 * This constructor sets the action type to {@code 4}.<br>
	 * It initializes the quest progress with a step of {@code 0}.
	 * @param questId The unique identifier for the quest.
	 * @param timer The time duration associated with this action.
	 */
	public SM_QUEST_ACTION(int questId, int timer)
	{
		action = 4;
		this.questId = questId;
		this.timer = timer;
		step = 0;
	}
	
	/**
	 * Creates a new {@code SM_QUEST_ACTION} packet.<br>
	 * This constructor sets the action type to {@code 5}.<br>
	 * It initializes the quest data and sharer information.
	 * @param questId The unique identifier for the quest.
	 * @param sharerId The unique identifier for the player sharing the quest.
	 * @param unk An unknown boolean flag used by the server.
	 */
	public SM_QUEST_ACTION(int questId, int sharerId, boolean unk)
	{
		action = 5;
		this.questId = questId;
		this.sharerId = sharerId;
		this.unk = unk;
	}
	
	/**
	 * Creates a new {@code SM_QUEST_ACTION} packet for a specific quest.<br>
	 * This constructor sets the action type to {@code 6}.<br>
	 * It initializes the timer and step values to {@code 0}.
	 * @param questId The unique identifier for the quest.
	 * @param fake A boolean flag indicating if the action is simulated.
	 */
	public SM_QUEST_ACTION(int questId, boolean fake)
	{
		action = 6;
		this.questId = questId;
		timer = 0;
		step = 0;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(questId);
		if ((questTemplate != null) && (questTemplate.getExtraCategory() != QuestExtraCategory.NONE))
		{
			return;
		}
		
		writeC(action);
		writeD(questId);
		switch (action)
		{
			case 1:
				writeC(status); // quest status goes by ENUM value
				writeC(0x0);
				writeD(step); // current quest step
				writeH(0);
				writeC(0);
				break;
			case 2:
				writeC(status); // quest status goes by ENUM value
				writeC(0x0);
				writeD(step); // current quest step
				writeH(0);
				break;
			case 3:
				writeD(0);
				break;
			case 4:
				writeD(timer); // sets client timer ie 84030000 is 900 seconds/15 mins
				writeC(0x01);
				writeH(0x0);
				writeC(0x01);
				break;
			case 5:
				writeD(sharerId);
				writeD(0);
				break;
			case 6:
				writeH(0x01); // ???
				writeH(0x0);
				break;
		}
	}
}
