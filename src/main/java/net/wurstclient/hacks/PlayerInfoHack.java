/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.world.entity.player.Player;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.clickgui.Window;
import net.wurstclient.clickgui.components.PlayerInfoComponent;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;

@SearchTags({"player info", "equipment", "enchants", "armor", "weapon"})
public final class PlayerInfoHack extends Hack implements UpdateListener
{
	private final Window window;
	private Player targetPlayer;
	
	private final CheckboxSetting showSelf = new CheckboxSetting("Show self",
		"Shows your own equipment when not looking at another player.", true);
	
	private final SliderSetting range = new SliderSetting("Range",
		"Maximum range to detect players. Default 1000 = infinite within render distance.",
		1000, 10, 1000, 10, SliderSetting.ValueDisplay.INTEGER);
	
	public PlayerInfoHack()
	{
		super("PlayerInfo");
		setCategory(Category.RENDER);
		addSetting(showSelf);
		addSetting(range);
		
		window = new Window("Gear");
		window.setPinned(true);
		window.setInvisible(true);
		window.add(new PlayerInfoComponent(this));
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		window.setInvisible(false);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		window.setInvisible(true);
		targetPlayer = null;
	}
	
	@Override
	public void onUpdate()
	{
		if(MC.player == null || MC.level == null)
		{
			targetPlayer = null;
			return;
		}
		
		// looking dir
		net.minecraft.world.phys.Vec3 eyePos = MC.player.getEyePosition(1.0F);
		net.minecraft.world.phys.Vec3 lookVec = MC.player.getViewVector(1.0F);
		double maxRange = range.getValue();
		net.minecraft.world.phys.Vec3 endPos = eyePos.add(lookVec.x * maxRange,
			lookVec.y * maxRange, lookVec.z * maxRange);
		
		// closest in dir
		Player closestPlayer = null;
		double closestDistance = maxRange;
		
		for(net.minecraft.world.entity.Entity entity : MC.level
			.entitiesForRendering())
		{
			if(!(entity instanceof Player player) || entity == MC.player)
				continue;
			
			// in range
			double distance = MC.player.distanceTo(entity);
			if(distance > maxRange)
				continue;
			
			net.minecraft.world.phys.AABB box = entity.getBoundingBox();
			
			// truly looking at
			java.util.Optional<net.minecraft.world.phys.Vec3> hit =
				box.clip(eyePos, endPos);
			
			if(hit.isPresent() && distance < closestDistance)
			{
				closestPlayer = player;
				closestDistance = distance;
			}
		}
		
		if(closestPlayer != null)
		{
			targetPlayer = closestPlayer;
			return;
		}
		
		// show own data
		if(showSelf.isChecked())
			targetPlayer = MC.player;
		else
			targetPlayer = null;
	}
	
	public Window getWindow()
	{
		return window;
	}
	
	public Player getTargetPlayer()
	{
		return targetPlayer;
	}
}
