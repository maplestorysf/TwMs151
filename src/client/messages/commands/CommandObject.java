package client.messages.commands;

import client.MapleClient;
import constants.ServerConstants.CommandType;

public class CommandObject {

    private final String command;

    private final int gmLevelReq;

    private final CommandExecute exe;

    public CommandObject(String com, CommandExecute c, int gmLevel) {
        command = com;
        exe = c;
        gmLevelReq = gmLevel;
    }

    public boolean execute(MapleClient c, String[] splitted) {
        return exe.execute(c, splitted);
    }

    public CommandType getType() {
        return exe.getType();
    }

    public int getReqGMLevel() {
        return gmLevelReq;
    }

    public String getMessage() {
        return command != null ? exe.getMessage() : "";
    }
}
