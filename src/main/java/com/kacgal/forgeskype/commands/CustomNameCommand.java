package com.kacgal.forgeskype.commands;

import com.kacgal.forgeskype.ConfigKey;
import com.kacgal.forgeskype.ForgeSkype;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CustomNameCommand extends BaseCommand {

    enum SubCommand {
        LIST, ADD, RENAME, REMOVE;
    }

    public CustomNameCommand(String cmd) {
        super(cmd);
    }

    @Override
    public String getCommandArguments() {
        return "<add|list|remove> [custom name] [skype name]";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            throw new WrongUsageException(getCommandUsage());
        SubCommand sc;
        try {
            sc = SubCommand.valueOf(args[0].toUpperCase());
        }
        catch (IllegalArgumentException ex) {
            throw new WrongUsageException(getCommandUsage());
        }
        switch (sc) {
            case LIST:
                ForgeSkype.sendModMessage(ConfigKey.CUSTOMNAMES_HEADER);
                for (String cname : ForgeSkype.customNamesMap.keySet()) {
                    ForgeSkype.sendModMessage(ConfigKey.CUSTOMNAMES_CONTENT, ForgeSkype.getUserVars(cname));
                }
                ForgeSkype.sendModMessage(ConfigKey.CUSTOMNAMES_FOOTER);
                return;
            case ADD:
                if (args.length < 3) break;
                ForgeSkype.customNamesMap.put(args[1], args[2]);
                ForgeSkype.saveCustomNames();
                ForgeSkype.sendMessage("Added custom name %s", args[1]);
                return;
            case RENAME:
                if (args.length < 3) break;
                if (!ForgeSkype.customNamesMap.containsKey(args[1]))
                    ForgeSkype.sendMessage("No such custom name exists!");
                else {
                    ForgeSkype.customNamesMap.put(args[2], ForgeSkype.customNamesMap.remove(args[1]));
                    if (ForgeSkype.groupChats.containsKey(args[1]))
                        ForgeSkype.groupChats.put(args[2], ForgeSkype.groupChats.get(args[1]));
                    ForgeSkype.saveCustomNames();
                    ForgeSkype.sendMessage("Renamed %s to %s", args[1], args[2]);
                }
                return;
            case REMOVE:
                if (args.length < 2) break;
                if (!ForgeSkype.customNamesMap.containsKey(args[1]))
                    ForgeSkype.sendMessage("No such custom name exists!");
                else {
                    ForgeSkype.customNamesMap.remove(args[1]);
                    ForgeSkype.saveCustomNames();
                }
                return;
        }
        throw new WrongUsageException(getCommandUsage());
    }
}
