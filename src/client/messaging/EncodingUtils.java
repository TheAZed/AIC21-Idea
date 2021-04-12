package client.messaging;

public final class EncodingUtils {
    public static String encodeInteger(int number) {
        return Integer.toBinaryString(number);
    }

    public static String encodeInteger(int number, int digitCount) {
        String baseStr = Integer.toBinaryString(number);
        String zeros = "0".repeat(digitCount - baseStr.length());
        return zeros + baseStr;
    }

    public static int extractInteger(StringBuilder data, int bitCount) {
        int output = decodeInteger(data.substring(0, bitCount));
        data.delete(0, bitCount);
        return output;
    }

    public static int decodeInteger(String numberBinaryStr) {
        return Integer.parseInt(numberBinaryStr, 2);
    }

    public static String encodeUuidString(String uuidStr) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < uuidStr.length(); i++) {
            int currentInt = Integer.parseInt(String.valueOf(uuidStr.charAt(i)), 16);
            builder.append(Integer.toBinaryString(currentInt));
        }
        return builder.toString();
    }

    public static String decodeUuidString(String uuidBinaryStr) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            String hexStr = Integer.toHexString(Integer.parseInt(uuidBinaryStr.substring(i, i + 4), 2));
            builder.append(hexStr);
        }
        return builder.toString();
    }

    public static int getIntegerBitCount(int number) {
        return Integer.toBinaryString(number).length();
    }

    public static String encodeBinaryToText(String binaryString, int textLength) {
        StringBuilder inputBuilder = new StringBuilder(binaryString);
        StringBuilder outputBuilder = new StringBuilder();
        while (inputBuilder.length() > 8) {
            String  nextChar = Character.toString((char) Integer.parseInt(inputBuilder.substring(0, 8), 2));
            outputBuilder.append(nextChar);
            inputBuilder.delete(0, 8);
            System.out.println(outputBuilder + "$$" + inputBuilder);
        }
        inputBuilder.append("0".repeat(8 - inputBuilder.length()));
        char finalChar = (char) Integer.parseInt(inputBuilder.toString(), 2);
        outputBuilder.append(finalChar);
        outputBuilder.append(String.valueOf((char) 0).repeat(Math.max(0, textLength - outputBuilder.length())));
        return outputBuilder.toString();
    }
}
