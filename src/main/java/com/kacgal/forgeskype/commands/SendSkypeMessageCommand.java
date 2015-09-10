package com.kacgal.forgeskype.commands;

import com.kacgal.forgeskype.ConfigKey;
import com.kacgal.forgeskype.ForgeSkype;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class SendSkypeMessageCommand extends BaseCommand {

    public SendSkypeMessageCommand(String cmd) {
        super(cmd);
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getName() + " <skype user> <message>";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        StringBuilder msg = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++)
            msg.append(" ").append(args[i]);
        try {
            User u = Skype.getUser(ForgeSkype.getSkype(args[0]));
            if (!u.isAuthorized() || u.isBlocked()) {
                ForgeSkype.sendModMessage(ConfigKey.ERR_NOT_FRIEND, ForgeSkype.getUserVars(args[0]));
                return;
            }
            String m = msg.toString();
            u.send(m);
            ForgeSkype.sendModMessage(ConfigKey.MESSAGE_SENT_FORMAT, ForgeSkype.getUserVars(args[0], 'm', m));
        } catch (SkypeException e) {
            ForgeSkype.sendMessage("Failed to send message");
        }
    }
}
