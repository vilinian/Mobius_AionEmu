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

import java.util.Collection;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.PetAction;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.templates.pet.PetDopingEntry;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the synchronization of pet-related data between the server and the client.<br>
 * It is used to update information regarding {@link Pet} objects, including their actions and status.
 * @author M@xx, xTz, Rolandas
 */
public class SM_PET extends AionServerPacket
{
	private final int actionId;
	private Pet pet;
	private PetCommonData commonData;
	private int itemObjectId;
	private Collection<PetCommonData> pets;
	private int count;
	private int subType;
	private int shuggleEmotion;
	private boolean isActing;
	private int lootNpcId;
	private int dopeAction;
	private int dopeSlot;
	
	/**
	 * Creates a new {@code SM_PET} packet.<br>
	 * This constructor initializes the pet action and data.
	 * @param subType The specific type of the pet action.
	 * @param actionId The unique identifier for the action.
	 * @param objectId The ID of the object associated with this packet.
	 * @param count The number of items or actions to process.
	 * @param pet The {@link Pet} object being referenced.
	 */
	public SM_PET(int subType, int actionId, int objectId, int count, Pet pet)
	{
		this.subType = subType;
		this.actionId = actionId;
		this.count = count;
		itemObjectId = objectId;
		this.pet = pet;
		commonData = pet.getCommonData();
	}
	
	/**
	 * Creates a new {@link SM_PET} packet with a specific action identifier.<br>
	 * This constructor initializes the {@code actionId} field.
	 * @param actionId The unique ID for the pet action.
	 */
	public SM_PET(int actionId)
	{
		this.actionId = actionId;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet with a specific action and pet.<br>
	 * This constructor initializes the packet using default values for other fields.
	 * @param actionId The unique identifier for the pet action.
	 * @param pet The {@code Pet} object associated with this packet.
	 */
	public SM_PET(int actionId, Pet pet)
	{
		this(0, actionId, 0, 0, pet);
	}
	
	/**
	 * Creates a new {@link SM_PET} packet for looting actions.<br>
	 * This constructor sets the {@code actionId} to {@code 13}.<br>
	 * It also sets the {@code subType} to {@code 3}.
	 * @param isLooting The status of the looting action.
	 */
	public SM_PET(boolean isLooting)
	{
		actionId = 13;
		isActing = isLooting;
		subType = 3;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet for looting actions.<br>
	 * This constructor sets the loot status and the specific NPC identifier.
	 * @param isLooting A boolean indicating if the pet is currently looting.
	 * @param npcId The unique ID of the NPC being looted.
	 */
	public SM_PET(boolean isLooting, int npcId)
	{
		this(isLooting);
		lootNpcId = npcId;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet for pet doping actions.<br>
	 * This constructor sets the action ID to {@code 13}.<br>
	 * It also initializes the sub type to {@code 2}.
	 * @param dopeAction The specific action associated with the dope.
	 * @param isBuffing A boolean indicating if the pet is currently buffing.
	 */
	public SM_PET(int dopeAction, boolean isBuffing)
	{
		actionId = 13;
		this.dopeAction = dopeAction;
		isActing = isBuffing;
		subType = 2;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet for doping actions.<br>
	 * This constructor sets the buffing status to {@code true}.<br>
	 * It initializes the pet's item template and specific slot.
	 * @param dopeAction The unique identifier for the doping action.
	 * @param itemId The template ID of the item used.
	 * @param slot The specific slot index for the item.
	 */
	public SM_PET(int dopeAction, int itemId, int slot)
	{
		this(dopeAction, true);
		itemObjectId = itemId; // it's template ID, not objectId though
		dopeSlot = slot;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet with specific buffing and action data.<br>
	 * This constructor sets the internal {@code actionId} to {@code 13}.<br>
	 * It maps the buffing status to the {@code isActing} field.
	 * @param isBuffing A boolean indicating if the pet is currently buffing.
	 * @param what An integer representing the primary action type.
	 * @param wahtwaht An integer representing the secondary action data.
	 */
	public SM_PET(boolean isBuffing, int what, int wahtwaht)
	{
		actionId = 13;
		isActing = isBuffing;
		subType = 5;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet for a specific pet.<br>
	 * This constructor initializes the packet with default action values.<br>
	 * It sets the unique subtype and shuffle emotion provided.
	 * @param pet The {@code Pet} object associated with this packet.
	 * @param subType The integer identifier for the pet subtype.
	 * @param shuggleEmotion The integer value representing the shuffle emotion.
	 */
	public SM_PET(Pet pet, int subType, int shuggleEmotion)
	{
		this(0, PetAction.MOOD.getActionId(), 0, 0, pet);
		this.shuggleEmotion = shuggleEmotion;
		this.subType = subType;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet with specific action and data.<br>
	 * This constructor initializes the basic pet information for network transmission.
	 * @param actionId The unique identifier for the pet action.
	 * @param commonData The shared data associated with the pet.
	 */
	public SM_PET(int actionId, PetCommonData commonData)
	{
		this.actionId = actionId;
		this.commonData = commonData;
	}
	
	/**
	 * Creates a new {@link SM_PET} packet with a specific action and a list of pets.<br>
	 * This constructor is used when multiple pet data objects need to be sent together.
	 * @param actionId The unique identifier for the action being performed.
	 * @param pets A {@code Collection} of {@link PetCommonData} objects representing the pets involved.
	 */
	public SM_PET(int actionId, Collection<PetCommonData> pets)
	{
		this.actionId = actionId;
		this.pets = pets;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		PetTemplate petTemplate = null;
		writeH(actionId);
		switch (actionId)
		{
			case 0:
				// load list on login
				writeC(0); // unk
				writeH(pets.size());
				for (PetCommonData petCommonData : pets)
				{
					petTemplate = DataManager.PET_DATA.getPetTemplate(petCommonData.getPetId());
					final int expireTime = petCommonData.getExpireTime();
					writeS(petCommonData.getName());
					writeD(petCommonData.getPetId());
					writeD(petCommonData.getObjectId());
					writeD(petCommonData.getMasterObjectId());
					writeD(0);
					writeD(0);
					writeD(petCommonData.getBirthday());
					writeD(expireTime != 0 ? expireTime - (int) (System.currentTimeMillis() / 1000) : 0); // accompanying time
					
					int specialtyCount = 0;
					if (petTemplate.ContainsFunction(PetFunctionType.WAREHOUSE))
					{
						writeH(PetFunctionType.WAREHOUSE.getId());
						specialtyCount++;
					}
					
					if (petTemplate.ContainsFunction(PetFunctionType.LOOT))
					{
						writeH(PetFunctionType.LOOT.getId());
						writeC(0);
						specialtyCount++;
					}
					
					if (petTemplate.ContainsFunction(PetFunctionType.BUFF))
					{
						writeH(PetFunctionType.BUFF.getId());
						specialtyCount++;
					}
					
					if (petTemplate.ContainsFunction(PetFunctionType.MERCHANT))
					{
						writeH(PetFunctionType.MERCHANT.getId());
						specialtyCount++;
					}
					
					if (petTemplate.ContainsFunction(PetFunctionType.DOPING))
					{
						writeH(PetFunctionType.DOPING.getId());
						final short dopeId = (short) petTemplate.getPetFunction(PetFunctionType.DOPING).getId();
						final PetDopingEntry dope = DataManager.PET_DOPING_DATA.getDopingTemplate(dopeId);
						writeD(dope.isUseFood() ? petCommonData.getDopingBag().getFoodItem() : 0);
						writeD(dope.isUseDrink() ? petCommonData.getDopingBag().getDrinkItem() : 0);
						final int[] scrollBag = petCommonData.getDopingBag().getScrollsUsed();
						if (scrollBag.length == 0)
						{
							writeQ(0);
							writeQ(0);
							writeQ(0);
						}
						else
						{
							writeD(scrollBag[0]); // Scroll 1
							writeD(scrollBag.length > 1 ? scrollBag[1] : 0); // Scroll 2
							writeD(scrollBag.length > 2 ? scrollBag[2] : 0); // Scroll 3 - no pet supports it yet
							writeD(scrollBag.length > 3 ? scrollBag[3] : 0); // Scroll 4 - no pet supports it yet
							writeD(scrollBag.length > 4 ? scrollBag[4] : 0); // Scroll 5 - no pet supports it yet
							writeD(scrollBag.length > 5 ? scrollBag[5] : 0); // Scroll 6 - no pet supports it yet
						}
						
						specialtyCount++;
					}
					
					if (petTemplate.ContainsFunction(PetFunctionType.FOOD))
					{
						writeH(PetFunctionType.FOOD.getId());
						writeD(petCommonData.getFeedProgress().getDataForPacket());
						writeD((int) petCommonData.getRefeedDelay() / 1000);
						specialtyCount++;
					}
					
					// Pets have only 2 functions max. If absent filled with NONE
					if (specialtyCount == 0)
					{
						writeH(PetFunctionType.NONE.getId());
						writeH(PetFunctionType.NONE.getId());
					}
					else if (specialtyCount == 1)
					{
						writeH(PetFunctionType.NONE.getId());
					}
					
					writeH(PetFunctionType.APPEARANCE.getId());
					writeC(0); // not implemented color R ?
					writeC(0); // not implemented color G ?
					writeC(0); // not implemented color B ?
					writeD(petCommonData.getDecoration());
					
					// epilog
					writeD(0); // unk
					writeD(0); // unk
				}
				break;
			case 1:
				// adopt
				writeS(commonData.getName());
				writeD(commonData.getPetId());
				writeD(commonData.getObjectId());
				writeD(commonData.getMasterObjectId());
				writeD(0);
				writeD(0);
				writeD(commonData.getBirthday());
				writeD(commonData.getExpireTime() != 0 ? commonData.getExpireTime() - (int) (System.currentTimeMillis() / 1000) : 0); // accompanying time
				petTemplate = DataManager.PET_DATA.getPetTemplate(commonData.getPetId());
				int specialtyCount = 0;
				if (petTemplate.ContainsFunction(PetFunctionType.WAREHOUSE))
				{
					writeH(PetFunctionType.WAREHOUSE.getId());
					specialtyCount++;
				}
				
				if (petTemplate.ContainsFunction(PetFunctionType.LOOT))
				{
					writeH(PetFunctionType.LOOT.getId());
					writeC(0);
					specialtyCount++;
				}
				
				if (petTemplate.ContainsFunction(PetFunctionType.DOPING))
				{
					writeH(PetFunctionType.DOPING.getId());
					writeQ(0);
					writeQ(0);
					writeQ(0);
					writeQ(0);
					specialtyCount++;
				}
				
				if (petTemplate.ContainsFunction(PetFunctionType.FOOD))
				{
					writeH(PetFunctionType.FOOD.getId());
					writeQ(0);
					specialtyCount++;
				}
				
				// Pets have only 2 functions max. If absent filled with NONE
				if (specialtyCount == 0)
				{
					writeH(PetFunctionType.NONE.getId());
					writeH(PetFunctionType.NONE.getId());
				}
				else if (specialtyCount == 1)
				{
					writeH(PetFunctionType.NONE.getId());
				}
				
				writeH(PetFunctionType.APPEARANCE.getId());
				writeC(0); // not implemented color R ?
				writeC(0); // not implemented color G ?
				writeC(0); // not implemented color B ?
				writeD(commonData.getDecoration());
				
				// epilog
				writeD(0); // unk
				writeD(0); // unk
				break;
			case 2:
				// surrender
				writeD(commonData.getPetId());
				writeD(commonData.getObjectId());
				writeD(0); // unk
				writeD(0); // unk
				break;
			case 3:
				// spawn
				writeS(pet.getName());
				writeD(pet.getPetId());
				writeD(pet.getObjectId());
				
				if ((pet.getPosition().getX() == 0) && (pet.getPosition().getY() == 0) && (pet.getPosition().getZ() == 0))
				{
					writeF(pet.getMaster().getX());
					writeF(pet.getMaster().getY());
					writeF(pet.getMaster().getZ());
					
					writeF(pet.getMaster().getX());
					writeF(pet.getMaster().getY());
					writeF(pet.getMaster().getZ());
					
					writeC(pet.getMaster().getHeading());
				}
				else
				{
					writeF(pet.getPosition().getX());
					writeF(pet.getPosition().getY());
					writeF(pet.getPosition().getZ());
					writeF(pet.getMoveController().getTargetX2());
					writeF(pet.getMoveController().getTargetY2());
					writeF(pet.getMoveController().getTargetZ2());
					writeC(pet.getHeading());
				}
				
				writeD(pet.getMaster().getObjectId()); // unk
				
				writeC(1); // unk
				writeD(0); // accompanying time ??
				writeD(pet.getCommonData().getDecoration());
				writeD(0); // wings ID if customize_attach = 1
				writeD(0); // unk
				break;
			case 4:
				// dismiss
				writeD(pet.getObjectId());
				writeC(0x01);
				break;
			case 9:
				writeH(1);
				writeC(1);
				writeC(subType);
				switch (subType)
				{
					case 1: // eat
						writeD(commonData.getFeedProgress().getDataForPacket());
						writeD(0);
						writeD(itemObjectId);
						writeD(count);
						break;
					case 2: // eating successful
						writeD(commonData.getFeedProgress().getDataForPacket());
						writeD(0);
						writeD(itemObjectId);
						writeD(count);
						writeC(0);
						break;
					case 3: // not hungry
					case 4: // cancel feed
					case 5: // clean feed task
						writeD(commonData.getFeedProgress().getDataForPacket());
						writeD((int) commonData.getRefeedDelay() / 1000);
						break;
					case 6: // give item
						writeD(commonData.getFeedProgress().getDataForPacket());
						writeD(0);
						writeD(itemObjectId);
						writeC(0);
						break;
					case 7: // present notification
						writeD(commonData.getFeedProgress().getDataForPacket());
						writeD((int) commonData.getRefeedDelay() / 1000); // time
						writeD(itemObjectId);
						writeD(0);
						break;
					case 8: // is full
						writeD(commonData.getFeedProgress().getDataForPacket());
						writeD((int) commonData.getRefeedDelay() / 1000);
						writeD(itemObjectId);
						writeD(count);
						break;
				}
				break;
			case 10:
				// rename
				writeD(pet.getObjectId());
				writeS(pet.getName());
				break;
			case 12:
				switch (subType)
				{
					case 0: // check pet status
						writeC(subType);
						
						// desynced feedback data, need to send delta in percents
						if (commonData.getLastSentPoints() < commonData.getMoodPoints(true))
						{
							writeD(commonData.getMoodPoints(true) - commonData.getLastSentPoints());
						}
						else
						{
							writeD(0);
							commonData.setLastSentPoints(commonData.getMoodPoints(true));
						}
						break;
					case 2: // emotion sent
						writeC(subType);
						writeD(0);
						writeD(pet.getCommonData().getMoodPoints(true));
						writeD(shuggleEmotion);
						commonData.setLastSentPoints(pet.getCommonData().getMoodPoints(true));
						commonData.setMoodCdStarted(System.currentTimeMillis());
						break;
					case 3: // give gift
						writeC(subType);
						writeD(pet.getPetTemplate().getConditionReward());
						commonData.setGiftCdStarted(System.currentTimeMillis());
						break;
					case 4: // periodic update
						writeC(subType);
						writeD(commonData.getMoodPoints(true));
						writeD(commonData.getMoodRemainingTime());
						writeD(commonData.getGiftRemainingTime());
						commonData.setLastSentPoints(pet.getCommonData().getMoodPoints(true));
						break;
				}
				break;
			case 13:
				writeC(subType);
				if (subType == 2)
				{
					writeC(dopeAction);
					switch (dopeAction)
					{
						case 0: // add item
							writeD(itemObjectId);
							writeD(dopeSlot);
							break;
						case 1: // remove item
							writeD(0);
							break;
						case 2: // TODO: move item from one slot to other
							break;
						case 3: // use item
							writeD(itemObjectId);
							break;
					}
				}
				else if (subType == 3)
				{
					// looting NPC
					if (lootNpcId > 0)
					{
						writeC(isActing ? 1 : 2); // 0x02 display looted msg.
						writeD(lootNpcId);
					}
					else
					{
						// loot function activation
						writeC(0);
						writeC(isActing ? 1 : 0);
					}
				}
				else if (subType == 4)
				{
					writeC(0);
					writeC(isActing ? 1 : 0);
				}
				else if (subType == 5)
				{
					writeC(isActing ? 0 : 1);
				}
				break;
			default:
				break;
		}
	}
}
