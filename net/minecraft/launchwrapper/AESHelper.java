/*
 * Decompiled with CFR_Moded_ho3DeBug.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 */
package net.minecraft.launchwrapper;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.launchwrapper.network.common.Common;
import net.minecraft.launchwrapper.network.common.LittleEndian;
import net.minecraft.launchwrapper.network.socket.NetworkSocket;
import org.apache.logging.log4j.Level;

public class AESHelper {
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final int MIN_CLASS_FILE_LEN = 32;
    private static final byte CLASS_FILE_FIRST_BYTE = -54;
    private static final int IV_START_INDEX = 4;
    private static final int IID_START_INDEX = 20;
    private static final int VERSTART_INDEX = 28;
    private static final int MD5START_INDEX = 32;

    public static boolean checkEncryted(byte[] data) {
        if (null == data) {
            return false;
        }
        if (data.length <= 32) {
            return false;
        }
        return data[0] != -54;
    }

    public static byte[] Decrypt(byte[] data) {
        if (null == data) {
            return data;
        }
        if (data.length <= 32) {
            return data;
        }
        if (data[0] == -54) {
            return data;
        }
        byte[] ivbyte = Arrays.copyOfRange(data, 4, 20);
        byte[] iidbyte = Arrays.copyOfRange(data, 20, 28);
        byte[] verbyte = Arrays.copyOfRange(data, 28, 32);
        String aeskey = null;
        try {
            int ver = ByteBuffer.wrap(verbyte).getInt();
            ver = LittleEndian.littleInt(ver);
            long iid = ByteBuffer.wrap(iidbyte, 0, 8).getLong();
            iid = LittleEndian.littleLong(iid);
            aeskey = NetworkSocket.GetModKey(iid, ver);
        }
        catch (Exception e) {
            LogWrapper.log(Level.ERROR, "Could not get mod key. detail:%s.", e.toString());
            return data;
        }
        byte[] keybyte = aeskey.getBytes();
        if (keybyte == null || keybyte.length != 16) {
            return data;
        }
        return AESHelper.Decrypt(Arrays.copyOfRange(data, 32, data.length), keybyte, ivbyte);
    }

    public static byte[] Decrypt(byte[] data, byte[] keybyte, byte[] ivbyte) {
        if (null == data) {
            return data;
        }
        SecretKeySpec key = new SecretKeySpec(keybyte, "AES");
        IvParameterSpec ivparameter = new IvParameterSpec(ivbyte);
        byte[] byte_decode = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(2, (Key)key, ivparameter);
            byte_decode = cipher.doFinal(data);
            Common.debug("[Decrypt] Done");
        }
        catch (Exception e) {
            LogWrapper.log(Level.ERROR, "Decryt failed! detail:%s.", e.toString());
            return data;
        }
        return byte_decode;
    }
}

