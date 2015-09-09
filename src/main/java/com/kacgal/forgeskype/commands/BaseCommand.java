package com.kacgal.forgeskype.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public abstract class BaseCommand extends CommandBase {

    private final String commandName;

    public BaseCommand(String cmd) {
        this.commandName = cmd;
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public boolean canCommandSenderUse(ICommandSender sender) {
        return true;
    }

    @Override
    public abstract String getCommandUsage(ICommandSender sender);

    @Override
    public abstract void execute(ICommandSender sender, String[] args) throws CommandException;
}
