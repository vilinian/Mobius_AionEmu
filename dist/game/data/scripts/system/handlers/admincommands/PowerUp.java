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
package system.handlers.admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command for granting power-ups to players.<br>
 * This class processes requests to modify player status effects or attributes.
 * @author Tago
 */
public class PowerUp extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link PowerUp} command.<br>
	 * This constructor registers the {@code powerup} command with the system.
	 */
	public PowerUp()
	{
		super("powerup");
	}
	
	/**
	 * Multiplies a player's stats based on the administrator's stats and a given multiplier.<br>
	 * The target is determined by name, then target, then self.<br>
	 * A value of {@code 0} resets the player to normal stats.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the multiplier.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		// Rewrite the integer index variable to two.
		// int i = 0;
		// Player player = null;
		// if (params.length != 0) {
		// if ("help".startsWith(params[i])) {
		// PacketSendUtility.sendMessage(admin, "0 to return to normal state");
		// PacketSendUtility.sendMessage(admin, "//powerup <Multiplier = 2>");
		// This command multiplies your actual power by the given multiplier, where a value of 0 resets it to normal; parameters can be omitted, and the target priority is named player, then targeted player, then self, with a default multiplier of 2.
		// return;
		// }
		// player = World.getInstance().findPlayer(Util.convertName(params[i]));
		// if (player == null) {
		// VisibleObject target = admin.getTarget();
		// if (target instanceof Player)
		// player = (Player) target;
		// Set the player as an administrator.
		// }
		// Increment the counter by one.
		// try {
		// index = Integer.parseInt(params[i]);
		// }
		// catch (NumberFormatException ex) {
		// PacketSendUtility.sendMessage(admin, "Wrong input use //powerup help");
		// return;
		// }
		// catch (Exception ex2) {
		// PacketSendUtility.sendMessage(admin, "Occurs an error.");
		// return;
		// }
		// }
		// if (index == 0) {
		// player.getGameStats().recomputeStats();
		// player.getLifeStats().increaseHp(TYPE.HP, admin.getLifeStats().getMaxHp() + 1);
		// player.getLifeStats().increaseMp(TYPE.MP, admin.getLifeStats().getMaxMp() + 1);
		// PacketSendUtility.sendPacket(player, new SM_STATS_INFO(admin));
		// if (player == admin)
		// PacketSendUtility.sendMessage(player, "You are now normal again.");
		// else {
		// PacketSendUtility.sendMessage(admin, "Player " + player.getName() + " is now normal again.");
		// PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " made you normal again.");
		// }
		// return;
		// }
		// player.getGameStats().setStat(StatEnum.MAXMP, admin.getLifeStats().getMaxHp() * index);
		// player.getGameStats().setStat(StatEnum.MAXHP, admin.getLifeStats().getMaxMp() * index);
		//
		// player.getGameStats().setStat(StatEnum.BLOCK, admin.getGameStats().getStatBonus(StatEnum.BLOCK) * index);
		// player.getGameStats().setStat(StatEnum.EVASION, admin.getGameStats().getStatBonus(StatEnum.EVASION) * index);
		// player.getGameStats().setStat(StatEnum.HEALTH, admin.getGameStats().getStatBonus(StatEnum.HEALTH) * index);
		// player.getGameStats().setStat(StatEnum.ACCURACY, admin.getGameStats().getStatBonus(StatEnum.ACCURACY) * index);
		// player.getGameStats().setStat(StatEnum.PARRY, admin.getGameStats().getStatBonus(StatEnum.PARRY) * index);
		//
		// player.getGameStats().setStat(StatEnum.MAIN_HAND_ACCURACY,
		// admin.getGameStats().getStatBonus(StatEnum.MAIN_HAND_ACCURACY) * index);
		// player.getGameStats().setStat(StatEnum.MAIN_HAND_CRITICAL,
		// admin.getGameStats().getStatBonus(StatEnum.MAIN_HAND_CRITICAL) * index);
		// player.getGameStats().setStat(StatEnum.MAIN_HAND_POWER,
		// admin.getGameStats().getStatBonus(StatEnum.MAIN_HAND_POWER) * index);
		// player.getGameStats().setStat(StatEnum.MAIN_HAND_ATTACK_SPEED,
		// admin.getGameStats().getStatBonus(StatEnum.MAIN_HAND_POWER) * index);
		// player.getGameStats().setStat(StatEnum.OFF_HAND_ACCURACY,
		// admin.getGameStats().getStatBonus(StatEnum.OFF_HAND_ACCURACY) * index);
		// player.getGameStats().setStat(StatEnum.OFF_HAND_CRITICAL,
		// admin.getGameStats().getStatBonus(StatEnum.OFF_HAND_CRITICAL) * index);
		// player.getGameStats().setStat(StatEnum.OFF_HAND_POWER,
		// admin.getGameStats().getStatBonus(StatEnum.OFF_HAND_POWER) * index);
		// player.getGameStats().setStat(StatEnum.OFF_HAND_ATTACK_SPEED,
		// admin.getGameStats().getStatBonus(StatEnum.OFF_HAND_ATTACK_SPEED) * index);
		//
		// player.getGameStats().setStat(StatEnum.MAGICAL_ATTACK,
		// admin.getGameStats().getStatBonus(StatEnum.MAGICAL_ATTACK) * index);
		// player.getGameStats().setStat(StatEnum.MAGICAL_ACCURACY,
		// admin.getGameStats().getStatBonus(StatEnum.MAGICAL_ACCURACY) * index);
		// player.getGameStats().setStat(StatEnum.MAGICAL_CRITICAL,
		// admin.getGameStats().getStatBonus(StatEnum.MAGICAL_CRITICAL) * index);
		// player.getGameStats().setStat(StatEnum.MAGICAL_RESIST,
		// admin.getGameStats().getStatBonus(StatEnum.MAGICAL_RESIST) * index);
		// player.getGameStats().setStat(StatEnum.BOOST_MAGICAL_SKILL,
		// admin.getGameStats().getStatBonus(StatEnum.BOOST_MAGICAL_SKILL) * index * 15);
		//
		// player.getGameStats().setStat(StatEnum.REGEN_MP, admin.getGameStats().getStatBonus(StatEnum.REGEN_MP) * index);
		// player.getGameStats().setStat(StatEnum.REGEN_HP, admin.getGameStats().getStatBonus(StatEnum.REGEN_HP) * index);
		//
		// player.getLifeStats().increaseHp(TYPE.HP, admin.getLifeStats().getMaxHp() + 1);
		// player.getLifeStats().increaseMp(TYPE.MP, admin.getLifeStats().getMaxMp() + 1);
		// PacketSendUtility.sendPacket(player, new SM_STATS_INFO(admin));
		// if (player == admin)
		// PacketSendUtility.sendMessage(player, "You are now " + index + " times more powerfull than before.");
		// else {
		// Send a message to the admin stating that the player is now index times more powerful than before.
		// PacketSendUtility.sendMessage(player, "Admin " + admin.getName() + " made you " + index + " times more powerful than before.");.
		// }
	}
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		// TODO Auto-generated method stub
	}
}
