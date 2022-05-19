package project.smartCard;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadClientInterface;
import com.sun.javacard.apduio.CadDevice;
import com.sun.javacard.apduio.CadTransportException;
import com.sun.javacard.jpcsclite.APDU;
import project.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class APDUUtils {
    private CadClientInterface cad;
    private String crefFilePath = "C:\\Program Files (x86)\\Oracle\\Java Card Development Kit Simulator 3.1.0\\bin\\cref.bat";

    public APDUUtils() {
        Process process;
        Socket sock;

        try {
            process = Runtime.getRuntime().exec(crefFilePath);

            sock = new Socket("localhost", 9025);
            InputStream is = sock.getInputStream();
            OutputStream os = sock.getOutputStream();
            cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T1, is, os);
        } catch (Exception ex) {
            System.out.println(ex);
            System.exit(1);
        }
    }

    public void powerUp() {
        try {
            cad.powerUp();
        } catch (CadTransportException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(1);
        }
    }

    public void powerDown() {
        try {
            cad.powerDown();
        } catch (CadTransportException | IOException ex) {
            System.out.println(ex);
            System.exit(1);
        }
    }

    private Apdu createApduCommand(byte[] bytes) {
        int length = bytes.length;

        byte[] dataIn = {};

        Apdu apdu = new Apdu();

        apdu.command[APDU.CLA] = bytes[0];
        apdu.command[APDU.INS] = bytes[1];
        apdu.command[APDU.P1] = bytes[2];
        apdu.command[APDU.P2] = bytes[3];

        int lc = (int) bytes[4];

        if (length > 6) {
            dataIn = new byte[length - 6];
            for (int i = 5; i < length - 1; i++)
                dataIn[i - 5] = bytes[i];
        }

        apdu.setDataIn(dataIn, lc);

        apdu.Le = bytes[length - 1];

        return apdu;
    }


    public byte[] runCommand(byte[] command, String message, boolean printMessage) {
        try {
            Apdu apdu = createApduCommand(command);

            if (printMessage) {
                System.out.println("\n-------------> " + message + " <-------------");
                System.out.println(apdu);
            }
            cad.exchangeApdu(apdu);

            if (printMessage)
                System.out.println(apdu);

            byte[] sw1sw2 = apdu.getSw1Sw2();

            if (sw1sw2[0] != Utils.getByteFromHexCode("0x90") || sw1sw2[1] != Utils.getByteFromHexCode("0x00"))
                return null;

            return apdu.getDataOut().clone();

        } catch (CadTransportException | IOException ex) {
            System.out.println(ex);
            System.exit(1);
        }

        return null;
    }

//    public void createWalletApplet() {
//        String command = "0x80 0xB8 0x00 0x00 0x14 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x06 0x01 0x08 0x00 0x00 0x05 0x01 0x02 0x03 0x04 0x05 0x7F;";
//
//        List<Byte> byteList = Utils.convertStringToByteList(command);
//        runCommand(Utils.getByteArrayFromList(byteList), "CREATE WALLET APPLET", true);
//    }
//
//    public void selectWallet() {
//        String command = "0x00 0xA4 0x04 0x00 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x06 0x01 0x7F";
//
//        List<Byte> byteList = Utils.convertStringToByteList(command);
//        runCommand(Utils.getByteArrayFromList(byteList), "SELECT WALLET", true);
//    }

    public void runCapWallet() {
        String capWalletFilePath = "C:\\Users\\vlada\\IdeaProjects\\StudentCard\\src\\main\\resources\\cap-wallet.txt";

        try {
            File file = new File(capWalletFilePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                List<Byte> byteList = Utils.convertStringToByteList(line);

                runCommand(Utils.getByteArrayFromList(byteList), "INSTALL", false);
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
