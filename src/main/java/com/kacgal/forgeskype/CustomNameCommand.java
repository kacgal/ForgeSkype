package com.kacgal.forgeskype;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class CustomNameCommand extends CommandBase {

    private static HashMap<String, String> cnameToSkype = new HashMap<>();
    private File f = new File("skypecustomnames.txt");

    public CustomNameCommand() throws IOException {
        if (!f.exists())
            f.createNewFile();
        List<String> cnames = Files.readAllLines(f.toPath(), Charset.defaultCharset());
        for (String cname : cnames) {
            String[] c = cname.split("\\|");
            cnameToSkype.put(c[0], c[1]);
        }
    }

    @Override
    public String getName() {
        return "cname";
    }

    @Override
    public boolean canCommandSenderUse(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cname <add|list|remove> [custom name] [skype name]";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            throw new WrongUsageException(getCommandUsage(sender));
        switch (args[0]) {
            case "list":
                for (String cname : cnameToSkype.keySet()) {
                    ForgeSkype.sendMessage("%s -> %s", cname, cnameToSkype.get(cname));
                }
                return;
            case "add":
                if (args.length < 3) break;
                cnameToSkype.put(args[1], args[2]);
                save();
                ForgeSkype.sendMessage("Added custom name %s", args[1]);
                return;
            case "remove":
                if (args.length < 2) break;
                if (!cnameToSkype.containsKey(args[1]))
                    ForgeSkype.sendMessage("No such custom name exists!");
                else {
                    cnameToSkype.remove(args[1]);
                    save();
                }
                return;
        }
        throw new WrongUsageException(getCommandUsage(sender));
    }

    private void save() {
        try {
            f.delete();
            f.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            StringBuilder b = new StringBuilder();
            for (String cname : cnameToSkype.keySet()) {
                b.append(cname).append("|").append(cnameToSkype.get(cname)).append("\n");
            }
            writer.write(b.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSkype(String name) {
        for (String cname : cnameToSkype.keySet()) {
            if (cname.equals(name)) {
                return cnameToSkype.get(cname);
            }
        }
        return name;
    }
}
