/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.clickgui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.Component;
import net.wurstclient.hacks.PlayerInfoHack;
import net.wurstclient.util.RenderUtils;

public final class PlayerInfoComponent extends Component
{
	private final PlayerInfoHack hack;
	private static final int ITEM_SIZE = 16;
	private static final int PADDING = 2;
	private static final int LINE_HEIGHT = 18;
	
	public PlayerInfoComponent(PlayerInfoHack hack)
	{
		this.hack = hack;
		setWidth(getDefaultWidth());
		setHeight(getDefaultHeight());
	}
	
	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY,
		float partialTicks)
	{
		ClickGui gui = WURST.getGui();
		
		int x1 = getX();
		int y1 = getY();
		int x2 = x1 + getWidth();
		int y2 = y1 + getHeight();
		
		// tooltip
		if(isHovering(mouseX, mouseY))
			gui.setTooltip("");
		
		// bg
		context.fill(x1, y1, x2, y2,
			RenderUtils.toIntColor(gui.getBgColor(), gui.getOpacity()));
		
		Player target = hack.getTargetPlayer();
		
		if(target == null)
		{
			// Show message when no target
			String msg = "Look at a player";
			int textWidth = MC.font.width(msg);
			context.drawString(MC.font, msg, x1 + (getWidth() - textWidth) / 2,
				y1 + PADDING, 0xAAAAAA, false);
			return;
		}
		
		int currentY = y1 + PADDING;
		
		// each slot
		EquipmentSlot[] slots = {EquipmentSlot.MAINHAND, EquipmentSlot.HEAD,
			EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		
		for(EquipmentSlot slot : slots)
		{
			ItemStack stack = target.getItemBySlot(slot);
			if(stack.isEmpty())
				continue;
			
			// item icon
			context.renderItem(stack, x1 + PADDING, currentY);
			
			currentY += LINE_HEIGHT;
		}
	}
	
	@Override
	public int getDefaultWidth()
	{
		return ITEM_SIZE + (PADDING * 2);
	}
	
	@Override
	public int getDefaultHeight()
	{
		return (LINE_HEIGHT * 5) + (PADDING * 2);
	}
}
