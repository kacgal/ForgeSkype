package com.kacgal.forgeskype.commands;

import com.kacgal.forgeskype.ConfigKey;
import com.kacgal.forgeskype.ForgeSkype;
import com.skype.Call;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.HashMap;

public class CallCommand extends BaseCommand {

    private static volatile boolean outgoing;

    enum SubCommand {
        ACCEPT, DENY, CALL, END, HOLD, LIST, RESUME;
    }

    public CallCommand(String cmd) {
        super(cmd);
    }

    @Override
    public String getCommandArguments() {
        return "<accept|deny|call|end|hold|list|resume> [user]";
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
        CallHolder callHolder = isInCall();
        switch (sc) {
            case ACCEPT:
                try {
                    ForgeSkype.latestCall.answer();
                } catch (SkypeException e) {
                    e.printStackTrace();
                }
                break;
            case DENY:
                try {
                    ForgeSkype.latestCall.finish();
                } catch (SkypeException e) {
                    e.printStackTrace();
                }
                break;
            case CALL:
                if (args.length < 2)
                    throw new WrongUsageException(getCommandUsage());
                outgoing = true;
                try {
                    Skype.call(ForgeSkype.getSkype(args[1]));
                } catch (SkypeException e) {
                    outgoing = false;
                    ForgeSkype.sendMessage("Failed to call");
                    return;
                }
                ForgeSkype.sendModMessage(ConfigKey.CALL_SENT_MESSAGE, ForgeSkype.getUserVars(args[1]));
                break;
            case END:
                if (callHolder.currentCall == null) {
                    if (args.length < 2) {
                        throw new WrongUsageException(getCommandUsage());
                    }
                    else {
                        User u = Skype.getUser(ForgeSkype.getSkype(args[1]));
                        if (callHolder.callMap.containsKey(u)) {
                            callHolder.currentCall = callHolder.callMap.get(u);
                        }
                    }
                }
                try {
                    callHolder.currentCall.finish();
                } catch (SkypeException e) {
                    e.printStackTrace();
                }
                break;
            case HOLD:
                if (callHolder.currentCall == null)
                    throw new WrongUsageException(getCommandUsage());
                try {
                    callHolder.currentCall.hold();
                } catch (SkypeException e) {
                    e.printStackTrace();
                }
                break;
            case LIST:
                ForgeSkype.sendModMessage(ConfigKey.CALLLIST_HEADER);
                for (User user : callHolder.callMap.keySet()) {
                    try {
                        ForgeSkype.sendModMessage(ConfigKey.CALLLIST_CONTENT, ForgeSkype.getUserVars(user.getId(), 'h', callHolder.callMap.get(user).getStatus().equals(Call.Status.INPROGRESS) ? "Ongoing" : "Held"));
                    } catch (SkypeException e) {
                        e.printStackTrace();
                    }
                }
                ForgeSkype.sendModMessage(ConfigKey.CALLLIST_FOOTER);
                break;
            case RESUME:
                if (args.length < 2)
                    throw new WrongUsageException(getCommandUsage());
                User u = Skype.getUser(ForgeSkype.getSkype(args[1]));
                for (User user : callHolder.callMap.keySet()) {
                    if (user.equals(u)) {
                        try {
                            callHolder.callMap.get(user).resume();
                        } catch (SkypeException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    public static boolean isOutgoing() {
        return outgoing;
    }

    public static void setOutgoing(boolean outgoing) {
        CallCommand.outgoing = outgoing;
    }

    private CallHolder isInCall() {
        CallHolder calls = new CallHolder();
        try {
            for (Call call : Skype.getAllActiveCalls()) {
                calls.callMap.put(call.getPartner(), call);
                if (call.getStatus().equals(Call.Status.INPROGRESS))
                    calls.currentCall = call;
            }
        } catch (SkypeException e) {
            e.printStackTrace();
        }
        return calls;
    }

    class CallHolder {
        public Call currentCall = null;
        public HashMap<User, Call> callMap = new HashMap<User, Call>();
    }
}
