package com.kacgal.forgeskype;

public enum ConfigKey {
    SEND_MESSAGE_COMMAND("ssm", "Command to send Skype messages"),
    CUSTOM_NAMES_COMMAND("cname", "Command to manage custom names"),
    CALL_COMMAND("scall", "Command to manage calls"),
    PREFIX("[Skype]", "Prefix for all chat messages"),
    MESSAGE_RECEIVED_FORMAT("%d -> me: %m", "Format for received messages"),
    MESSAGE_SENT_FORMAT("me -> %d: %m", "Format for sent messages"),
    ERR_NOT_FRIEND("Couldn't send message, as %d isn't in your contacts", "Error message when user is not in contacts"),
    CUSTOMNAMES_HEADER("--- Custom Names ---", "Displayed before custom names"),
    CUSTOMNAMES_FOOTER("--------------------", "Displayed after custom names"),
    CUSTOMNAMES_CONTENT("%c -> %u (%d)", "Format for displaying custom names"),
    CALL_SENT_MESSAGE("Call sent to %d", "Message displayed when sending calls"),
    CALL_RECEIVED_MESSAGE("Call from %d", "Message displayed when recieving calls"),
    CALL_ENDED_MESSAGE("Call with %d ended", "Message displayed when call ended"),
    CALL_REFUSED_REMOTE_MESSAGE("%d refused your call", "Message when your call is refused"),
    CALL_REFUSED_LOCAL_MESSAGE("You refused %d's call", "Message when you refuse a call"),
    CALL_HELD_MESSAGE("The call with %d is now on hold", "Message when call is held"),
    CALL_RESUMED_MESSAGE("The call with %d is now resumed", "Message when call is unheld"),
    CALL_ACCEPTED_MESSAGE("You are now in a call with %d", "Message when user accepts your call"),
    CALLLIST_HEADER("--- Calls ---", "Displayed before calls"),
    CALLLIST_FOOTER("-------------", "Displayed after calls"),
    CALLLIST_CONTENT("%d (%h)", "Format for displaying calls");

    public final String comment;
    public final String defaultValue;

    ConfigKey(String defaultValue, String comment) {
        this.defaultValue = defaultValue;
        this.comment = comment;
    }
}
