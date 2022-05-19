package project.commands;

import lombok.Getter;
import lombok.Setter;
import project.smartCard.APDUUtils;

@Getter
@Setter
public class Command {
    protected byte[] command;
    protected String message;

    public byte[] runCommand(APDUUtils apduUtils) {
        return apduUtils.runCommand(command, message, false);
    }
}
