package aero.cubox.api.util;

import cubl.util.AES256Util;

public class CuboxTerminalUtil {

    private static String key = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";

    public static String byteArrEncode(byte[] bytes) throws Exception {
        AES256Util aes256Util = new AES256Util();
        String result =  aes256Util.byteArrEncode(bytes, key);
        return result;
    }

    public static byte[] byteArrDecode(String encoded) throws Exception {
        AES256Util aes256Util = new AES256Util();
        byte[] result =  aes256Util.byteArrDecode(encoded, key);
        return result;
    }

    public static byte[] floatArrayToByteArray(float[] floats)
    {
        byte[] result = new byte[floats.length * 4];

        for(int i=0;i<floats.length;i++)
        {
            int intBits =  Float.floatToIntBits(floats[i]);
            result[i * 4 + 3] = (byte) (intBits >> 24);
            result[i * 4 + 2] = (byte) (intBits >> 16);
            result[i * 4 + 1] = (byte) (intBits >> 8);
            result[i * 4 + 0] = (byte) (intBits);
        }

        return result;
    }

    public static float[] byteArrayToFloatArray(byte[] bytes)
    {
        float[] result = new float[bytes.length / 4];

        for(int i=0;i<result.length;i++)
        {
            int intBits = bytes[i * 4 + 3] << 24
                    | (bytes[i * 4 + 2] & 0xFF) << 16
                    | (bytes[i * 4 + 1] & 0xFF) << 8
                    | (bytes[i * 4 + 0] & 0xFF);

            result[i] = Float.intBitsToFloat(intBits);
        }

        return result;
    }
}
