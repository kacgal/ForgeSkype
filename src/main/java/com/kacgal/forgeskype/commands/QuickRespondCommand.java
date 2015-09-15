package com.kacgal.forgeskype.commands;

import com.kacgal.forgeskype.ConfigKey;
import com.kacgal.forgeskype.ForgeSkype;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraftforge.client.ClientCommandHandler;

public class QuickRespondCommand extends BaseCommand {
    public QuickRespondCommand(String cmd) {
        super(cmd);
    }

    @Override
    public String getCommandArguments() {
        return "<message>";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            throw new WrongUsageException(getCommandUsage());
        if (ForgeSkype.lastMessagedUser.equals("")) {
            ForgeSkype.sendMessage("You haven't messaged anyone yet!");
            return;
        }
        String[] a = new String[args.length + 1];
        System.arraycopy(args, 0, a, 1, args.length);
        a[0] = ForgeSkype.lastMessagedUser;
        ((SendSkypeMessageCommand) ClientCommandHandler.instance.getCommands().get(ForgeSkype.getConfigValue(ConfigKey.SEND_MESSAGE_COMMAND))).execute(sender, a);
    }
}
