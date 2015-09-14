package com.kacgal.forgeskype;

import com.kacgal.forgeskype.commands.CallCommand;
import com.kacgal.forgeskype.commands.CustomNameCommand;
import com.kacgal.forgeskype.commands.SendSkypeMessageCommand;
import com.skype.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(modid = ForgeSkype.MODID, version = ForgeSkype.VERSION, name = ForgeSkype.NAME)
public class ForgeSkype {
    public static final String MODID = "forgeskype";
    public static final String VERSION = "1.2.1";
    public static final String NAME = "Skype for Forge";

    private static File customNamesFile = new File("skypecustomnames.txt");
    public static HashMap<String, String> customNamesMap = new HashMap<String, String>();

    public static List<Call> heldCalls = new ArrayList<Call>();
    public static Call latestCall = null;

    public static HashMap<String, Chat> groupChats = new HashMap<String, Chat>();

    private static Configuration config = null;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new Configuration(e.getSuggestedConfigurationFile());
        config.load();
        config.setCategoryComment(Configuration.CATEGORY_GENERAL, StringUtils.join(new String[] {
                "Variables:",
                "%c: Custom name",
                "%u: Username",
                "%d: Display name",
                "%m: Message",
                "%h: Held/Ongoing (for calls)",
                "%g: Group"
        }, '\n'));
        for (ConfigKey key : ConfigKey.values()) {
            getConfigValue(key);
        }
        config.save();
    }

    public static String getConfigValue(ConfigKey key) {
        return config.getString(key.toString(), Configuration.CATEGORY_GENERAL, key.defaultValue, key.comment);
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent e) {
        try {
            if (!customNamesFile.exists() && !customNamesFile.createNewFile())
                throw new IOException("Failed to create customnamesfile.txt");
            loadCustomNames();

            connectSkype();
            loadSkypeGroups();
        }
        catch (IOException ex) {
            System.err.println("Failed to load custom names");
            ex.printStackTrace();
            return;
        }
        catch (SkypeException ex) {
            System.err.println("Failed to connect to Skype");
            ex.printStackTrace();
            return;
        }

        registerCommands();
    }

    private void loadCustomNames() throws IOException {
        List<String> customNames = Files.readAllLines(customNamesFile.toPath(), Charset.defaultCharset());
        for (String customName : customNames) {
            String[] c = customName.split("\\|");
            customNamesMap.put(c[0], c[1]);
        }
    }

    private void connectSkype() throws SkypeException {
        SkypeClient.setSilentMode(true);
        Skype.addChatMessageListener(new ChatMessageAdapter() {
            @Override
            public void chatMessageReceived(ChatMessage cm) throws SkypeException {
                sendModMessage(cm.getChat().getAllMembers().length == 2 ? ConfigKey.MESSAGE_RECEIVED_FORMAT : ConfigKey.GROUP_MESSAGE_RECEIVED_FORMAT, getUserVars(cm.getSenderId(), 'm', cm.getContent(), 'g', getCustomGroupName(cm.getChat())));
            }
        });
        Skype.addCallMonitorListener(new CallMonitor());
    }

    private void loadSkypeGroups() throws SkypeException {
        for (Chat c : Skype.getAllChats())
            if (customNamesMap.containsValue(c.getId()))
                groupChats.put(getCustomGroupName(c), c);
    }

    private void registerCommands() {
        ClientCommandHandler.instance.registerCommand(new SendSkypeMessageCommand(getConfigValue(ConfigKey.SEND_MESSAGE_COMMAND)));
        ClientCommandHandler.instance.registerCommand(new CustomNameCommand(getConfigValue(ConfigKey.CUSTOM_NAMES_COMMAND)));
        ClientCommandHandler.instance.registerCommand(new CallCommand(getConfigValue(ConfigKey.CALL_COMMAND)));
    }


    public static void saveCustomNames() {
        try {
            customNamesFile.delete();
            customNamesFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(customNamesFile));
            StringBuilder b = new StringBuilder();
            for (String cname : customNamesMap.keySet())
                b.append(cname).append("|").append(customNamesMap.get(cname)).append("\n");
            writer.write(b.toString());
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to save custom names");
            e.printStackTrace();
        }
    }

    public static void sendModMessage(ConfigKey message, Object... values) {
        String tf = parseColors(getConfigValue(message));
        for (int i = 0; i < values.length; i += 2) {
            tf = tf.replaceAll("%" + values[i], String.valueOf(values[i + 1]).replaceAll("\\$", "\\\\\\$"));
        }
        sendMessage(parseColors(getConfigValue(ConfigKey.PREFIX)) + " " + tf);
    }

    private static String parseColors(String s) {
        return s.replaceAll("&([a-fA-F0-9])", "\u00A7$1");
    }

    public static void sendMessage(String msg, String... format) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(String.format(msg, format)));
    }

    public static Object[] getUserVars(String cname, Object... args) {
        User u = Skype.getUser(getSkype(cname));
        Object[] a = new Object[] {
                'd', cname,
                'u', u.getId(),
                'c', cname
        };
        try {
            if (groupChats.containsKey(cname)) {
                Chat c = groupChats.get(cname);
                a[1] = c.getWindowTitle();
                a[3] = StringUtils.join(c.getAllMembers(), ", ");
            }
            else {
                a[1] = u.getDisplayName();
            }
        } catch (SkypeException ex) {
            ex.printStackTrace();
        }
        Object[] b = new Object[a.length + args.length];
        System.arraycopy(a, 0, b, 0, a.length);
        System.arraycopy(args, 0, b, a.length, args.length);
        return b;
    }

    public static String getSkype(String name) {
        return customNamesMap.containsKey(name) ? customNamesMap.get(name) : name;
    }

    public static String getCustomGroupName(Chat c) {
        for (String s : customNamesMap.keySet())
            if (customNamesMap.get(s).equals(c.getId()))
                return s;
        return "Unknown";
    }
}
