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
package com.aionemu.gameserver.model.stats.container;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.ride.RideInfo;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class represents the game statistics for a {@link Player}.<br>
 * It handles the calculation and storage of various player-specific attributes.<br>
 * It extends {@link CreatureGameStats} to provide specialized logic for human characters.
 * @author xavier
 */
public class PlayerGameStats extends CreatureGameStats<Player>
{
	private int cachedSpeed;
	private int cachedAttackSpeed;
	
	/**
	 * Creates a new {@link PlayerGameStats} instance for a specific player.<br>
	 * This constructor initializes the stats container using the provided {@code owner}.
	 * @param owner The {@link Player} who owns these game statistics.
	 */
	public PlayerGameStats(Player owner)
	{
		super(owner);
	}
	
	/**
	 * Updates the visual representation of stats and speed.<br>
	 * This method calls {@code updateStatsAndSpeedVisually}.
	 */
	@Override
	protected void onStatsChange()
	{
		super.onStatsChange();
		updateStatsAndSpeedVisually();
	}
	
	/**
	 * Updates the visual representation of player statistics.<br>
	 * This method also triggers a check for speed-related stats.<br>
	 * It calls {@code updateStatsVisually} and {@code checkSpeedStats}.
	 */
	public void updateStatsAndSpeedVisually()
	{
		updateStatsVisually();
		checkSpeedStats();
	}
	
	/**
	 * Updates the visual statistics for the player.<br>
	 * This method adds a broadcast mask to notify clients of changes.<br>
	 * It uses {@code BroadcastMode#UPDATE_STATS} to trigger the update.
	 */
	public void updateStatsVisually()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}
	
	/**
	 * Updates the cached movement speed for the player.<br>
	 * This method checks if the current speed has changed since the last update.<br>
	 * If a change is detected, it broadcasts an update to all relevant clients.
	 */
	private void checkSpeedStats()
	{
		final int current = getMovementSpeed().getCurrent();
		final int currentAttackSpeed = getAttackSpeed().getCurrent();
		if ((current != cachedSpeed) || (currentAttackSpeed != cachedAttackSpeed))
		{
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		
		cachedSpeed = current;
		cachedAttackSpeed = currentAttackSpeed;
	}
	
	/**
	 * Retrieves the maximum health point value for the player.<br>
	 * This method fetches the stat from the {@code PlayerStatsTemplate}.
	 * @return The {@code Stat2} object representing the maximum HP.
	 */
	@Override
	public Stat2 getMaxHp()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXHP, pst.getMaxHp());
	}
	
	/**
	 * Retrieves the maximum mana points for the player.<br>
	 * This method calculates the value based on the current stats template.
	 * @return The {@code Stat2} object containing the maximum MP.
	 */
	@Override
	public Stat2 getMaxMp()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXMP, pst.getMaxMp());
	}
	
	/**
	 * Retrieves the physical critical resistance stat.<br>
	 * This value determines how well an entity resists critical hits.
	 * @return The {@code Stat2} object containing the strike resistance value.
	 */
	@Override
	public Stat2 getStrikeResist()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, pst.getStrikeResist());
	}
	
	/**
	 * Retrieves the physical critical damage reduction stat.<br>
	 * This value represents how much damage from critical hits is reduced.
	 * @return the {@code Stat2} object containing the physical critical damage reduction.
	 */
	@Override
	public Stat2 getStrikeFort()
	{
		return getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0);
	}
	
	/**
	 * Retrieves the magical critical resistance stat.<br>
	 * This method returns a {@link Stat2} object representing the value.
	 * @return The current magical critical resistance as a {@code Stat2}.
	 */
	@Override
	public Stat2 getSpellResist()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAGICAL_CRITICAL_RESIST, pst.getSpellResist());
	}
	
	/**
	 * Retrieves the magical critical damage reduction stat.<br>
	 * This value is used to determine how much magic critical hits are reduced.
	 * @return the {@code Stat2} object representing the spell fort.
	 */
	@Override
	public Stat2 getSpellFort()
	{
		return getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0);
	}
	
	/**
	 * Retrieves the maximum Defense Points for the player.<br>
	 * This method returns a {@link Stat2} object representing the value.<br>
	 * It uses a default value of {@code 4000} if no specific stat is found.
	 * @return The {@link Stat2} object containing the maximum defense points.
	 */
	public Stat2 getMaxDp()
	{
		return getStat(StatEnum.MAXDP, 4000);
	}
	
	/**
	 * Retrieves the current fly time for the player.<br>
	 * This value is calculated based on the {@code StatEnum.FLY_TIME} stat.<br>
	 * It uses {@code BASE_FLYTIME} as the default base value.
	 * @return The {@code Stat2} object representing the fly time.
	 */
	public Stat2 getFlyTime()
	{
		return getStat(StatEnum.FLY_TIME, CustomConfig.BASE_FLYTIME);
	}
	
	/**
	 * Retrieves the total speed statistic for the NPC.<br>
	 * This method calculates the {@code ALLSPEED} stat using a base value of {@code 7500}.
	 * @return The {@link Stat2} object containing the all speed value.
	 */
	@Override
	public Stat2 getAllSpeed()
	{
		final int base = 7500; // TODO current value
		return getStat(StatEnum.ALLSPEED, base);
	}
	
	/**
	 * Retrieves the current attack speed of the character.<br>
	 * This method calculates the value based on the {@code StatEnum.ATTACK_SPEED} and the weapon stats.
	 * @return The {@code Stat2} object containing the attack speed.
	 */
	@Override
	public Stat2 getAttackSpeed()
	{
		int base = 1500;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		
		if (mainHandWeapon != null)
		{
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackSpeed();
			final Item offWeapon = owner.getEquipment().getOffHandWeapon();
			if (offWeapon != null)
			{
				base += offWeapon.getItemTemplate().getWeaponStats().getAttackSpeed() / 4;
			}
		}
		
		final Stat2 aSpeed = getStat(StatEnum.ATTACK_SPEED, base);
		return aSpeed;
	}
	
	/**
	 * Retrieves the casting time stat for the player.<br>
	 * This method returns a {@code Stat2} object representing the boost to casting speed.<br>
	 * It uses a default value of {@code 800} for specific classes like Sorcerer, Spirit-Master, or Songweaver.
	 * @return The {@code Stat2} object containing the casting time information.
	 */
	@Override
	public Stat2 getBCastingTime()
	{
		int base = 0;
		final int casterClass = owner.getPlayerClass().getClassId();
		if ((casterClass == 7) || // Sorcerer.
			(casterClass == 8) || // Spirit-Master.
			(casterClass == 16))
		{
			// Songweaver.
			base = 800;
		}
		
		return getStat(StatEnum.BOOST_CASTING_TIME, base);
	}
	
	/**
	 * Retrieves the concentration statistic for this player.<br>
	 * This method calculates a base value based on the player's class and level.<br>
	 * It then calls {@code int)} to fetch the final value.
	 * @return the {@code Stat2} object representing the concentration stat.
	 */
	@Override
	public Stat2 getConcentration()
	{
		int base = 0;
		final int sorcerer1 = owner.getPlayerClass().getClassId();
		final int spiritMaster1 = owner.getPlayerClass().getClassId();
		if (sorcerer1 == 7)
		{
			base = 25;
		}
		else if ((spiritMaster1 == 8) && (owner.getLevel() >= 56))
		{
			base = 100;
		}
		
		return getStat(StatEnum.CONCENTRATION, base);
	}
	
	/**
	 * Retrieves the root resistance stat for this player.<br>
	 * This value determines how well the entity resists being rooted.
	 * @return a {@code Stat2} object containing the root resistance value.
	 */
	@Override
	public Stat2 getRootResistance()
	{
		int base = 0;
		final int aethertech2 = owner.getPlayerClass().getClassId();
		if (aethertech2 == 13)
		{
			base = 200;
		}
		
		return getStat(StatEnum.ROOT_RESISTANCE, base);
	}
	
	/**
	 * Retrieves the snare resistance value for the player.<br>
	 * This method returns a {@link Stat2} object representing the current resistance.
	 * @return The {@code Stat2} value of the snare resistance.
	 */
	@Override
	public Stat2 getSnareResistance()
	{
		int base = 0;
		final int aethertech3 = owner.getPlayerClass().getClassId();
		if (aethertech3 == 13)
		{
			base = 200;
		}
		
		return getStat(StatEnum.SNARE_RESISTANCE, base);
	}
	
	/**
	 * Retrieves the bind resistance stat for this player.<br>
	 * This value is used to determine how well the entity resists being bound.
	 * @return the {@code Stat2} object containing the bind resistance value.
	 */
	@Override
	public Stat2 getBindResistance()
	{
		int base = 0;
		final int aethertech4 = owner.getPlayerClass().getClassId();
		if (aethertech4 == 13)
		{
			base = 200;
		}
		
		return getStat(StatEnum.BIND_RESISTANCE, base);
	}
	
	/**
	 * Retrieves the fear resistance stat for the player.<br>
	 * This method returns a {@link Stat2} object representing the current value.<br>
	 * It uses the {@code FEAR_RESISTANCE} enum to fetch the data.
	 * @return The {@code Stat2} object containing the fear resistance value.
	 */
	@Override
	public Stat2 getFearResistance()
	{
		int base = 0;
		final int aethertech5 = owner.getPlayerClass().getClassId();
		if (aethertech5 == 13)
		{
			base = -200;
		}
		
		return getStat(StatEnum.FEAR_RESISTANCE, base);
	}
	
	/**
	 * Retrieves the sleep resistance stat for the player.<br>
	 * This method returns a {@code Stat2} object representing the value.<br>
	 * It uses the {@code StatEnum.SLEEP_RESISTANCE} enum to fetch the data.
	 * @return The {@code Stat2} object containing the sleep resistance value.
	 */
	@Override
	public Stat2 getSleepResistance()
	{
		int base = 0;
		final int aethertech6 = owner.getPlayerClass().getClassId();
		if (aethertech6 == 13)
		{
			base = -200;
		}
		
		return getStat(StatEnum.SLEEP_RESISTANCE, base);
	}
	
	/**
	 * Retrieves the physical defense statistic for this player.<br>
	 * This method calculates the value based on specific class IDs.
	 * @return the {@code Stat2} object representing physical defense.
	 */
	@Override
	public Stat2 getPDef()
	{
		int base = 0;
		final int gunslinger = owner.getPlayerClass().getClassId();
		final int aethertech = owner.getPlayerClass().getClassId();
		if (gunslinger == 14)
		{
			base = 100;
		}
		else if (aethertech == 13)
		{
			base = 350;
		}
		
		return getStat(StatEnum.PHYSICAL_DEFENSE, base);
	}
	
	/**
	 * Retrieves the magical resistance stat for this player.<br>
	 * It calculates a default value based on the owner level if not already set.
	 * @return The {@code Stat2} object representing the magical resistance.
	 */
	@Override
	public Stat2 getMResist()
	{
		int base = 0;
		final int assassin = owner.getPlayerClass().getClassId();
		if ((assassin == 4) && (owner.getLevel() >= 37))
		{
			base = 30;
		}
		
		return getStat(StatEnum.MAGICAL_RESIST, base);
	}
	
	/**
	 * Retrieves the Magic Skill Boost Resistance stat.<br>
	 * This value is used to determine how much magic skill boosts are resisted.
	 * @return the {@code Stat2} object containing the resistance value.
	 */
	@Override
	public Stat2 getMBResist()
	{
		int base = 0;
		final int cleric = owner.getPlayerClass().getClassId();
		final int sorcerer2 = owner.getPlayerClass().getClassId();
		final int spiritMaster2 = owner.getPlayerClass().getClassId();
		if ((cleric == 10) && (owner.getLevel() >= 60))
		{
			base = 140;
		}
		
		if ((sorcerer2 == 7) && (owner.getLevel() >= 60))
		{
			base = 180;
		}
		
		if ((spiritMaster2 == 8) && (owner.getLevel() >= 60))
		{
			base = 180;
		}
		
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
	}
	
	/**
	 * Retrieves the current movement speed of the player.<br>
	 * This value changes based on the owner's state such as flying, walking, or riding.<br>
	 * The result is calculated dynamically based on the player's class, level, and current status.
	 * @return the {@code Stat2} object representing the calculated movement speed.
	 */
	@Override
	public Stat2 getMovementSpeed()
	{
		Stat2 movementSpeed;
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		if (owner.isInPlayerMode(PlayerMode.RIDE))
		{
			final RideInfo ride = owner.ride;
			final int runSpeed = (int) pst.getRunSpeed() * 1000;
			if (owner.isInState(CreatureState.FLYING))
			{
				movementSpeed = new AdditionStat(StatEnum.FLY_SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int) (ride.getFlySpeed() * 1000) - runSpeed);
			}
			else
			{
				final float speed = owner.isInSprintMode() ? ride.getSprintSpeed() : ride.getMoveSpeed();
				movementSpeed = new AdditionStat(StatEnum.SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int) (speed * 1000) - runSpeed);
			}
		}
		else if (owner.isInFlyingState())
		{
			movementSpeed = getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000));
		}
		else if (owner.isInState(CreatureState.FLIGHT_TELEPORT) && !owner.isInState(CreatureState.RESTING))
		{
			movementSpeed = getStat(StatEnum.SPEED, 12000);
		}
		else if (owner.isInState(CreatureState.WALKING))
		{
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000));
		}
		else if (getAllSpeed().getBonus() != 0)
		{
			movementSpeed = getStat(StatEnum.SPEED, getAllSpeed().getCurrent());
		}
		else
		{
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getRunSpeed() * 1000));
		}
		
		return movementSpeed;
	}
	
	/**
	 * Retrieves the current attack range for the player.<br>
	 * This method calculates the value based on the equipped weapons and their templates.
	 * @return the {@code Stat2} object containing the calculated attack range.
	 */
	@Override
	public Stat2 getAttackRange()
	{
		int base = 1500;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if (mainHandWeapon != null)
		{
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
			if (!mainHandWeapon.getItemTemplate().isTwoHandWeapon() && (offHandWeapon != null) && (offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD))
			{
				if (mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange() != offHandWeapon.getItemTemplate().getWeaponStats().getAttackRange())
				{
					if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H))
					{
						base = 1500;
					}
					else if ((mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) && (offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H))
					{
						base = 1500;
					}
					else if (offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD)
					{
						base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
						log.info("[Error] PlayerGameStats] mainHandWeapon [" + mainHandWeapon.getItemTemplate().getItemType() + "] offHandWeapon [" + offHandWeapon.getItemTemplate().getItemType() + "]");
					}
				}
			}
		}
		
		return getStat(StatEnum.ATTACK_RANGE, base);
	}
	
	/**
	 * Retrieves the magical defense statistic.<br>
	 * This method returns a {@link Stat2} object representing the value of {@code MAGICAL_DEFEND}.
	 * @return The magical defense stat as a {@code Stat2} object.
	 */
	@Override
	public Stat2 getMDef()
	{
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}
	
	/**
	 * Retrieves the power statistic for the player.<br>
	 * This method calls {@code int)} using the {@code POWER} enum.
	 * @return The {@code Stat2} object representing the current power value.
	 */
	@Override
	public Stat2 getPower()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.POWER, pst.getPower());
	}
	
	/**
	 * Retrieves the current health of the player.<br>
	 * This method returns a {@link Stat2} object representing the health value.<br>
	 * It uses a default value of {@code 100} if no specific stat is found.
	 * @return The health statistic as a {@code Stat2} object.
	 */
	@Override
	public Stat2 getHealth()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.HEALTH, pst.getHealth());
	}
	
	/**
	 * Retrieves the accuracy statistic for this player.<br>
	 * It returns a {@link Stat2} object representing the current value.
	 * @return The {@code Stat2} object containing the accuracy value.
	 */
	@Override
	public Stat2 getAccuracy()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.ACCURACY, pst.getAccuracy());
	}
	
	/**
	 * Retrieves the agility statistic for this player.<br>
	 * It fetches the value from the {@link PlayerStatsTemplate} based on the player's class and level.
	 * @return The {@link Stat2} object representing the agility stat.
	 */
	@Override
	public Stat2 getAgility()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.AGILITY, pst.getAgility());
	}
	
	/**
	 * Retrieves the knowledge statistic for the player.<br>
	 * This method calls {@code int)} using the {@code StatEnum.KNOWLEDGE} type.
	 * @return the {@code Stat2} object representing the current knowledge value.
	 */
	@Override
	public Stat2 getKnowledge()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.KNOWLEDGE, pst.getKnowledge());
	}
	
	/**
	 * Retrieves the {@code Will} statistic for this player.<br>
	 * It returns a {@link Stat2} object representing the value.
	 * @return The {@code Will} stat as a {@code Stat2} object.
	 */
	@Override
	public Stat2 getWill()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.WILL, pst.getWill());
	}
	
	/**
	 * Retrieves the current evasion statistic for the player.<br>
	 * This method fetches the base evasion from the {@code PlayerStatsTemplate}.<br>
	 * It returns a {@code Stat2} object representing the final calculated value.
	 * @return The {@code Stat2} object containing the evasion value.
	 */
	@Override
	public Stat2 getEvasion()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.EVASION, pst.getEvasion());
	}
	
	/**
	 * Retrieves the parry statistic for the player.<br>
	 * This method calculates the total parry value based on the player class and level.<br>
	 * It also adds any parry bonus from the equipped main hand weapon.
	 * @return The {@code Stat2} object containing the calculated parry stat.
	 */
	@Override
	public Stat2 getParry()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getParry();
		final Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getParry();
		}
		
		return getStat(StatEnum.PARRY, base);
	}
	
	/**
	 * Retrieves the block stat for this player.<br>
	 * This method calls {@code int)} to fetch the value.
	 * @return the {@code Stat2} object representing the block value.
	 */
	@Override
	public Stat2 getBlock()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.BLOCK, pst.getBlock());
	}
	
	/**
	 * Retrieves the physical attack value for the main hand weapon.<br>
	 * This method fetches the stat from the {@link Npc} template.
	 * @return The {@code Stat2} object representing the main hand physical attack.
	 */
	@Override
	public Stat2 getMainHandPAttack()
	{
		int base = 0;
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			if (mainHandWeapon.getItemTemplate().getAttackType().isMagical())
			{
				return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, owner);
			}
			
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		else
		{
			base = pst.getMainHandAttack();
		}
		
		final Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
		return getStat(StatEnum.MAIN_HAND_POWER, stat);
	}
	
	/**
	 * Retrieves the off-hand power statistic for the player.<br>
	 * This value is calculated based on the mean damage of the equipped off-hand weapon.<br>
	 * If no valid off-hand weapon is equipped, it returns a value of 0.
	 * @return The {@code Stat2} object representing the off-hand power.
	 */
	public Stat2 getOffHandPAttack()
	{
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.98;
			final Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		
		return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, owner);
	}
	
	/**
	 * Retrieves the physical critical hit stat for the main hand.<br>
	 * This method fetches the value associated with {@code StatEnum.PHYSICAL_CRITICAL}.<br>
	 * It uses a default index of {@code 10}.
	 * @return The {@link Stat2} object containing the physical critical stat.
	 */
	@Override
	public Stat2 getMainHandPCritical()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandCritRate();
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
		}
		
		return getStat(StatEnum.PHYSICAL_CRITICAL, base);
	}
	
	/**
	 * Retrieves the physical critical stat from the off-hand weapon.<br>
	 * If no off-hand weapon is equipped, it returns a default value of {@code 0}.<br>
	 * This method checks if the item in the off-hand slot is a valid weapon.
	 * @return The {@link Stat2} object representing the off-hand critical stat.
	 */
	public Stat2 getOffHandPCritical()
	{
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			final int base = offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
			return getStat(StatEnum.PHYSICAL_CRITICAL, base);
		}
		
		return new AdditionStat(StatEnum.OFF_HAND_CRITICAL, 0, owner);
	}
	
	/**
	 * Retrieves the physical accuracy stat for the main hand.<br>
	 * This method triggers a calculation if {@code pAccuracy} is 0.
	 * @return The {@link Stat2} value of the physical accuracy.
	 */
	@Override
	public Stat2 getMainHandPAccuracy()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAccuracy();
		final Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
		}
		
		return getStat(StatEnum.PHYSICAL_ACCURACY, base);
	}
	
	/**
	 * Retrieves the physical accuracy value for the off-hand weapon.<br>
	 * This method checks if the player is holding a valid weapon in their off-hand slot.<br>
	 * If a weapon exists, it calculates the total accuracy based on class data and item stats.<br>
	 * If no weapon is equipped, it returns a default value of 0.
	 * @return the {@code Stat2} object representing the off-hand physical accuracy.
	 */
	public Stat2 getOffHandPAccuracy()
	{
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
			int base = pst.getMainHandAccuracy();
			base += offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
			return getStat(StatEnum.PHYSICAL_ACCURACY, base);
		}
		
		return new AdditionStat(StatEnum.OFF_HAND_ACCURACY, 0, owner);
	}
	
	/**
	 * Retrieves the magical attack statistic for this player.<br>
	 * It calculates the value based on the equipped main hand weapon.
	 * @return The {@code Stat2} object representing the magical attack.
	 */
	@Override
	public Stat2 getMAttack()
	{
		int base = 0;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical())
			{
				return new AdditionStat(StatEnum.MAGICAL_ATTACK, 0, owner);
			}
			
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		
		return getStat(StatEnum.MAGICAL_ATTACK, base);
	}
	
	/**
	 * Retrieves the main hand magical attack stat for this player.<br>
	 * It checks if the equipped weapon is a magical type.<br>
	 * The result is returned as a {@code Stat2} object.
	 * @return The calculated {@code Stat2} for main hand magical power.
	 */
	@Override
	public Stat2 getMainHandMAttack()
	{
		int base = 0;
		final Equipment equipment = owner.getEquipment();
		final Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical())
			{
				return new AdditionStat(StatEnum.MAIN_HAND_MAGICAL_POWER, 0, owner);
			}
			
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		
		final Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
		return getStat(StatEnum.MAIN_HAND_MAGICAL_POWER, stat);
	}
	
	/**
	 * Retrieves the magical attack stat for the off-hand weapon.<br>
	 * This value is based on the power from the {@link Npc} template.
	 * @return the {@code Stat2} object containing the off-hand magical attack.
	 */
	@Override
	public Stat2 getOffHandMAttack()
	{
		int base = 0;
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon())
		{
			base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.82;
			final Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_MAGICAL_POWER, stat);
		}
		
		return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_POWER, 0, owner);
	}
	
	/**
	 * Retrieves the magical skill boost statistic.<br>
	 * This method returns a {@code Stat2} object representing the boost value.<br>
	 * It uses a base value of {@code 100} for the calculation.
	 * @return The {@code Stat2} object containing the magical skill boost.
	 */
	@Override
	public Stat2 getMBoost()
	{
		int base = 0;
		final Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getBoostMagicalSkill();
		}
		
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, base);
	}
	
	/**
	 * Retrieves the physical PvP attack ratio for the player.<br>
	 * This method calculates the value based on a base of {@code 0}.
	 * @return a {@link Stat2} object representing the PvP attack ratio.
	 */
	@Override
	public Stat2 getPvpAttack()
	{
		final int base = 0;
		return getStat(StatEnum.PVP_ATTACK_RATIO_PHYSICAL, base);
	}
	
	/**
	 * Retrieves the PvP defense statistic.<br>
	 * This method returns the physical PvP defense ratio.
	 * @return the {@code Stat2} object representing PvP defense.
	 */
	@Override
	public Stat2 getPvpDeff()
	{
		final int base = 0;
		return getStat(StatEnum.PVP_DEFEND_RATIO_PHYSICAL, base);
	}
	
	// new 7.x
	/**
	 * Retrieves the PVP attack statistic for the player.<br>
	 * This method calls {@code int)} to fetch the value.
	 * @return The {@code Stat2} object representing the PVP attack stat.
	 */
	@Override
	public Stat2 getPVPAttack()
	{
		final int base = 0;
		return getStat(StatEnum.PVP_ATTACK, base);
	}
	
	/**
	 * Retrieves the PVP Defense statistic for the player.<br>
	 * This method returns a {@link Stat2} object representing the defense value.
	 * @return The {@code Stat2} value of the PVP Defense.
	 */
	@Override
	public Stat2 getPVPDefense()
	{
		final int base = 0;
		return getStat(StatEnum.PVP_DEFENSE, base);
	}
	
	/**
	 * Retrieves the PVE attack statistic for this NPC.<br>
	 * This method calls {@code int)} using the {@code PVE_ATTACK} enum.
	 * @return The {@code Stat2} object representing the PVE attack value.
	 */
	@Override
	public Stat2 getPVEAttack()
	{
		final int base = 0;
		return getStat(StatEnum.PVE_ATTACK, base);
	}
	
	/**
	 * Retrieves the PVE Defense stat for this NPC.<br>
	 * This method returns a {@link Stat2} object representing the defense value.
	 * @return The {@code Stat2} value of the PVE Defense.
	 */
	@Override
	public Stat2 getPVEDefense()
	{
		final int base = 0;
		return getStat(StatEnum.PVE_DEFENSE, base);
	}
	
	/*
	 * @Override public Stat2 getMainHandMAccuracy() { PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel()); int base = pst.getMagicAccuracy(); Item mainHandWeapon = owner.getEquipment().getMainHandWeapon(); if (mainHandWeapon != null) { base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy(); } return getStat(StatEnum.MAGICAL_ACCURACY, base); }
	 */
	
	/**
	 * Retrieves the magical accuracy of the player.<br>
	 * This method calculates the value based on the character class and level.<br>
	 * It also adds any magical accuracy from the main hand weapon if equipped.
	 * @return The {@code Stat2} object representing the magical accuracy.
	 */
	@Override
	public Stat2 getMAccuracy()
	{
		final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMagicAccuracy();
		final Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null)
		{
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
		}
		
		return getStat(StatEnum.MAGICAL_ACCURACY, base);
	}
	
	/**
	 * Retrieves the magical accuracy value from the off-hand weapon.<br>
	 * This method checks if the player is holding a valid one-handed weapon in their off-hand slot.<br>
	 * If no such weapon exists, it returns a default value of 0.
	 * @return the {@code Stat2} object containing the calculated magical accuracy.
	 */
	public Stat2 getOffHandMAccuracy()
	{
		// TODO Check if there should more added current is only Bonus from offHand(find formula)
		final Equipment equipment = owner.getEquipment();
		final Item offHandWeapon = equipment.getOffHandWeapon();
		if ((offHandWeapon != null) && offHandWeapon.getItemTemplate().isWeapon() && !offHandWeapon.getItemTemplate().isTwoHandWeapon())
		{
			final PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
			int base = pst.getMagicAccuracy();
			base += offHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
			return getStat(StatEnum.MAGICAL_ACCURACY, base);
		}
		
		return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_ACCURACY, 0, owner);
	}
	
	/**
	 * Retrieves the magical critical hit statistic.<br>
	 * This method calls {@code int)} using the {@code MAGICAL_CRITICAL} enum.<br>
	 * It provides a default value of {@code 50} if the stat is not found.
	 * @return The {@code Stat2} object representing magical critical hit.
	 */
	@Override
	public Stat2 getMCritical()
	{
		return getStat(StatEnum.MAGICAL_CRITICAL, 50); // TODO CHECK
	}
	
	// TODO OFF HAND MAGIC CRIT
	
	/**
	 * Retrieves the current health regeneration rate for the player.<br>
	 * This value depends on the player level and their current state.<br>
	 * It is also scaled by the percentage of current health.
	 * @return a {@code Stat2} object representing the regeneration rate.
	 */
	@Override
	public Stat2 getHpRegenRate()
	{
		int base = owner.getLevel() + 3;
		if (owner.isInState(CreatureState.RESTING))
		{
			base *= 8;
		}
		
		base *= getHealth().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_HP, base);
	}
	
	/**
	 * Retrieves the current MP regeneration rate for the player.<br>
	 * The calculation considers the player level and {@code RESTING}.<br>
	 * It also factors in the current value of the Will stat.
	 * @return a {@code Stat2} object representing the calculated MP regeneration rate.
	 */
	@Override
	public Stat2 getMpRegenRate()
	{
		int base = owner.getLevel() + 8;
		if (owner.isInState(CreatureState.RESTING))
		{
			base *= 8;
		}
		
		base *= getWill().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_MP, base);
	}
	
	/**
	 * Sends the current statistics information to the player.<br>
	 * This method uses {@code sendPacket} to transmit a {@code SM_STATS_INFO} packet.<br>
	 * It informs the client of the owner's updated stats.
	 */
	@Override
	public void updateStatInfo()
	{
		PacketSendUtility.sendPacket(owner, new SM_STATS_INFO(owner));
	}
	
	/**
	 * Updates the movement speed information for the creature.<br>
	 * This method refreshes the internal values used by {@code getMovementSpeedFloat}.
	 */
	@Override
	public void updateSpeedInfo()
	{
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0), true);
	}
}
