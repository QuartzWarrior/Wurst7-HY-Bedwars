/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.world.entity.Entity;

public final class FilterShopNPCsSetting extends EntityFilterCheckbox
{
	public FilterShopNPCsSetting(String description, boolean checked)
	{
		super("Filter shop NPCs", description, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// FILTER PLAYERS WITH NO ARMOR (npcs, won't work on all)
		if(e instanceof net.minecraft.world.entity.player.Player player)
		{
			boolean hasAnyArmor = false;
			net.minecraft.world.entity.EquipmentSlot[] armorSlots =
				{net.minecraft.world.entity.EquipmentSlot.HEAD,
					net.minecraft.world.entity.EquipmentSlot.CHEST,
					net.minecraft.world.entity.EquipmentSlot.LEGS,
					net.minecraft.world.entity.EquipmentSlot.FEET};
			
			for(net.minecraft.world.entity.EquipmentSlot slot : armorSlots)
			{
				if(!player.getItemBySlot(slot).isEmpty())
				{
					hasAnyArmor = true;
					break;
				}
			}
			
			// If player has no armor at all, it's likely a shop NPC
			if(!hasAnyArmor)
				return false;
		}
		
		// Also check for shop-related text in display name (secondary check)
		if(e.hasCustomName() && e.getCustomName() != null)
		{
			String name = e.getCustomName().getString().toUpperCase();
			
			// Common shop NPC indicators on Hypixel
			if(name.contains("RIGHT CLICK") || name.contains("CLICK")
				|| name.contains("SHOP") || name.contains("SHOPKEEPER")
				|| name.contains("UPGRADE") || name.contains("UPGRADES")
				|| name.contains("ITEM SHOP") || name.contains("ITEMS")
				|| name.contains("QUICK BUY"))
				return false;
		}
		
		return true;
	}
	
	public static FilterShopNPCsSetting genericCombat(boolean checked)
	{
		return new FilterShopNPCsSetting(
			"Won't target shop NPCs with 'RIGHT CLICK' in their name.",
			checked);
	}
}
