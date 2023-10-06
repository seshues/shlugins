package com.example;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.leviathan.Leviathan;
import com.example.whisperer.Whisperer;
import com.example.toaextended.ToaExtendedPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class, Leviathan.class, Whisperer.class,
                ToaExtendedPlugin.class);
        RuneLite.main(args);
    }
}