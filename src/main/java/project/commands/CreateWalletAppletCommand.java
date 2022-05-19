package project.commands;

import project.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class CreateWalletAppletCommand extends Command{
//    String command = "0x80 0xB8 0x00 0x00 0x14 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x06 0x01 0x08 0x00 0x00 0x05 0x01 0x02 0x03 0x04 0x05 0x7F;";
    private static final byte CREATE_APPLET_CLA = Utils.getByteFromHexCode("0x80");
    private static final byte CREATE_APPLET_INS = Utils.getByteFromHexCode("0xB8");
    private static final byte CREATE_APPLET_P1 = Utils.getByteFromHexCode("0x00");
    private static final byte CREATE_APPLET_P2 = Utils.getByteFromHexCode("0x00");
    private static final byte CREATE_APPLET_LE = Utils.getByteFromHexCode("0x7F");

    public CreateWalletAppletCommand() {
        setMessage("CREATE WALLET APPLET");
    }

    public void createApplet()
    {
        List<Byte> newCommand = new LinkedList<>();

        newCommand.add(CREATE_APPLET_CLA);
        newCommand.add(CREATE_APPLET_INS);
        newCommand.add(CREATE_APPLET_P1);
        newCommand.add(CREATE_APPLET_P2);

        String walletArg = "0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x06 0x01 0x08 0x00 0x00 0x05 0x01 0x02 0x03 0x04 0x05";
        byte[] args = Utils.getByteArrayFromList(Utils.convertStringToByteList(walletArg));

        newCommand.add((byte) args.length);
        for(byte value: args)
        {
            newCommand.add(value);
        }

        newCommand.add(CREATE_APPLET_LE);

        setCommand(Utils.getByteArrayFromList(newCommand));
    }
}
