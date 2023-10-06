package com.example.whisperer;

import com.example.EthanApiPlugin.Collections.query.QuickPrayer;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InteractionHelper;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "[SH] Whisperer",
        description = "Automatically switches your prayers during the Whisperer fight",
        tags = {"pajau", "ethan", "seshu"},
        enabledByDefault = false
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@Slf4j

public class Whisperer extends Plugin {
    @Inject
    Client client;

    Projectile[] projectiles = new Projectile[5];
    int c = 0;

    boolean forceTab = false;

    Set<Integer> WHISPERER_IDS = Set.of(NpcID.THE_WHISPERER, NpcID.THE_WHISPERER_12205, NpcID.THE_WHISPERER_12206,
            NpcID.THE_WHISPERER_12207);

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        if (client.getGameCycle() < event.getProjectile().getStartCycle()) {
            projectiles[c]=event.getProjectile();
            c = (c+1)%5;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getLocalPlayer().isDead() || client.getLocalPlayer().getHealthRatio() == 0) {
            forceTab = false;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            forceTab = false;
            return;
        }

        NPC whisperer = client.getNpcs().stream().filter(x -> WHISPERER_IDS.contains(x.getId())).findFirst().orElse(null);
        if (whisperer != null && (whisperer.isDead() || whisperer.getHealthRatio() == 0)) {
            if (EthanApiPlugin.isQuickPrayerEnabled()) {
                InteractionHelper.togglePrayer();
                return;
            }
        }

        if (forceTab) {
            client.runScript(915, 3);
            forceTab = false;
        }

        if (client.getWidget(5046276) == null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB), "Setup");
            forceTab = true;
        }

        Optional<Projectile> proj2Pray = Arrays.stream(projectiles).min(Comparator.comparingInt(x -> Math.abs(30 - x.getRemainingCycles())));
        if (proj2Pray.isPresent()) {
            if (proj2Pray.get().getId() == 2445 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MAGIC)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 12); //quickPrayer magic
            } else if (proj2Pray.get().getId() == 2444 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MISSILES)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 13); //quickPrayer range
            }
        }

        if (whisperer != null) {
            if (auguryUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.AUGURY)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 27); //quickPrayer augury
            } else if (!auguryUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.MYSTIC_MIGHT)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 23); //quickPrayer mystic might
            }
            if (EthanApiPlugin.isQuickPrayerEnabled()) {
                InteractionHelper.togglePrayer();
            }
            InteractionHelper.togglePrayer();
        }
    }

    public boolean auguryUnlocked() {
        return !(client.getVarbitValue(5452) == 0) && client.getRealSkillLevel(Skill.PRAYER) >= 77 && client.getRealSkillLevel(Skill.DEFENCE) >= 70;
    }

    @Override
    protected void startUp() {
        forceTab = false;
    }
}
