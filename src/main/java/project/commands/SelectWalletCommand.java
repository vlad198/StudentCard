package project.commands;

import project.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class SelectWalletCommand extends Command{
    private static final byte SELECT_WALLET_CLA = Utils.getByteFromHexCode("0x00");
    private static final byte SELECT_WALLET_INS = Utils.getByteFromHexCode("0xA4");
    private static final byte SELECT_WALLET_P1 = Utils.getByteFromHexCode("0x04");
    private static final byte SELECT_WALLET_P2 = Utils.getByteFromHexCode("0x00");
    private static final byte SELECT_WALLET_LE = Utils.getByteFromHexCode("0x7F");

    public SelectWalletCommand() {
        setMessage("SELECT WALLET APPLET");
    }

    public void selectWallet()
    {
        List<Byte> newCommand = new LinkedList<>();

        newCommand.add(SELECT_WALLET_CLA);
        newCommand.add(SELECT_WALLET_INS);
        newCommand.add(SELECT_WALLET_P1);
        newCommand.add(SELECT_WALLET_P2);

        String walletArg = "0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x06 0x01";
        byte[] args = Utils.getByteArrayFromList(Utils.convertStringToByteList(walletArg));

        newCommand.add((byte) args.length);
        for(byte value: args)
        {
            newCommand.add(value);
        }

        newCommand.add(SELECT_WALLET_LE);

        setCommand(Utils.getByteArrayFromList(newCommand));
    }
}
