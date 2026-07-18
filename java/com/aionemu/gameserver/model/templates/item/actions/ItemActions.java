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
package com.aionemu.gameserver.model.templates.item.actions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the actions that can be performed on items within the game.<br>
 * It serves as a container for various {@code List} entries of item behaviors.<br>
 * Developers can use this to configure how specific items interact with players or the world.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemActions")
public class ItemActions
{
	@XmlElements(
	{
		@XmlElement(name = "skilllearn", type = SkillLearnAction.class),
		@XmlElement(name = "extract", type = ExtractAction.class),
		@XmlElement(name = "skilluse", type = SkillUseAction.class),
		@XmlElement(name = "enchant", type = EnchantItemAction.class),
		@XmlElement(name = "queststart", type = QuestStartAction.class),
		@XmlElement(name = "dye", type = DyeAction.class),
		@XmlElement(name = "craftlearn", type = CraftLearnAction.class),
		@XmlElement(name = "toypetspawn", type = ToyPetSpawnAction.class),
		@XmlElement(name = "decompose", type = DecomposeAction.class),
		@XmlElement(name = "titleadd", type = TitleAddAction.class),
		@XmlElement(name = "learnemotion", type = EmotionLearnAction.class),
		@XmlElement(name = "read", type = ReadAction.class),
		@XmlElement(name = "fireworkact", type = FireworksUseAction.class),
		@XmlElement(name = "instancetimeclear", type = InstanceTimeClear.class),
		@XmlElement(name = "expandinventory", type = ExpandInventoryAction.class),
		@XmlElement(name = "animation", type = AnimationAddAction.class),
		@XmlElement(name = "cosmetic", type = CosmeticItemAction.class),
		@XmlElement(name = "charge", type = ChargeAction.class),
		@XmlElement(name = "ride", type = RideAction.class),
		@XmlElement(name = "houseobject", type = SummonHouseObjectAction.class),
		@XmlElement(name = "housedeco", type = DecorateAction.class),
		@XmlElement(name = "assemble", type = AssemblyItemAction.class),
		@XmlElement(name = "adoptpet", type = AdoptPetAction.class),
		@XmlElement(name = "apextract", type = ApExtractAction.class),
		@XmlElement(name = "remodel", type = RemodelAction.class),
		@XmlElement(name = "expextract", type = ExpExtractAction.class),
		@XmlElement(name = "polish", type = PolishAction.class),
		@XmlElement(name = "composition", type = CompositionAction.class),
		@XmlElement(name = "tuning", type = TuningAction.class),
		@XmlElement(name = "pack", type = PackAction.class),
		@XmlElement(name = "stenchant", type = AuthorizeAction.class),
		@XmlElement(name = "multi_return_item", type = MultiReturnAction.class),
		@XmlElement(name = "godsocket", type = GodstoneAction.class),
		@XmlElement(name = "remove_soul_bind", type = RemoveSoulBindAction.class),
		@XmlElement(name = "expaction", type = ExpAction.class),
		@XmlElement(name = "skill_skin", type = SkillAnimationAction.class),
		@XmlElement(name = "f2p", type = F2pAction.class),
		@XmlElement(name = "reduce_level", type = ItemReduceLevelAction.class),
		@XmlElement(name = "luna", type = LunaChestAction.class),
		@XmlElement(name = "skill_enhance", type = SkillEnhanceAction.class),
		@XmlElement(name = "sweep", type = ShugoSweepAction.class),
		@XmlElement(name = "unseal", type = UnSealAction.class),
		@XmlElement(name = "manastone_slot_expansion", type = ManastoneSlotExpansionAction.class),
		@XmlElement(name = "fame_exp", type = FameAddExpAction.class),
		@XmlElement(name = "enchant_grind", type = EnchantGrindingAction.class),
		@XmlElement(name = "grind_slot_expansion", type = GrindSlotExpansionAction.class),
		@XmlElement(name = "enchant_glyph", type = EnchantGlyphAction.class)
	})
	
	protected List<AbstractItemAction> itemActions;
	
	/**
	 * Retrieves the list of actions associated with an item.<br>
	 * This method ensures that a non-null {@code List} is always returned.
	 * @return a {@code List} of {@link AbstractItemAction} objects.
	 */
	public List<AbstractItemAction> getItemActions()
	{
		if (itemActions == null)
		{
			itemActions = new ArrayList<>();
		}
		
		return itemActions;
	}
	
	/**
	 * Retrieves all actions related to spawning toy pets.<br>
	 * This method filters the list of {@link AbstractItemAction} objects.<br>
	 * It returns only those that are instances of {@code ToyPetSpawnAction}.
	 * @return a {@code List} of {@code ToyPetSpawnAction} objects or an empty list if none exist.
	 */
	public List<ToyPetSpawnAction> getToyPetSpawnActions()
	{
		final List<ToyPetSpawnAction> result = new ArrayList<>();
		if (itemActions == null)
		{
			return result;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof ToyPetSpawnAction)
			{
				result.add((ToyPetSpawnAction) action);
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves the {@code EnchantItemAction} from the list of item actions.<br>
	 * It searches through all available actions to find a matching type.
	 * @return The {@code EnchantItemAction} if found, or {@code null} if it does not exist.
	 */
	public EnchantItemAction getEnchantAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof EnchantItemAction)
			{
				return (EnchantItemAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the house object action associated with this item.<br>
	 * It searches through the list of {@link AbstractItemAction} objects.<br>
	 * Returns {@code null} if no such action is found or if the actions list is empty.
	 * @return the {@code SummonHouseObjectAction} instance, or {@code null} if not found.
	 */
	public SummonHouseObjectAction getHouseObjectAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof SummonHouseObjectAction)
			{
				return (SummonHouseObjectAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code CraftLearnAction} from the list of item actions.<br>
	 * It searches through all available actions to find a matching instance.<br>
	 * Returns {@code null} if no such action exists or if the list is empty.
	 * @return The {@link CraftLearnAction} object, or {@code null} if not found.
	 */
	public CraftLearnAction getCraftLearnAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof CraftLearnAction)
			{
				return (CraftLearnAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link DecorateAction} from the list of item actions.<br>
	 * It searches for an instance of {@code DecorateAction}.<br>
	 * Returns {@code null} if no such action is found or if the list is empty.
	 * @return The {@code DecorateAction} object, or {@code null} if it does not exist.
	 */
	public DecorateAction getDecorateAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof DecorateAction)
			{
				return (DecorateAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link DyeAction} associated with this item.<br>
	 * It searches through all available actions to find a matching type.
	 * @return the {@code DyeAction} if found, or {@code null} if no such action exists.
	 */
	public DyeAction getDyeAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof DyeAction)
			{
				return (DyeAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link AdoptPetAction} from the list of available item actions.<br>
	 * This method searches for an action that is an instance of {@code AdoptPetAction}.<br>
	 * It returns {@code null} if no such action exists or if the action list is empty.
	 * @return The {@code AdoptPetAction} object, or {@code null} if not found.
	 */
	public AdoptPetAction getAdoptPetAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof AdoptPetAction)
			{
				return (AdoptPetAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code RemodelAction} from the list of item actions.<br>
	 * This method searches for an instance of {@link RemodelAction}.<br>
	 * It returns {@code null} if no such action is found or if the list is empty.
	 * @return the {@code RemodelAction} object, or {@code null} if it does not exist.
	 */
	public RemodelAction getRemodelAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof RemodelAction)
			{
				return (RemodelAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code PolishAction} from the list of item actions.<br>
	 * This method searches for an instance of {@link PolishAction}.<br>
	 * It returns {@code null} if no such action is found or if the list is empty.
	 * @return The {@code PolishAction} object, or {@code null} if it does not exist.
	 */
	public PolishAction getPolishAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof PolishAction)
			{
				return (PolishAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code TuningAction} associated with this item.<br>
	 * It searches through the list of available actions.<br>
	 * Returns {@code null} if no such action is found.
	 * @return the {@code TuningAction} object or {@code null}.
	 */
	public TuningAction getTuningAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof TuningAction)
			{
				return (TuningAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link AuthorizeAction} from the list of item actions.<br>
	 * It searches through all available actions to find a matching type.<br>
	 * Returns {@code null} if no such action exists or if the list is empty.
	 * @return the {@code AuthorizeAction} object, or {@code null} if not found.
	 */
	public AuthorizeAction getAuthorizeAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof AuthorizeAction)
			{
				return (AuthorizeAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link RemoveSoulBindAction} from the list of item actions.<br>
	 * It searches through all available actions to find a matching instance.
	 * @return The {@code RemoveSoulBindAction} if found, or {@code null} otherwise.
	 */
	public RemoveSoulBindAction getRemoveSoulBindAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof RemoveSoulBindAction)
			{
				return (RemoveSoulBindAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code ManastoneSlotExpansionAction} from the list of item actions.<br>
	 * This method searches for an action that matches the specific expansion type.<br>
	 * It returns {@code null} if no such action is found or if the action list is empty.
	 * @return The {@code ManastoneSlotExpansionAction} object, or {@code null} if not found.
	 */
	public ManastoneSlotExpansionAction getMagmaticExpandSlot()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof ManastoneSlotExpansionAction)
			{
				return (ManastoneSlotExpansionAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code GrindSlotExpansionAction} from the list of item actions.<br>
	 * This method searches through all available actions to find a matching expansion type.<br>
	 * It returns {@code null} if no such action exists or if the action list is empty.
	 * @return The {@code GrindSlotExpansionAction} object, or {@code null} if not found.
	 */
	public GrindSlotExpansionAction getGrindExpandSlot()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof GrindSlotExpansionAction)
			{
				return (GrindSlotExpansionAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link EnchantGrindingAction} from the list of item actions.<br>
	 * It searches through all available actions to find a matching type.<br>
	 * Returns {@code null} if no such action exists or if the action list is empty.
	 * @return The {@code EnchantGrindingAction} object, or {@code null} if not found.
	 */
	public EnchantGrindingAction getEnchantGrindAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof EnchantGrindingAction)
			{
				return (EnchantGrindingAction) action;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@code EnchantGlyphAction} from the list of item actions.<br>
	 * It searches through all available actions to find a matching instance.<br>
	 * Returns {@code null} if no such action exists or if the list is empty.
	 * @return the {@code EnchantGlyphAction} object, or {@code null} if not found.
	 */
	public EnchantGlyphAction getEnchantGlyphAction()
	{
		if (itemActions == null)
		{
			return null;
		}
		
		for (AbstractItemAction action : itemActions)
		{
			if (action instanceof EnchantGlyphAction)
			{
				return (EnchantGlyphAction) action;
			}
		}
		
		return null;
	}
}
