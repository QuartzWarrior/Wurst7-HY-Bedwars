/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.Items;
import net.wurstclient.WurstClient;

public final class FilterTeammatesSetting extends EntityFilterCheckbox
{
	private static final Minecraft MC = WurstClient.MC;
	
	public FilterTeammatesSetting(String description, boolean checked)
	{
		super("Filter teammates", description, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		if(!(e instanceof Player targetPlayer))
			return true;
		
		if(MC.player == null)
			return true;
			
		// Primary check: armor color
		Integer targetArmorColor = getArmorColor(targetPlayer);
		Integer playerArmorColor = getArmorColor(MC.player);
		
		if(targetArmorColor != null && playerArmorColor != null
			&& targetArmorColor.equals(playerArmorColor))
			return false;
			
		// Fallback: scoreboard team
		if(targetPlayer.getTeam() != null && MC.player.getTeam() != null
			&& targetPlayer.getTeam() == MC.player.getTeam())
			return false;

        // could do nametag but maybe ltr
		return true;
	}

	private Integer getArmorColor(Player player)
	{
		EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
			EquipmentSlot.LEGS, EquipmentSlot.FEET};
		
		for(EquipmentSlot slot : armorSlots)
		{
			ItemStack stack = player.getItemBySlot(slot);
			if(stack.isEmpty())
				continue;
			
			if(stack.is(Items.LEATHER_HELMET)
				|| stack.is(Items.LEATHER_CHESTPLATE)
				|| stack.is(Items.LEATHER_LEGGINGS)
				|| stack.is(Items.LEATHER_BOOTS))
			{
				DyedItemColor dyedColor = stack.get(
					net.minecraft.core.component.DataComponents.DYED_COLOR);
				if(dyedColor != null)
					return dyedColor.rgb();
			}
		}
		
		return null;
	}
	
	public static FilterTeammatesSetting genericCombat(boolean checked)
	{
		return new FilterTeammatesSetting(
			"Won't target teammates based on armor color (for Hypixel Bedwars/Skywars).",
			checked);
	}
}
