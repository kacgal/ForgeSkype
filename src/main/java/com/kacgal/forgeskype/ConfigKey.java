package com.kacgal.forgeskype;

public enum ConfigKey {
    SEND_MESSAGE_COMMAND("ssm", "Command to send Skype messages"),
    CUSTOM_NAMES_COMMAND("cname", "Command to manage custom names"),
    PREFIX("[Skype]", "Prefix for all chat messages"),
    MESSAGE_RECEIVED_FORMAT("%d -> me: %m", "Format for received messages"),
    MESSAGE_SENT_FORMAT("me -> %d: %m", "Format for sent messages"),
    ERR_NOT_FRIEND("Couldn't send message, as %d isn't in your contacts", "Error message when user is not in contacts"),
    CUSTOMNAMES_HEADER("--- Custom Names ---", "Displayed before custom names"),
    CUSTOMNAMES_FOOTER("--------------------", "Displayed after custom names"),
    CUSTOMNAMES_CONTENT("%c -> %u (%d)", "Format for displaying custom names");

    public final String comment;
    public final String defaultValue;

    ConfigKey(String defaultValue, String comment) {
        this.defaultValue = defaultValue;
        this.comment = comment;
    }
}
