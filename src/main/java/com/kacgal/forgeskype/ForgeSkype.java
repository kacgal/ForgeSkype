package com.kacgal.forgeskype;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ForgeSkype.MODID, version = ForgeSkype.VERSION, name = ForgeSkype.NAME)
public class ForgeSkype {
    public static final String MODID = "forgeskype";
    public static final String VERSION = "1.0";
    public static final String NAME = "Skype for Forge";

    @EventHandler
    public void init(FMLInitializationEvent e) {
        try {
            Skype.addChatMessageListener(new ChatMessageAdapter() {
                @Override
                public void chatMessageReceived(ChatMessage cm) throws SkypeException {
                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(String.format("[Skype] %s: %s", cm.getSender().getDisplayName(), cm.getContent())));
                }
            });
        } catch (SkypeException ex) {
            ex.printStackTrace();
        }
    }
}
