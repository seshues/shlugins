package com.example.toaprayers;

import com.example.EthanApiPlugin.Collections.query.QuickPrayer;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InteractionHelper;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.NpcID;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.util.Text;
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
        name = "[SH] ToA Auto Prayers",
        description = "Automatically switches your prayers in Tombs of Amascut",
        tags = {"seshu"},
        enabledByDefault = false
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@Slf4j

public class ToAPrayers extends Plugin {
    @Inject
    Client client;

    private NPC npc;

    // Zebak
    private static final int ZEBAK_MAGIC_ROCK = 2176;
    private static final int ZEBAK_MAGIC_ROCK_ENRAGED = 2177;
    private static final int ZEBAK_RANGE_ROCK = 2178;
    private static final int ZEBAK_RANGE_ROCK_ENRAGED = 2179;

    // Warden P2
    private static final int WP2_ARCANE_SCIMITAR = 2204;
    private static final int WP2_WHITE_ARROW = 2206;
    private static final int WP2_BLUE_SPELL = 2208;
    private static final int WP2_RED_SKULL = 2224;
    private static final int WP2_WHITE_SKULL = 2241;

    // Warden P3
    private static final int WP3_AKKHA_RANGE = 9772;
    private static final int WP3_AKKHA_MAGIC = 9774;


    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event)
    {
        if (client.getLocalPlayer().isDead() || client.getLocalPlayer().getHealthRatio() == 0) {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        final Projectile projectile = event.getProjectile();

        if (projectile.getRemainingCycles() <= 0)
        {
            return;
        }

        switch (projectile.getId())
        {
            case ZEBAK_MAGIC_ROCK:
            case ZEBAK_MAGIC_ROCK_ENRAGED:
            case WP2_WHITE_SKULL:
            case WP2_WHITE_ARROW:
            case WP3_AKKHA_MAGIC:
                if (client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MAGIC) == 0) {
                    InteractionHelper.toggleNormalPrayer(WidgetInfoExtended.PRAYER_PROTECT_FROM_MAGIC.getPackedId());
                }
                break;
            case ZEBAK_RANGE_ROCK:
            case ZEBAK_RANGE_ROCK_ENRAGED:
            case WP2_RED_SKULL:
            case WP2_BLUE_SPELL:
            case WP3_AKKHA_RANGE:
                if (client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0) {
                    InteractionHelper.toggleNormalPrayer(WidgetInfoExtended.PRAYER_PROTECT_FROM_MISSILES.getPackedId());
                }
                break;
            case WP2_ARCANE_SCIMITAR:
                if (client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 0) {
                    InteractionHelper.toggleNormalPrayer(WidgetInfoExtended.PRAYER_PROTECT_FROM_MELEE.getPackedId());
                }
                break;
            default:
                break;
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        final NPC npc = event.getNpc();
        this.npc = npc;
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        if (event.getNpc() == npc)
        {
            npc = null;
        }
    }

    @Subscribe
    private void onNpcChanged(final NpcChanged event)
    {
        final NPC npc = event.getNpc();

        if (npc != this.npc)
        {
            return;
        }

        final NPCComposition composition = npc.getComposition();

        switch (composition.getId())
        {
            case NpcID.AKKHA_11791:
                if (client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0) {
                    InteractionHelper.toggleNormalPrayer(WidgetInfoExtended.PRAYER_PROTECT_FROM_MISSILES.getPackedId());
                }
                break;
            case NpcID.AKKHA_11790:
                if (client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 0) {
                    InteractionHelper.toggleNormalPrayer(WidgetInfoExtended.PRAYER_PROTECT_FROM_MELEE.getPackedId());
                }
                break;
            case NpcID.AKKHA_11792:
                if (client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MAGIC) == 0) {
                    InteractionHelper.toggleNormalPrayer(WidgetInfoExtended.PRAYER_PROTECT_FROM_MAGIC.getPackedId());
                }
                break;
            default:
                return;
        }
    }

    @Subscribe
    private void onChatMessage(final ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }

        final String message = Text.standardize(event.getMessage());

        if (message.equals("challenge started: akkha."))
        {
            if (client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 0) {
                InteractionHelper.toggleNormalPrayer(WidgetInfoExtended.PRAYER_PROTECT_FROM_MELEE.getPackedId());
            }
        }
    }
}
