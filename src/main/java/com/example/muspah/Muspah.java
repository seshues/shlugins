package com.example.muspah;

import com.example.EthanApiPlugin.Collections.query.QuickPrayer;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InteractionHelper;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
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
        name = "[SH] Muspah",
        description = "Automatically switches your prayers during the Muspah fight",
        tags = {"ethan", "seshu"},
        enabledByDefault = false
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@Slf4j

public class Muspah extends Plugin {
    @Inject
    Client client;

    boolean forceTab = false;

    Set<Integer> MUSPAH_IDS = Set.of(NpcID.PHANTOM_MUSPAH, NpcID.PHANTOM_MUSPAH_12078, NpcID.PHANTOM_MUSPAH_12079,
            NpcID.PHANTOM_MUSPAH_12080, NpcID.PHANTOM_MUSPAH_12082);

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

        NPC muspah = client.getNpcs().stream().filter(x -> MUSPAH_IDS.contains(x.getId())).findFirst().orElse(null);
        if (muspah != null && (muspah.isDead() || muspah.getHealthRatio() == 0)) {
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

        if (muspah != null) {
            if ((muspah.getId() == NpcID.PHANTOM_MUSPAH) || (muspah.getId() == NpcID.PHANTOM_MUSPAH_12079) ||
                    (muspah.getId() == NpcID.PHANTOM_MUSPAH_12080)) {
                if (rigourUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.RIGOUR)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 24); //quickPrayer rigour
                } else if (!rigourUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.EAGLE_EYE)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 22); //quickPrayer eagle eye
                }
            }

            if (muspah.getId() == NpcID.PHANTOM_MUSPAH_12078) {
                if (auguryUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.AUGURY)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 27); //quickPrayer augury
                } else if (!auguryUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.MYSTIC_MIGHT)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 23); //quickPrayer mystic might
                }
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

    public boolean auguryUnlocked() {
        return !(client.getVarbitValue(5452) == 0) && client.getRealSkillLevel(Skill.PRAYER) >= 77 && client.getRealSkillLevel(Skill.DEFENCE) >= 70;
    }

    @Override
    protected void startUp() {
        forceTab = false;
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if (e.getActor() == null) {
            return;
        }
        if (!(e.getActor() instanceof NPC)) {
            return;
        }

        NPC npc = (NPC) e.getActor();
        if (!MUSPAH_IDS.contains(npc.getId())) {
            return;
        }
        if (e.getActor().getAnimation() == 9920 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MELEE)) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 14); //quickPrayer melee
        }
        if (e.getActor().getAnimation() == 9922 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MISSILES)) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 13); //quickPrayer range
        }
        if (e.getActor().getAnimation() == 9918 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MAGIC)) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 12); //quickPrayer mage
        }
    }
}
