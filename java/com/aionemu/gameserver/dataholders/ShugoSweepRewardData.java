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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.shugosweep.ShugoSweepReward;

/**
 * This class holds the data for rewards obtained from a {@link com.aionemu.gameserver.model.templates.shugosweep.ShugoSweepReward} event.<br>
 * It serves as a data container to manage reward information within the game server.
 * @author Ghostfur
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"ShugoSweepRewardData"
})
@XmlRootElement(name = "shugo_sweeps")
public class ShugoSweepRewardData
{
	@XmlElement(name = "shugo_sweep")
	protected List<ShugoSweepReward> ShugoSweepRewardData;
	
	@XmlTransient
	protected List<ShugoSweepReward> ShugoSweepRewardList = new ArrayList<>();
	
	/**
	 * This method is called after the XML data is loaded.<br>
	 * It copies elements from the {@code ShugoSweepRewardData} list into the internal {@code ShugoSweepRewardList}.<br>
	 * The original list is then cleared and set to {@code null}.
	 * @param unmarshaller The {@link Unmarshaller} used to read the data.
	 * @param parent The object that contains this data.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		for (ShugoSweepReward reward : ShugoSweepRewardData)
		{
			ShugoSweepRewardList.add(reward);
		}
		
		ShugoSweepRewardData.clear();
		ShugoSweepRewardData = null;
	}
	
	/**
	 * Retrieves a specific reward from the Shugo Sweep rewards list.<br>
	 * It searches for a match based on the provided board and reward numbers.
	 * @param boardId The unique identifier for the reward board.
	 * @param rewardNum The specific number of the reward on that board.
	 * @return The matching {@link ShugoSweepReward} object, or {@code null} if no match is found.
	 */
	public ShugoSweepReward getRewardBoard(int boardId, int rewardNum)
	{
		for (ShugoSweepReward reward : ShugoSweepRewardList)
		{
			if ((reward.getBoardId() == boardId) && (reward.getRewardNum() == rewardNum))
			{
				return reward;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return ShugoSweepRewardList.size();
	}
}
