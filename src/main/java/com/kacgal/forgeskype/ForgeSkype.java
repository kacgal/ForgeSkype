package com.kacgal.forgeskype;

import com.skype.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.io.IOException;

@Mod(modid = ForgeSkype.MODID, version = ForgeSkype.VERSION, name = ForgeSkype.NAME)
public class ForgeSkype {
    public static final String MODID = "forgeskype";
    public static final String VERSION = "1.0";
    public static final String NAME = "Skype for Forge";

    @EventHandler
    public void init(FMLInitializationEvent e) {
        try {
            SkypeClient.setSilentMode(true);
            Skype.addChatMessageListener(new ChatMessageAdapter() {
                @Override
                public void chatMessageReceived(ChatMessage cm) throws SkypeException {
                    sendMessage("[Skype] %s: %s", cm.getSender().getDisplayName(), cm.getContent());
                }
            });
        } catch (SkypeException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        ClientCommandHandler.instance.registerCommand(new SendSkypeMessageCommand());
        try {
            ClientCommandHandler.instance.registerCommand(new CustomNameCommand());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void sendMessage(String msg, String... format) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(String.format(msg, format)));
    }
}
