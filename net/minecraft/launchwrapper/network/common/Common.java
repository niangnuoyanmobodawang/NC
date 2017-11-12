/*
 * Decompiled with CFR_Moded_ho3DeBug.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.apache.logging.log4j.Level
 */
package net.minecraft.launchwrapper.network.common;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.launchwrapper.network.common.GameState;
import net.minecraft.launchwrapper.network.protocol.LauncherPort;
import net.minecraft.launchwrapper.network.protocol.LauncherPortArray;
import org.apache.logging.log4j.Level;

public class Common {
    public static final String STRING_ENCODE = "ISO-8859-1";
    public static final boolean DEBUG_FLAG = false;
    public static final int SMIDLEN = 2;
    public static int version = 1;
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static void debug(String log) {
    	System.out.println("[ho3]"+log);
    }

    public static void LogWrapper(String log) {
        LogWrapper.log(Level.DEBUG, log, new Object[0]);
    }

    public static void GetLauncherPort(String[] args) {
        LauncherPort entity;
        Gson gson;
        LauncherPortArray array;
        String json = "";
        for (int idx = 0; idx < args.length; ++idx) {
            if (!args[idx].contains("userProperties")) continue;
            json = args[idx + 1];
            break;
        }
        if ((array = (LauncherPortArray)(gson = new Gson()).fromJson(json, LauncherPortArray.class)) != null && array.launcherport != null) {
            for (int idx = 0; idx < array.launcherport.length; ++idx) {
                if (array.launcherport[idx] == 0) continue;
                GameState.launcherport = array.launcherport[idx];
            }
        }
        if (GameState.launcherport == 0 && (entity = (LauncherPort)gson.fromJson(json, LauncherPort.class)) != null && entity.launcherport != 0) {
            GameState.launcherport = entity.launcherport;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static String getMd5ByFile(File file) throws Exception {
        String value = "";
        FileInputStream in = null;
        in = new FileInputStream(file);
        MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        MessageDigest md52222 = MessageDigest.getInstance("MD5");
        md52222.update(byteBuffer);
        BigInteger bi = new BigInteger(1, md52222.digest());
        value = bi.toString(16);
        if (null == in) return value;
        try {
            in.close();
            return value;
        }
        catch (IOException e) {
            LogWrapper.log(Level.ERROR, "Close %s Failed.Message:%s.", file.getName(), e.toString());
            return null;
        }
        catch (Exception e) {
            String md522221;
            try {
                LogWrapper.log(Level.ERROR, "Get %s Md5 Failed.Message:%s.", file.getName(), e.toString());
                md522221 = null;
                if (null == in) return md522221;
            }
            catch (Throwable throwable) {
                if (null == in) throw throwable;
                try {
                    in.close();
                    throw throwable;
                }
                catch (IOException e2) {
                    LogWrapper.log(Level.ERROR, "Close %s Failed.Message:%s.", file.getName(), e2.toString());
                    return null;
                }
            }
            try {
                in.close();
                return md522221;
            }
            catch (IOException e3) {
                LogWrapper.log(Level.ERROR, "Close %s Failed.Message:%s.", file.getName(), e3.toString());
                return null;
            }
        }
    }

    private static String BytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }
        return new String(hexChars);
    }

    public static void onLog(byte[] msg) {
        String log = Common.BytesToHex(msg);
        Common.debug(log);
    }

    public static int GetSidMid(byte[] msg) {
        if (msg.length < 2) {
            return -1;
        }
        return msg[0] * 256 + msg[1];
    }
}

