package com.kacgal.forgeskype.commands;

import com.kacgal.forgeskype.ConfigKey;
import com.kacgal.forgeskype.ForgeSkype;
import com.skype.Chat;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import org.apache.commons.lang3.StringUtils;

public class SendSkypeMessageCommand extends BaseCommand {

    public SendSkypeMessageCommand(String cmd) {
        super(cmd);
    }

    @Override
    public String getCommandArguments() {
        return "<skype user> <message>";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage());
        }
        StringBuilder msg = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++)
            msg.append(" ").append(args[i]);
        Chat c = null;
        if (args[0].contains(",")) {
            String[] us = args[0].split(",");
            for (int i = 0; i < us.length; i++) {
                us[i] = ForgeSkype.getSkype(us[i]);
            }
            try {
                c = Skype.chat(StringUtils.join(us, ","));
            } catch (SkypeException e) {
                ForgeSkype.sendMessage("Failed to send message");
            }
        }
        else if (ForgeSkype.groupChats.containsKey(args[0])) {
            c = ForgeSkype.groupChats.get(args[0]);
        }
        else {
            try {
                User u = Skype.getUser(ForgeSkype.getSkype(args[0]));
                if (!u.isAuthorized() || u.isBlocked()) {
                    ForgeSkype.sendModMessage(ConfigKey.ERR_NOT_FRIEND, ForgeSkype.getUserVars(args[0]));
                    return;
                }
                c = u.chat();
            } catch (SkypeException e) {
                ForgeSkype.sendMessage("Failed to send message");
            }
        }
        String m = msg.toString();
        try {
            if (c != null) {
                c.send(m);
                if (c.getAllMembers().length == 2) {
                    ForgeSkype.sendModMessage(ConfigKey.MESSAGE_SENT_FORMAT, ForgeSkype.getUserVars(args[0], 'm', m));
                }
                else {
                    ForgeSkype.sendModMessage(ConfigKey.GROUP_MESSAGE_RECEIVED_FORMAT, 'g', ForgeSkype.getCustomGroupName(c), 'm', m, 'd', "me");
                }
                ForgeSkype.lastMessagedUser = args[0];
            }
            else {
                ForgeSkype.sendMessage("Failed to send message");
            }
        } catch (SkypeException e) {
            ForgeSkype.sendMessage("Failed to send message");
        }
    }
}
