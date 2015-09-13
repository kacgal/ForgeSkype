package com.kacgal.forgeskype;

import com.kacgal.forgeskype.commands.CallCommand;
import com.skype.Call;
import com.skype.CallMonitorListener;
import com.skype.SkypeException;

public class CallMonitor implements CallMonitorListener {
    @Override
    public void callMonitor(Call call, Call.Status status) throws SkypeException {
        switch (status) {
            case RINGING:
                // Call incomming/outgoing
                if (!CallCommand.isOutgoing()) {
                    ForgeSkype.latestCall = call;
                    ForgeSkype.sendModMessage(ConfigKey.CALL_RECEIVED_MESSAGE, ForgeSkype.getUserVars(call.getPartnerId()));
                }
                break;
            case REFUSED:
                // Call is refused
                if (CallCommand.isOutgoing()) {
                    ForgeSkype.sendModMessage(ConfigKey.CALL_REFUSED_REMOTE_MESSAGE, ForgeSkype.getUserVars(call.getPartnerId()));
                } else {
                    ForgeSkype.sendModMessage(ConfigKey.CALL_REFUSED_LOCAL_MESSAGE, ForgeSkype.getUserVars(call.getPartnerId()));
                }
                break;
            case INPROGRESS:
                // Call started/resumed
                if (ForgeSkype.heldCalls.contains(call)) {
                    ForgeSkype.sendModMessage(ConfigKey.CALL_RESUMED_MESSAGE, ForgeSkype.getUserVars(call.getPartnerId()));
                    ForgeSkype.heldCalls.remove(call);
                } else if (CallCommand.isOutgoing()) {
                    ForgeSkype.sendModMessage(ConfigKey.CALL_ACCEPTED_MESSAGE, ForgeSkype.getUserVars(call.getPartnerId()));
                    CallCommand.setOutgoing(false);
                }
                break;
            case LOCALHOLD:
            case REMOTEHOLD:
                // Call held locally/remotely
                ForgeSkype.heldCalls.add(call);
                if (!CallCommand.isOutgoing()) {
                    ForgeSkype.sendModMessage(ConfigKey.CALL_HELD_MESSAGE, ForgeSkype.getUserVars(call.getPartnerId()));
                }
                break;
            case FINISHED:
                //  Call ended
                if (!CallCommand.isOutgoing()) {
                    ForgeSkype.sendModMessage(ConfigKey.CALL_ENDED_MESSAGE, ForgeSkype.getUserVars(call.getPartnerId()));
                }
                break;
        }
    }
}
