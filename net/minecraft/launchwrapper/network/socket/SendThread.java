/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.socket;

import net.minecraft.launchwrapper.network.common.Common;
import net.minecraft.launchwrapper.network.socket.MessageQueue;
import net.minecraft.launchwrapper.network.socket.NetworkSocket;

public class SendThread
implements Runnable {
    private MessageQueue queue;

    public SendThread(MessageQueue msgQueue) {
        this.queue = msgQueue;
    }

    @Override
    public void run() {
        try {
            do {
                byte[] data = this.queue.Get();
                NetworkSocket.Send(data);
            } while (true);
        }
        catch (InterruptedException ex) {
            Common.debug(ex.toString());
            return;
        }
    }
}

