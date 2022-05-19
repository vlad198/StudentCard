package project.commands;

import project.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class VerifyPinCommand extends Command {
    private static final byte VERIFY_PIN_CLA = Utils.getByteFromHexCode("0x80");
    private static final byte VERIFY_PIN_INS = Utils.getByteFromHexCode("0x20");
    private static final byte VERIFY_PIN_P1 = Utils.getByteFromHexCode("0x00");
    private static final byte VERIFY_PIN_P2 = Utils.getByteFromHexCode("0x00");
    private static final byte VERIFY_PIN_LE = Utils.getByteFromHexCode("0x7F");

    public VerifyPinCommand() {
        setMessage("VERIFY PIN");
    }

    public void setPin(List<Integer> pin)
    {
        for(Integer value: pin)
            if(!(value >= 0 && value <=9))
            {
                System.out.println("Invalid PIN");
                return;
            }

            List<Byte> newCommand = new LinkedList<>();

            newCommand.add(VERIFY_PIN_CLA);
            newCommand.add(VERIFY_PIN_INS);
            newCommand.add(VERIFY_PIN_P1);
            newCommand.add(VERIFY_PIN_P2);

            newCommand.add((byte) pin.size());
            for(int value: pin)
            {
                String hexValue = Utils.toHexString(value);
                newCommand.add(Utils.getByteFromHexCode(hexValue));
            }

            newCommand.add(VERIFY_PIN_LE);

            setCommand(Utils.getByteArrayFromList(newCommand));
    }
}
