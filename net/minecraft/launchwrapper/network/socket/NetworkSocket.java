/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.launchwrapper.network.Srv.NetDispatcher;
import net.minecraft.launchwrapper.network.common.Common;
import net.minecraft.launchwrapper.network.common.GameState;
import net.minecraft.launchwrapper.network.protocol.ComponentEntity;
import net.minecraft.launchwrapper.network.socket.MessageQueue;
import net.minecraft.launchwrapper.network.socket.RecvThread;
import net.minecraft.launchwrapper.network.socket.SendThread;
import nivia.utils.Helper;

public class NetworkSocket {
    private static final String SEVER_IP = "127.0.0.1";
    public static Socket mSocket;
    private static int server_port;
    public static List<ComponentEntity> componentKeys;
    static DataInputStream inputStream;
    static DataOutputStream outputStream;
    static NetDispatcher netDispatcher;
    static MessageQueue msgQueue;

    public static void FakeInit() {
    }

    public static void init() {
      //  if (GameState.launcherport != 0) {
            server_port = Integer.parseInt(Helper.portx);
    //    }
        try {
            mSocket = new Socket("127.0.0.1", server_port);
            inputStream = new DataInputStream(mSocket.getInputStream());
            outputStream = new DataOutputStream(mSocket.getOutputStream());
            netDispatcher = new NetDispatcher();
            msgQueue = new MessageQueue();
            NetworkSocket.HandleMessage();
            if (Common.version != 0) {
                NetworkSocket.HandleMessage();
            }
            SendThread sendThread = new SendThread(msgQueue);
            RecvThread recvThread = new RecvThread(inputStream);
            Thread t1 = new Thread(sendThread);
            Thread t2 = new Thread(recvThread);
            t1.start();
            t2.start();
        }
        catch (IOException e) {
            Common.debug("socket connect fail");
            NetworkSocket.Close();
        }
    }

    public static void HandleMessage() {
        try {
            int length = inputStream.readUnsignedByte();
            Common.debug("length:" + (length += inputStream.readUnsignedByte() * 256));
            byte[] buff = new byte[length];
            for (int i = 0; i < length; ++i) {
                buff[i] = inputStream.readByte();
            }
            Common.onLog(buff);
            if (length < 2) {
                return;
            }
            Integer sid = Common.GetSidMid(buff);
            Common.debug("sid:" + sid);
            byte[] data = Arrays.copyOfRange(buff, 2, buff.length);
            netDispatcher.Dispatch(sid, data);
        }
        catch (Exception e) {
            Common.debug(e.toString());
        }
    }

    private static void Close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        }
        catch (Exception e) {
            Common.debug(e.toString());
        }
    }

    public static String GetModKey(long iid, int ver) {
        String key = "";
        for (ComponentEntity entity : componentKeys) {
            if (entity.iid != iid || entity.version != ver) continue;
            key = entity.key;
            break;
        }
        return key;
    }

    public static boolean isInEntities(String modname) {
        if (null == modname || null == componentKeys) {
            return true;
        }
        for (ComponentEntity entity : componentKeys) {
            if (null == componentKeys || null == entity.name || !entity.name.equals(modname)) continue;
            return true;
        }
        return false;
    }

    public static void Send(byte[] data) throws InterruptedException {
        msgQueue.Add(data);
    }

    static {
        server_port = 9876;
        componentKeys = new ArrayList<ComponentEntity>();
    }
}

