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
package com.aionemu.gameserver.model.autogroup;

import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;

/**
 * Defines the different types of automated groups available in the system.<br>
 * This enum is used to categorize and identify specific {@link AutoGroup} configurations.
 * @author xTz
 * @author GiGatR00n v4.7.5.x
 * @author Mariella (genAutoGroup.php), generiert am/um 18.03.2018 00:10
 */
public enum AutoGroupType
{
	BARANATH_DREDGION(1, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoDredgionInstance();
		}
	},
	CHANTRA_DREDGION(2, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoDredgionInstance();
		}
	},
	TERATH_DREDGION(3, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoDredgionInstance();
		}
	},
	FIRE_TEMPLE_1(4, 300000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	NOCHSANA_TRAINING_CAMP_1(5, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	DARK_POETA_1(6, 1200000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	STEEL_RAKE_1(7, 1200000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	UDAS_TEMPLE_1(8, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	LOWER_UDAS_TEMPLE_1(9, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	DRAUPNIR_CAVE_1(10, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	EMPYREAN_CRUCIBLE_1(11, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ADMA_STRONGHOLD_1(12, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	THEOBOMOS_LAB_1(13, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	FIRE_TEMPLE_2(14, 300000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ARENA_OF_CHAOS_1(21, 110000, 5, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_CHAOS_2(22, 110000, 8, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_CHAOS_3(23, 110000, 8, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_DISCIPLINE_1(24, 110000, 2, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_DISCIPLINE_2(25, 110000, 2, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_DISCIPLINE_3(26, 110000, 2, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	CHAOS_TRAINING_GROUNDS_1(27, 110000, 5, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	CHAOS_TRAINING_GROUNDS_2(28, 110000, 8, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	CHAOS_TRAINING_GROUNDS_3(29, 110000, 8, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	DISCIPLINE_TRAINING_GROUNDS_1(30, 110000, 2, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	DISCIPLINE_TRAINING_GROUNDS_2(31, 110000, 2, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	DISCIPLINE_TRAINING_GROUNDS_3(32, 110000, 2, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_COOPERATION_1(33, 110000, 3, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	ARENA_OF_COOPERATION_2(34, 110000, 3, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	ARENA_OF_COOPERATION_3(35, 110000, 3, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	ARENA_OF_GLORY_1(36, 600000, 4, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_GLORY_2(38, 110000, 4, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_CHAOS_4(39, 110000, 8, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_DISCIPLINE_4(40, 110000, 2, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_COOPERATION_4(41, 110000, 3, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	ARENA_OF_GLORY_3(42, 110000, 4, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	CHAOS_TRAINING_GROUNDS_4(43, 110000, 8, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	DISCIPLINE_TRAINING_GROUNDS_4(44, 110000, 2, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	TRAINING_CAMP_OF_COOPERATION_1(45, 110000, 3, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	TRAINING_ARENA_OF_UNITY_1(46, 600000, 2, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	ARENA_OF_DISCIPLINE_5(99, 600000, 2, 5)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	TRAINING_CAMP_OF_COOPERATION_2(101, 110000, 3, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	TRAINING_CAMP_OF_COOPERATION_3(102, 110000, 3, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	TRAINING_CAMP_OF_COOPERATION_4(103, 110000, 3, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	TRAINING_ARENA_OF_UNITY_2(104, 110000, 2, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	TRAINING_ARENA_OF_UNITY_3(105, 110000, 2, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	TRAINING_ARENA_OF_UNITY_4(106, 110000, 2, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	KAMAR_BATTLEFIELD(107, 600000, 12)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoKamarBattlefieldInstance();
		}
	},
	JORMUNGAND_MARCHING_ROUTE(108, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoJormungandInstance();
		}
	},
	STEEL_WALL_BASTION_BATTLEFIELD(109, 600000, 24)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoSteelWallBastionBattlefieldInstance();
		}
	},
	RUNATORIUM(111, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoRunatoriumInstance();
		}
	},
	TOURNAMENT_INSTANCE_LOBBY_1(112, 600000, 1, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ARENA_OF_CHAOS_5(113, 600000, 8, 5)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_DISCIPLINE_6(114, 600000, 2, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	ARENA_OF_COOPERATION_5(115, 600000, 3, 5)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	ARENA_OF_GLORY_4(116, 600000, 4, 4)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	CHAOS_TRAINING_GROUNDS_5(117, 600000, 8, 5)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	DISCIPLINE_TRAINING_GROUNDS_5(118, 600000, 2, 5)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPvPFFAInstance();
		}
	},
	TRAINING_CAMP_OF_COOPERATION_5(119, 600000, 3, 5)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	TRAINING_ARENA_OF_UNITY_5(120, 600000, 2, 5)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoHarmonyInstance();
		}
	},
	ASHUNATAL_DREDGION(121, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoDredgionInstance();
		}
	},
	BALAUR_MARCHING_ROUTE(122, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoBalaurMarchingRouteInstance();
		}
	},
	RUNATORIUM_RUINS(123, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoRunatoriumRuinsInstance();
		}
	},
	TOURNAMENT_INSTANCE_LOBBY_2(124, 600000, 1, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	GOLDEN_CRUCIBLE(125, 600000, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGoldenCrucibleInstance();
		}
	},
	GOLDEN_CRUCIBLE_GROUP_BATTLES_1(127, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	GOLDEN_CRUCIBLE_GROUP_BATTLES_2(128, 600000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	GOLDEN_CRUCIBLE_GROUP_BATTLE(129, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	GOLDEN_CRUCIBLE_GROUP_BATTLES_3(130, 600000, 2, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	TREASURE_ISLAND_OF_COURAGE(131, 600000, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ABYSSAL_SPLINTER(201, 600000, 12)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	TIAMAT_HIDEOUT_1(205, 600000, 12, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	SHATTERED_ABYSSAL_SPLINTER(206, 600000, 12)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	INSTANCE_TEST(301, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	FIRE_TEMPLE_3(302, 300000, 6, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	NOCHSANA_TRAINING_CAMP_2(303, 600000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	THEOBOMOS_LAB_2(305, 1200000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ADMA_STRONGHOLD_2(306, 1200000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	DRAUPNIR_CAVE_2(307, 1200000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	STEEL_RAKE_2(308, 1200000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	UDAS_TEMPLE_2(309, 600000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	LOWER_UDAS_TEMPLE_2(310, 600000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	EMPYREAN_CRUCIBLE_2(311, 600000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RENTUS_BASE(313, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	JORMUNGAND_BRIDGE(314, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	INDRATU_FORTRESS(315, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RUNADIUM(316, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	SAURO_WAR_DEPOT(317, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	AETHEROGENETICS_LAB(318, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	TIAMAT_HIDEOUT_2(322, 600000, 12, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ALQUIMIA_RESEARCH_CENTER(323, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	KATALAMIZE(324, 600000, 12)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RUKIBUKI_CIRCUS_1(330, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	BESHMUNDIR_TEMPLE_EASY(331, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	BESHMUNDIR_TEMPLE_DIFFICULT(332, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	TIAMAT_FORTRESS(333, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	AZOTURAN_FORTRESS(334, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	STEEL_WALL_BASTION(335, 600000, 24)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	DANUAR_SANCTUARY(336, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RUKIBUKI_CIRCUS_2(337, 600000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RUNE_SHIELD_TOWER(338, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	SHUGO_IMPERIAL_TOMB(339, 600000, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	JORMUNGAND_BRIDGE_BONUS(340, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RUNADIUM_BONUS(341, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RUNADIUM_HEROIC(345, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	RUNE_TRIBE_REFUGE(346, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	MAKARNA(347, 600000, 12)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	TRILLONERK_GOLD_VAULT_1(348, 600000, 3, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	LIBRARY_OF_KNOWLEDGE(350, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	GARDEN_OF_KNOWLEDGE(351, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	MUSEUM_OF_KNOWLEDGE(352, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	TRILLONERK_GOLD_VAULT_2(353, 600000, 3, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ADMA_RUINS(354, 600000, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ELEMENTAL_LORD_LABORATORY(355, 600000, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ARKHAL_HIDDEN_SPACE(356, 600000, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	KROBAN_BASE(357, 600000, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	ESOTERRACE(358, 600000, 6)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	NARAKKALLI(359, 600000, 12)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	PADMARASHKA_CAVE(360, 600000, 12)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	FIRE_TEMPLE_OF_MEMORY(409, 600000, 3)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	SANCTUM_BATTLEFIELD(416, 600000, 384)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoSanctumBattlefieldInstance();
		}
	},
	PANDAEMONIUM_BATTLEFIELD(417, 600000, 384)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoPandaemoniumBattlefieldInstance();
		}
	},
	HOLY_TOWER_1(419, 600000, 6, 1)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	},
	HOLY_TOWER_2(421, 600000, 6, 2)
	{
		@Override
		AutoInstance newAutoInstance()
		{
			return new AutoGeneralInstance();
		}
	};
	
	private final int instanceMaskId;
	private final int time;
	private final byte playerSize;
	private byte difficultId;
	private final AutoGroup template;
	
	/**
	 * Creates a new {@link AutoGroupType} with a specific difficulty level.<br>
	 * This constructor initializes the group using existing parameters and sets the {@code difficultId}.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param time The duration of the group in milliseconds.
	 * @param playerSize The maximum number of players allowed.
	 * @param difficultId The difficulty level assigned to this group type.
	 */
	private AutoGroupType(int instanceMaskId, int time, int playerSize, int difficultId)
	{
		this(instanceMaskId, time, playerSize);
		this.difficultId = (byte) difficultId;
	}
	
	/**
	 * Constructs a new {@link AutoGroupType} instance.<br>
	 * This constructor initializes the group properties and fetches the template from {@link DataManager}.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 * @param time The duration associated with this group type.
	 * @param playerSize The maximum number of players allowed in the group.
	 */
	private AutoGroupType(int instanceMaskId, int time, int playerSize)
	{
		this.instanceMaskId = instanceMaskId;
		this.time = time;
		this.playerSize = (byte) playerSize;
		template = DataManager.AUTO_GROUP.getTemplateByInstaceMaskId(this.instanceMaskId);
	}
	
	/**
	 * Retrieves the unique instance ID for this auto group type.<br>
	 * This value is obtained from the underlying template.
	 * @return the {@code int} instance ID of the template.
	 */
	public int getInstanceMapId()
	{
		return template.getInstanceId();
	}
	
	/**
	 * Retrieves the size of the group for this {@code AutoGroupType}.<br>
	 * This value represents how many players can join.
	 * @return The size as a {@code byte}.
	 */
	public byte getPlayerSize()
	{
		return playerSize;
	}
	
	/**
	 * Retrieves the unique identifier for this auto group instance.<br>
	 * This value is used to distinguish between different instances of the same type.
	 * @return The {@code int} mask ID associated with this {@link AutoGroupType}.
	 */
	public int getInstanceMaskId()
	{
		return instanceMaskId;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return template.getNameId();
	}
	
	/**
	 * Retrieves the unique identifier for the title associated with this group.<br>
	 * This value is fetched from the underlying {@code template}.
	 * @return The {@code int} ID of the title.
	 */
	public int getTittleId()
	{
		return template.getTitleId();
	}
	
	/**
	 * Retrieves the duration associated with this {@code AutoGroupType}.<br>
	 * The value is returned in milliseconds.
	 * @return the time value as an {@code int}
	 */
	public int getTime()
	{
		return time;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return template.getMinLvl();
	}
	
	/**
	 * Retrieves the maximum level from the current template.<br>
	 * It calls the {@code getMaxLvl()} method on the internal template object.
	 * @return The highest level value as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return template.getMaxLvl();
	}
	
	/**
	 * Checks if the group registration feature is enabled.<br>
	 * This method returns the value of the {@code registerGroup} attribute.
	 * @return {@code true} if the group registration is active, {@code false} otherwise.
	 */
	public boolean hasRegisterGroup()
	{
		return template.hasRegisterGroup();
	}
	
	/**
	 * Checks if the quick registration feature is enabled.<br>
	 * This method returns the value of the {@code registerQuick} attribute.
	 * @return {@code true} if quick registration is active, {@code false} otherwise.
	 */
	public boolean hasRegisterQuick()
	{
		return template.hasRegisterQuick();
	}
	
	/**
	 * Checks if the {@code registerNew} flag is enabled.<br>
	 * This determines if new registrations are allowed for this group.
	 * @return {@code true} if registration is enabled, {@code false} otherwise.
	 */
	public boolean hasRegisterNew()
	{
		return template.hasRegisterNew();
	}
	
	/**
	 * Checks if the current group type contains a specific NPC.<br>
	 * This method looks up the {@code npcId} within the associated template.
	 * @param npcId The unique identifier of the NPC to check.
	 * @return {@code true} if the ID is found, otherwise {@code false}.
	 */
	public boolean containNpcId(int npcId)
	{
		return template.getNpcIds().contains(npcId);
	}
	
	/**
	 * Retrieves the list of NPC identifiers for this group.<br>
	 * Returns an empty {@code List} if no IDs are defined.
	 * @return a {@code List<Integer>} containing the NPC IDs.
	 */
	public List<Integer> getNpcIds()
	{
		return template.getNpcIds();
	}
	
	/**
	 * Checks if the current group type is a Dredgion.<br>
	 * This method returns {@code true} for specific Dredgion types.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if it is a Dredgion, otherwise {@code false}.
	 */
	public boolean isDredgion()
	{
		// generiert
		switch (this)
		{
			case BARANATH_DREDGION:
			case CHANTRA_DREDGION:
			case TERATH_DREDGION:
			case ASHUNATAL_DREDGION:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Retrieves an {@link AutoGroupType} based on a specific ID.<br>
	 * This method searches through all available types to find a match.
	 * @param instanceMaskId The unique identifier for the group type.
	 * @return The matching {@code AutoGroupType} or {@code null} if no match is found.
	 */
	public static AutoGroupType getAGTByMaskId(int instanceMaskId)
	{
		for (AutoGroupType autoGroupsType : values())
		{
			if (autoGroupsType.getInstanceMaskId() == instanceMaskId)
			{
				return autoGroupsType;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the correct {@link AutoGroupType} based on level and NPC ID.<br>
	 * This method checks all available types to find a match.<br>
	 * It returns {@code null} if no matching group is found.
	 * @param level The character level to check for permission.
	 * @param npcId The unique identifier of the NPC.
	 * @return The matching {@link AutoGroupType} or {@code null}.
	 */
	public static AutoGroupType getAutoGroup(int level, int npcId)
	{
		for (AutoGroupType agt : values())
		{
			if (agt.hasLevelPermit(level) && agt.containNpcId(npcId))
			{
				return agt;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link AutoGroupType} based on a specific level and world ID.<br>
	 * This method checks if the group matches the provided {@code worldId}.<br>
	 * It also verifies if the player has the required level permission.
	 * @param level The character level to check for permissions.
	 * @param worldId The unique identifier of the map instance.
	 * @return The matching {@link AutoGroupType} or {@code null} if no match is found.
	 */
	public static AutoGroupType getAutoGroupByWorld(int level, int worldId)
	{
		for (AutoGroupType agt : values())
		{
			if ((agt.getInstanceMapId() == worldId) && agt.hasLevelPermit(level))
			{
				return agt;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link AutoGroupType} associated with a specific NPC ID.<br>
	 * This method searches through all available groups to find a match.<br>
	 * It returns {@code null} if no group contains the provided ID.
	 * @param npcId The unique identifier of the NPC to check.
	 * @return The matching {@link AutoGroupType} or {@code null}.
	 */
	public static AutoGroupType getAutoGroup(int npcId)
	{
		for (AutoGroupType agt : values())
		{
			if (agt.containNpcId(npcId))
			{
				return agt;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if the current group type is a PvP Solo Arena.<br>
	 * This method returns {@code true} for all types of {@code ARENA_OF_DISCIPLINE}.<br>
	 * It returns {@code false} for any other group types.
	 * @return {@code true} if the type is a solo arena, otherwise {@code false}
	 */
	public boolean isPvPSoloArena()
	{
		// generiert
		switch (this)
		{
			case ARENA_OF_DISCIPLINE_1:
			case ARENA_OF_DISCIPLINE_2:
			case ARENA_OF_DISCIPLINE_3:
			case ARENA_OF_DISCIPLINE_4:
			case ARENA_OF_DISCIPLINE_5:
			case ARENA_OF_DISCIPLINE_6:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a training PvP solo arena.<br>
	 * This method returns {@code true} for all types of discipline training grounds.<br>
	 * It returns {@code false} for any other group types.
	 * @return {@code true} if it is a training PvP solo arena, otherwise {@code false}.
	 */
	public boolean isTrainingPvPSoloArena()
	{
		// generiert
		switch (this)
		{
			case DISCIPLINE_TRAINING_GROUNDS_1:
			case DISCIPLINE_TRAINING_GROUNDS_2:
			case DISCIPLINE_TRAINING_GROUNDS_3:
			case DISCIPLINE_TRAINING_GROUNDS_4:
			case DISCIPLINE_TRAINING_GROUNDS_5:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type belongs to an Arena of Chaos.<br>
	 * This method returns {@code true} for any {@code ARENA_OF_CHAOS_1} through {@code ARENA_OF_CHAOS_5}.<br>
	 * It returns {@code false} for all other group types.
	 * @return {@code true} if the type is an Arena of Chaos, otherwise {@code false}.
	 */
	public boolean isPvPFFAArena()
	{
		// generiert
		switch (this)
		{
			case ARENA_OF_CHAOS_1:
			case ARENA_OF_CHAOS_2:
			case ARENA_OF_CHAOS_3:
			case ARENA_OF_CHAOS_4:
			case ARENA_OF_CHAOS_5:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a Chaos Training Ground.<br>
	 * This method identifies specific training areas used for chaos activities.
	 * @return {@code true} if it matches any of the five Chaos Training Grounds, otherwise {@code false}
	 */
	public boolean isTrainingPvPFFAArena()
	{
		// generiert
		switch (this)
		{
			case CHAOS_TRAINING_GROUNDS_1:
			case CHAOS_TRAINING_GROUNDS_2:
			case CHAOS_TRAINING_GROUNDS_3:
			case CHAOS_TRAINING_GROUNDS_4:
			case CHAOS_TRAINING_GROUNDS_5:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current {@code AutoGroupType} belongs to a training harmony arena.<br>
	 * This method returns {@code true} for specific unity and cooperation training areas.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if it is a training harmony arena, otherwise {@code false}.
	 */
	public boolean isTrainingHarmonyArena()
	{
		// generiert
		switch (this)
		{
			case TRAINING_CAMP_OF_COOPERATION_1:
			case TRAINING_CAMP_OF_COOPERATION_2:
			case TRAINING_CAMP_OF_COOPERATION_3:
			case TRAINING_CAMP_OF_COOPERATION_4:
			case TRAINING_CAMP_OF_COOPERATION_5:
			case TRAINING_ARENA_OF_UNITY_1:
			case TRAINING_ARENA_OF_UNITY_2:
			case TRAINING_ARENA_OF_UNITY_3:
			case TRAINING_ARENA_OF_UNITY_4:
			case TRAINING_ARENA_OF_UNITY_5:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current {@code AutoGroupType} belongs to a Harmony Arena.<br>
	 * This method returns {@code true} for all types of Cooperation Arenas.<br>
	 * It returns {@code false} for any other arena type.
	 * @return {@code true} if it is a Harmony Arena, otherwise {@code false}.
	 */
	public boolean isHarmonyArena()
	{
		// generiert
		switch (this)
		{
			case ARENA_OF_COOPERATION_1:
			case ARENA_OF_COOPERATION_2:
			case ARENA_OF_COOPERATION_3:
			case ARENA_OF_COOPERATION_4:
			case ARENA_OF_COOPERATION_5:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type belongs to a Glory Arena.<br>
	 * It returns {@code true} for any of the four Glory Arena types.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if this is a Glory Arena, {@code false} otherwise.
	 */
	public boolean isGloryArena()
	{
		// generiert
		switch (this)
		{
			case ARENA_OF_GLORY_1:
			case ARENA_OF_GLORY_2:
			case ARENA_OF_GLORY_3:
			case ARENA_OF_GLORY_4:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a Kamar battlefield.<br>
	 * This method returns {@code true} only for {@code KAMAR_BATTLEFIELD}.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if it is a Kamar battlefield, otherwise {@code false}
	 */
	public boolean isKamar()
	{
		// generiert
		switch (this)
		{
			case KAMAR_BATTLEFIELD:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a {@code JORMUNGAND_MARCHING_ROUTE}.<br>
	 * This method helps identify specific route types for auto-grouping.
	 * @return {@code true} if it matches the Jormungand route, otherwise {@code false}.
	 */
	public boolean isJormungand()
	{
		// generiert
		switch (this)
		{
			case JORMUNGAND_MARCHING_ROUTE:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a steel wall.<br>
	 * This method returns {@code true} only for {@code STEEL_WALL_BASTION}.<br>
	 * It returns {@code false} for all other types.
	 * @return {@code true} if it is a steel wall, otherwise {@code false}
	 */
	public boolean isSteelWall()
	{
		// generiert
		switch (this)
		{
			case STEEL_WALL_BASTION:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is {@code RUNATORIUM}.<br>
	 * This method returns {@code true} only for the Runatorium area.
	 * @return {@code true} if this is a Runatorium, otherwise {@code false}
	 */
	public boolean isRunatorium()
	{
		// generiert
		switch (this)
		{
			case RUNATORIUM:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is {@code RUNATORIUM_RUINS}.<br>
	 * This method returns {@code true} only for this specific location.
	 * @return {@code true} if it matches, otherwise {@code false}.
	 */
	public boolean isRunatoriumRuins()
	{
		// generiert
		switch (this)
		{
			case RUNATORIUM_RUINS:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a {@code SANCTUM_BATTLEFIELD}.<br>
	 * This method helps identify specific battlefield types for logic handling.
	 * @return {@code true} if this instance is {@code SANCTUM_BATTLEFIELD}, otherwise {@code false}.
	 */
	public boolean isSanctumBattlefield()
	{
		switch (this)
		{
			case SANCTUM_BATTLEFIELD:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a Pandaemonium Battlefield.<br>
	 * This method returns {@code true} only for the {@code PANDAEMONIUM_BATTLEFIELD} case.
	 * @return {@code true} if it is a Pandaemonium Battlefield, otherwise {@code false}.
	 */
	public boolean isPandaemoniumBattlefield()
	{
		switch (this)
		{
			case PANDAEMONIUM_BATTLEFIELD:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is set to {@code BALAUR_MARCHING_ROUTE}.<br>
	 * This helps identify specific marching behaviors.
	 * @return {@code true} if it matches the Balaur route, otherwise {@code false}.
	 */
	public boolean isBalaurMarching()
	{
		switch (this)
		{
			case BALAUR_MARCHING_ROUTE:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type is a Golden Crucible battle.<br>
	 * This method identifies specific combat instances for special logic.
	 * @return {@code true} if this instance is {@code GOLDEN_CRUCIBLE_GROUP_BATTLE}, otherwise {@code false}.
	 */
	public boolean isGoldenCrucible()
	{
		switch (this)
		{
			case GOLDEN_CRUCIBLE_GROUP_BATTLE:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current group type belongs to any PvP arena.<br>
	 * This method evaluates multiple arena types including training and solo modes.
	 * @return {@code true} if it is a PvP arena, {@code false} otherwise.
	 */
	public boolean isPvpArena()
	{
		return isTrainingPvPFFAArena() || isPvPFFAArena() || isTrainingPvPSoloArena() || isPvPSoloArena();
	}
	
	/**
	 * Checks if a specific level is allowed for this auto group.<br>
	 * It compares the provided {@code level} against the minimum and maximum limits.
	 * @param level The character level to check.
	 * @return {@code true} if the level is within the valid range, otherwise {@code false}.
	 */
	public boolean hasLevelPermit(int level)
	{
		return (level >= getMinLevel()) && (level <= getMaxLevel());
	}
	
	/**
	 * Retrieves the unique identifier for the difficulty level.<br>
	 * This value is used to distinguish between different difficulty settings.
	 * @return the {@code byte} value representing the difficulty ID.
	 */
	public byte getDifficultId()
	{
		return difficultId;
	}
	
	/**
	 * Retrieves a new instance of the {@code AutoInstance}.<br>
	 * This method calls {@code newAutoInstance} to create the object.
	 * @return A new {@code AutoInstance} object.
	 */
	public AutoInstance getAutoInstance()
	{
		return newAutoInstance();
	}
	
	abstract AutoInstance newAutoInstance();
}
