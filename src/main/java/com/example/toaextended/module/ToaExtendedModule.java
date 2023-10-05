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
package com.example.toaextended.module;

import com.example.toaextended.ToaExtendedConfig;
import com.example.toaextended.boss.akkha.Akkha;
import com.example.toaextended.boss.akkha.AkkhaFinalStand;
import com.example.toaextended.boss.akkha.AkkhaMemoryBlast;
import com.example.toaextended.boss.akkha.AkkhaPrayerInfoboxOverlay;
import com.example.toaextended.boss.akkha.AkkhaPrayerWidgetOverlay;
import com.example.toaextended.boss.akkha.AkkhaSceneOverlay;
import com.example.toaextended.boss.baba.Baba;
import com.example.toaextended.boss.baba.BabaSceneOverlay;
import com.example.toaextended.boss.kephri.Kephri;
import com.example.toaextended.boss.kephri.KephriSceneOverlay;
import com.example.toaextended.boss.warden.phase2.WardenP2;
import com.example.toaextended.boss.warden.phase2.WardenP2PrayerInfoboxOverlay;
import com.example.toaextended.boss.warden.phase2.WardenP2PrayerWidgetOverlay;
import com.example.toaextended.boss.warden.phase2.WardenP2SceneOverlay;
import com.example.toaextended.boss.warden.phase3.WardenP3;
import com.example.toaextended.boss.warden.phase3.WardenP3PrayerInfoboxOverlay;
import com.example.toaextended.boss.warden.phase3.WardenP3PrayerWidgetOverlay;
import com.example.toaextended.boss.warden.phase3.WardenP3SceneOverlay;
import com.example.toaextended.boss.zebak.Zebak;
import com.example.toaextended.boss.zebak.ZebakPrayerInfoboxOverlay;
import com.example.toaextended.boss.zebak.ZebakPrayerWidgetOverlay;
import com.example.toaextended.boss.zebak.ZebakSceneOverlay;
import com.example.toaextended.challenge.QuickProceedSwaps;
import com.example.toaextended.challenge.apmeken.Apmeken;
import com.example.toaextended.challenge.apmeken.ApmekenOverlay;
import com.example.toaextended.challenge.het.Het;
import com.example.toaextended.challenge.het.HetOverlay;
import com.example.toaextended.challenge.scabaras.ScabarasAdditionPuzzle;
import com.example.toaextended.challenge.scabaras.ScabarasLightPuzzle;
import com.example.toaextended.challenge.scabaras.ScabarasMatchingPuzzle;
import com.example.toaextended.challenge.scabaras.ScabarasObeliskPuzzle;
import com.example.toaextended.challenge.scabaras.ScabarasOverlay;
import com.example.toaextended.challenge.scabaras.ScabarasSequencePuzzle;
import com.example.toaextended.hud.FadeDisabler;
import com.example.toaextended.hud.HpOrbManager;
import com.example.toaextended.nexus.PathLevelTracker;
import com.example.toaextended.pointstracker.PointsTracker;
import com.example.toaextended.tomb.SarcophagusRecolorer;
import com.example.toaextended.util.RaidStateTracker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

@Slf4j
public class ToaExtendedModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		final Multibinder<PluginLifecycleComponent> lifecycleComponents =
			Multibinder.newSetBinder(binder(), PluginLifecycleComponent.class);

		lifecycleComponents.addBinding().to(RaidStateTracker.class);
		lifecycleComponents.addBinding().to(PathLevelTracker.class);

		lifecycleComponents.addBinding().to(PointsTracker.class);

		lifecycleComponents.addBinding().to(Baba.class);
		lifecycleComponents.addBinding().to(BabaSceneOverlay.class);

		lifecycleComponents.addBinding().to(Kephri.class);
		lifecycleComponents.addBinding().to(KephriSceneOverlay.class);

		lifecycleComponents.addBinding().to(Akkha.class);
		lifecycleComponents.addBinding().to(AkkhaSceneOverlay.class);
		lifecycleComponents.addBinding().to(AkkhaPrayerWidgetOverlay.class);
		lifecycleComponents.addBinding().to(AkkhaPrayerInfoboxOverlay.class);
		lifecycleComponents.addBinding().to(AkkhaMemoryBlast.class);
		lifecycleComponents.addBinding().to(AkkhaFinalStand.class);

		lifecycleComponents.addBinding().to(Zebak.class);
		lifecycleComponents.addBinding().to(ZebakSceneOverlay.class);
		lifecycleComponents.addBinding().to(ZebakPrayerWidgetOverlay.class);
		lifecycleComponents.addBinding().to(ZebakPrayerInfoboxOverlay.class);

		lifecycleComponents.addBinding().to(WardenP2.class);
		lifecycleComponents.addBinding().to(WardenP2SceneOverlay.class);
		lifecycleComponents.addBinding().to(WardenP2PrayerWidgetOverlay.class);
		lifecycleComponents.addBinding().to(WardenP2PrayerInfoboxOverlay.class);

		lifecycleComponents.addBinding().to(WardenP3.class);
		lifecycleComponents.addBinding().to(WardenP3SceneOverlay.class);
		lifecycleComponents.addBinding().to(WardenP3PrayerWidgetOverlay.class);
		lifecycleComponents.addBinding().to(WardenP3PrayerInfoboxOverlay.class);

		lifecycleComponents.addBinding().to(Apmeken.class);
		lifecycleComponents.addBinding().to(ApmekenOverlay.class);

		lifecycleComponents.addBinding().to(ScabarasAdditionPuzzle.class);
		lifecycleComponents.addBinding().to(ScabarasLightPuzzle.class);
		lifecycleComponents.addBinding().to(ScabarasMatchingPuzzle.class);
		lifecycleComponents.addBinding().to(ScabarasObeliskPuzzle.class);
		lifecycleComponents.addBinding().to(ScabarasSequencePuzzle.class);
		lifecycleComponents.addBinding().to(ScabarasOverlay.class);

		lifecycleComponents.addBinding().to(Het.class);
		lifecycleComponents.addBinding().to(HetOverlay.class);

		lifecycleComponents.addBinding().to(FadeDisabler.class);
		lifecycleComponents.addBinding().to(HpOrbManager.class);
		lifecycleComponents.addBinding().to(QuickProceedSwaps.class);
		lifecycleComponents.addBinding().to(SarcophagusRecolorer.class);
	}

	@Provides
	@Singleton
	ToaExtendedConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(ToaExtendedConfig.class);
	}

}
