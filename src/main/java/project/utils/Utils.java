package project.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static final String HEX_PREFIX = "0x";

    public static List<Byte> convertStringToByteList(String byteString) {
        byteString = byteString.replaceAll(";", "");

        String[] words = byteString.split(" ");

        return Arrays.stream(words).map(s -> getByteFromHexCode(s)).collect(Collectors.toList());
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte getByteFromHexCode(String hexCode) {
        try{
            return hexStringToByteArray(hexCode)[1];
        }catch (Exception err)
        {
            System.out.println("Invalid hexcode:" + hexCode);
            System.exit(1);
        }
        return 0;
    }

    public static byte[] getByteArrayFromList(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++)
            bytes[i] = byteList.get(i);
        return bytes;
    }

    public static byte[][] getByteArrayFromListOfByteArrays(List<byte[]> byteList) {
        byte[][] bytes = new byte[byteList.size()][4];
        for (int i = 0; i < byteList.size(); i++)
            bytes[i] = byteList.get(i);
        return bytes;
    }

    public static String toHexString(Integer value)
    {
        String hex = Integer.toHexString(value);

        String hexValue = HEX_PREFIX + (hex.length() == 1 ? "0" : "") + Integer.toHexString(value);

        return hexValue;
    }
}
