/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2023, rdutta <https://github.com/rdutta>
 * Copyright (c) 2022, LlemonDuck
 * Copyright (c) 2022, TheStonedTurtle
 * Copyright (c) 2019, Ron Young <https://github.com/raiyni>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.toaextended.boss.zebak;

import com.example.toaextended.ToaExtendedConfig;
import com.example.toaextended.boss.AttackProjectile;
import com.example.toaextended.boss.PrayerInfoboxOverlay;
import com.example.toaextended.module.PluginLifecycleComponent;
import com.example.toaextended.util.RaidState;
import java.util.Queue;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayManager;

@Singleton
public class ZebakPrayerInfoboxOverlay extends PrayerInfoboxOverlay implements PluginLifecycleComponent
{

	private final OverlayManager overlayManager;
	private final Zebak zebak;

	@Inject
	public ZebakPrayerInfoboxOverlay(
		final Client client,
		final ToaExtendedConfig config,
		final SpriteManager spriteManager,
		final OverlayManager overlayManager,
		final Zebak zebak)
	{
		super(client, config, spriteManager);
		this.overlayManager = overlayManager;
		this.zebak = zebak;
	}

	@Override
	protected Prayer getNextPrayer()
	{
		final Queue<AttackProjectile> queue = zebak.getAttackProjectiles();
		return queue.isEmpty() ? null : queue.peek().getPrayer();
	}

	@Override
	protected boolean isPrayerInfoboxEnabled()
	{
		return config.zebakPrayerIndicator();
	}

	@Override
	public boolean isEnabled(final ToaExtendedConfig config, final RaidState raidState)
	{
		return zebak.isEnabled(config, raidState);
	}

	@Override
	public void startUp()
	{
		overlayManager.add(this);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(this);
	}

}
