/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.socket;

import java.io.DataInputStream;
import java.io.IOException;
import net.minecraft.launchwrapper.network.common.Common;
import net.minecraft.launchwrapper.network.socket.NetworkSocket;

public class RecvThread
implements Runnable {
    private DataInputStream inputStream;

    public RecvThread(DataInputStream input) {
        this.inputStream = input;
    }

    @Override
    public void run() {
        try {
            do {
                if (this.HasMessage() > 0) {
                    NetworkSocket.HandleMessage();
                    continue;
                }
                Thread.sleep(100);
            } while (true);
        }
        catch (Exception ex) {
            Common.debug(ex.toString());
            return;
        }
    }

    public int HasMessage() throws IOException {
        return this.inputStream.available();
    }
}

