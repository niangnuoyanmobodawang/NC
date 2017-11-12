/*
 * Decompiled with CFR_Moded_ho3DeBug.
 */
package net.minecraft.launchwrapper.network.socket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();

    public void Add(byte[] data) throws InterruptedException {
        this.sendQueue.put(data);
    }

    public byte[] Get() throws InterruptedException {
        return this.sendQueue.take();
    }
}

