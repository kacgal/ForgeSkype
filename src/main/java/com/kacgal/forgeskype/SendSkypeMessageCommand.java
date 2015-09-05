package com.kacgal.forgeskype;

import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.ArrayList;
import java.util.List;

public class SendSkypeMessageCommand extends CommandBase {
    @Override
    public String getName() {
        return "ssm";
    }

    @Override
    public List getAliases() {
        return new ArrayList(){{
            add("sm");
        }};
    }

    @Override
    public boolean canCommandSenderUse(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ssm <skype user> <message>";
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
            User u = Skype.getUser(args[0]);
            String m = msg.toString();
            u.send(m);
            ForgeSkype.sendMessage("[Skype] -> %s: %s", u.getDisplayName(), m);
        } catch (SkypeException e) {
            ForgeSkype.sendMessage("Failed to send message");
        }
    }
}
