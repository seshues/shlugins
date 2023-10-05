package com.example.leviathan;

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
        name = "<html>[<font color=\"#59D634\">P</font>] Leviathan</html>",
        description = "Automatically switches your prayers during the Leviathan fight",
        tags = {"pajau", "ethan", "seshu"},
        enabledByDefault = false
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@Slf4j

public class Leviathan extends Plugin {
    @Inject
    Client client;

    Projectile[] projectiles = new Projectile[10];
    int c = 0;

    boolean forceTab = false;

    Set<Integer> LEVIATHAN_IDS = Set.of(NpcID.THE_LEVIATHAN, NpcID.THE_LEVIATHAN_12215, NpcID.THE_LEVIATHAN_12219,
            NpcID.THE_LEVIATHAN_12221, 12241);

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

        NPC leviathan = client.getNpcs().stream().filter(x -> LEVIATHAN_IDS.contains(x.getId())).findFirst().orElse(null);
        if (leviathan != null && (leviathan.isDead() || leviathan.getHealthRatio() == 0)) {
            if (EthanApiPlugin.isQuickPrayerEnabled()) {
                InteractionHelper.togglePrayer();
                return;
            }
        }

        /* GET LEVIATHAN VARBIT VALUES
        if (client.getVarbitValue() != 1) {
            forceTab = false;
            return;
        }
        if (client.getVarbitValue() != 1) {
            forceTab = false;
            return;
        }*/

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
            if (proj2Pray.get().getId() == 2489 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MAGIC)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 12); //quickPrayer magic
            } else if (proj2Pray.get().getId() == 2487 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MISSILES)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 13); //quickPrayer range
            } else if (proj2Pray.get().getId() == 2488 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MELEE)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 14); //quickPrayer melee
            }
        }

        if (leviathan != null) {
            if (rigourUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.RIGOUR)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 24); //quickPrayer rigour
            } else if (!rigourUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.EAGLE_EYE)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 22); //quickPrayer eagle eye
            }
            if (EthanApiPlugin.isQuickPrayerEnabled()) {
                InteractionHelper.togglePrayer();
            }
            InteractionHelper.togglePrayer();
        }
    }

    public boolean rigourUnlocked() {
        return !(client.getVarbitValue(5451) == 0) && client.getRealSkillLevel(Skill.PRAYER) >= 74 && client.getRealSkillLevel(Skill.DEFENCE) >= 70;
    }

    @Override
    protected void startUp() {
        forceTab = false;
    }

    /*private boolean isLeviathanVarbitSet() {
        return client.getVarbitValue() == 1; // GET LEVIATHAN VARBIT VALUE
    }*/
}
