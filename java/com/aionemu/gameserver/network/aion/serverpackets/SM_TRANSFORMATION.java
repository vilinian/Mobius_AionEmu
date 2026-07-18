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
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.account.AccountTransformList;
import com.aionemu.gameserver.model.account.TransformCollection;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transformation of a player character.<br>
 * It is used to update the visual appearance and state of a {@link Player} when they change forms.
 * @author Falke_34, FrozenKiller
 */
public class SM_TRANSFORMATION extends AionServerPacket
{
	private final int action;
	private AccountTransformList transformList;
	private int cardId;
	private int result;
	private Player player;
	private int code;
	List<Integer> deleteList;
	List<AccountTransfo> createdTransform;
	
	/**
	 * Creates a new {@code SM_TRANSFORMATION} packet for a specific player.<br>
	 * This constructor initializes the required lists and sets the action type.<br>
	 * It retrieves the transform list from the provided {@link Player}.
	 * @param action The type of transformation action to perform.
	 * @param player The {@link Player} object associated with this packet.
	 */
	public SM_TRANSFORMATION(int action, Player player)
	{
		deleteList = new ArrayList<>();
		createdTransform = new ArrayList<>();
		this.action = action;
		transformList = player.getTransformList();
		this.player = player;
	}
	
	/**
	 * This constructor initializes a transformation packet for a specific player.<br>
	 * It sets the action, card ID, and result codes required for the transformation.<br>
	 * The {@link Player} object is used to retrieve the current transform list.
	 * @param action The type of action being performed.
	 * @param player The {@code Player} instance involved in the transformation.
	 * @param cardId The unique identifier for the card used.
	 * @param result The numerical result of the transformation attempt.
	 * @param code The specific status code returned by the server.
	 */
	public SM_TRANSFORMATION(int action, Player player, int cardId, int result, int code)
	{
		deleteList = new ArrayList<>();
		createdTransform = new ArrayList<>();
		this.action = action;
		transformList = player.getTransformList();
		this.cardId = cardId;
		this.result = result;
		this.player = player;
		this.code = code;
	}
	
	/**
	 * Creates a new {@link SM_TRANSFORMATION} packet.<br>
	 * This constructor initializes the transformation data for a player.<br>
	 * It sets the default action to {@code 1}.
	 * @param createdTransform The list of newly created transformations.
	 * @param result The numerical result of the operation.
	 * @param code The specific status code returned by the server.
	 */
	public SM_TRANSFORMATION(List<AccountTransfo> createdTransform, int result, int code)
	{
		deleteList = new ArrayList<>();
		this.createdTransform = new ArrayList<>();
		action = 1;
		this.result = result;
		this.code = code;
		this.createdTransform = createdTransform;
	}
	
	/**
	 * Creates a new {@code SM_TRANSFORMATION} packet.<br>
	 * This constructor initializes the basic transformation data.<br>
	 * It sets the specific action and the associated card identifier.
	 * @param action The type of action to perform.
	 * @param cardId The unique identifier for the card.
	 */
	public SM_TRANSFORMATION(int action, int cardId)
	{
		deleteList = new ArrayList<>();
		createdTransform = new ArrayList<>();
		this.action = action;
		this.cardId = cardId;
	}
	
	/**
	 * Creates a new {@code SM_TRANSFORMATION} packet.<br>
	 * This constructor initializes the transformation data with a specific action and a list of IDs to remove.
	 * @param action The type of action to perform.
	 * @param deleteList A {@code List<Integer>} containing the IDs that should be deleted.
	 */
	public SM_TRANSFORMATION(int action, List<Integer> deleteList)
	{
		this.deleteList = new ArrayList<>();
		createdTransform = new ArrayList<>();
		this.action = action;
		this.deleteList = deleteList;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(action);
		switch (action)
		{
			case 0:
			{
				writeD(player.getLastUsedTransformation());
				writeC(0);
				if ((transformList != null) && (transformList.getTransformations().size() != 0))
				{
					writeH(transformList.getTransformations().size());
					for (AccountTransfo transfo : transformList.getTransformations())
					{
						writeD(transfo.getCardId());
						writeD(transfo.getCount());
					}
					break;
				}
				
				writeH(0);
				break;
			}
			case 1:
			{
				writeH(result);
				switch (result)
				{
					case 1:
					{
						int count = 0;
						for (AccountTransfo transfo2 : createdTransform)
						{
							writeD(transfo2.getCardId());
							++count;
						}
						
						for (int i = 0; i < (10 - count); ++i)
						{
							writeD(0);
						}
						break;
					}
					case 3:
					{
						writeD(cardId);
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
						writeD(0);
						break;
					}
				}
				
				writeD(code);
				break;
			}
			case 2:
			{
				writeH(deleteList.size());
				for (Integer trans : deleteList)
				{
					writeD(trans);
					writeD(1);
				}
				break;
			}
			case 3:
			{
				if (player.getTransformCollections().size() == 0)
				{
					writeB(new byte[DataManager.TRANSFORM_COLLECTION_DATA.size() * 4]);
					break;
				}
				
				for (TransformCollection collection : player.getTransformCollections().values())
				{
					writeD(collection.getId());
				}
				
				for (int diff = DataManager.TRANSFORM_COLLECTION_DATA.size() - player.getTransformCollections().size(), i = 0; i < diff; ++i)
				{
					writeD(0);
				}
				break;
			}
		}
	}
}
